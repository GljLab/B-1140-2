package com.example.picture.service;

import com.example.picture.dto.CollageCreateRequest;
import com.example.picture.dto.CollageDTO;
import com.example.picture.dto.CollageUpdateRequest;
import com.example.picture.entity.Collage;
import com.example.picture.entity.Picture;
import com.example.picture.repository.CollageRepository;
import com.example.picture.repository.PictureRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollageService {

    @Autowired
    private CollageRepository collageRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Value("${upload.path:/app/images/}")
    private String uploadPath;

    private ObjectMapper objectMapper = new ObjectMapper();

    public List<CollageDTO> listCollages(Long userId) {
        return collageRepository.findByUserIdOrderByIdDesc(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CollageDTO getCollage(Long id, Long userId) {
        Collage collage = collageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("拼图不存在"));
        if (!collage.getUserId().equals(userId)) {
            throw new RuntimeException("无权限访问此拼图");
        }
        return toDTO(collage);
    }

    @Transactional
    public CollageDTO createCollage(Long userId, CollageCreateRequest request) {
        Collage collage = new Collage();
        collage.setUserId(userId);
        collage.setName(request.getName());
        collage.setTemplateType(request.getTemplateType() != null ? request.getTemplateType() : "grid2");
        collage.setCanvasWidth(request.getCanvasWidth() != null ? request.getCanvasWidth() : 1080);
        collage.setCanvasHeight(request.getCanvasHeight() != null ? request.getCanvasHeight() : 1080);
        collage.setBackgroundColor(request.getBackgroundColor() != null ? request.getBackgroundColor() : "#FFFFFF");
        collage.setBackgroundImageUrl(request.getBackgroundImageUrl());
        collage.setGap(request.getGap() != null ? request.getGap() : 10);
        collage.setBorderRadius(request.getBorderRadius() != null ? request.getBorderRadius() : 0);
        collage.setBorderWidth(request.getBorderWidth() != null ? request.getBorderWidth() : 0);
        collage.setBorderColor(request.getBorderColor() != null ? request.getBorderColor() : "#000000");
        collage.setSizePreset(request.getSizePreset());
        collage.setQuality(request.getQuality() != null ? request.getQuality() : "high");

        if (request.getItems() != null) {
            collage.setItems(request.getItems());
        } else if (request.getPictureIds() != null && !request.getPictureIds().isEmpty()) {
            collage.setItems(buildItemsFromPictureIds(request.getPictureIds(), userId, collage.getTemplateType()));
        }

        if (request.getTexts() != null) {
            collage.setTexts(request.getTexts());
        }
        if (request.getStickers() != null) {
            collage.setStickers(request.getStickers());
        }

        Collage saved = collageRepository.save(collage);
        return toDTO(saved);
    }

    @Transactional
    public CollageDTO updateCollage(Long id, Long userId, CollageUpdateRequest request) {
        Collage collage = collageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("拼图不存在"));
        if (!collage.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此拼图");
        }

        if (request.getName() != null) collage.setName(request.getName());
        if (request.getTemplateType() != null) collage.setTemplateType(request.getTemplateType());
        if (request.getCanvasWidth() != null) collage.setCanvasWidth(request.getCanvasWidth());
        if (request.getCanvasHeight() != null) collage.setCanvasHeight(request.getCanvasHeight());
        if (request.getBackgroundColor() != null) collage.setBackgroundColor(request.getBackgroundColor());
        if (request.getBackgroundImageUrl() != null) collage.setBackgroundImageUrl(request.getBackgroundImageUrl());
        if (request.getGap() != null) collage.setGap(request.getGap());
        if (request.getBorderRadius() != null) collage.setBorderRadius(request.getBorderRadius());
        if (request.getBorderWidth() != null) collage.setBorderWidth(request.getBorderWidth());
        if (request.getBorderColor() != null) collage.setBorderColor(request.getBorderColor());
        if (request.getSizePreset() != null) collage.setSizePreset(request.getSizePreset());
        if (request.getQuality() != null) collage.setQuality(request.getQuality());
        if (request.getTexts() != null) collage.setTexts(request.getTexts());
        if (request.getStickers() != null) collage.setStickers(request.getStickers());

        if (request.getItems() != null) {
            collage.setItems(request.getItems());
        } else if (request.getPictureIds() != null && !request.getPictureIds().isEmpty()) {
            collage.setItems(buildItemsFromPictureIds(request.getPictureIds(), userId, collage.getTemplateType()));
        }

        Collage saved = collageRepository.save(collage);
        return toDTO(saved);
    }

    @Transactional
    public void deleteCollage(Long id, Long userId) {
        Collage collage = collageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("拼图不存在"));
        if (!collage.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此拼图");
        }
        collageRepository.delete(collage);
    }

    @Transactional
    public String generateCollage(Long id, Long userId) {
        Collage collage = collageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("拼图不存在"));
        if (!collage.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此拼图");
        }

        int width = collage.getCanvasWidth() != null ? collage.getCanvasWidth() : 1080;
        int height = collage.getCanvasHeight() != null ? collage.getCanvasHeight() : 1080;
        int gap = collage.getGap() != null ? collage.getGap() : 10;
        int borderRadius = collage.getBorderRadius() != null ? collage.getBorderRadius() : 0;
        int borderWidth = collage.getBorderWidth() != null ? collage.getBorderWidth() : 0;
        String borderColor = collage.getBorderColor() != null ? collage.getBorderColor() : "#000000";
        String bgColor = collage.getBackgroundColor() != null ? collage.getBackgroundColor() : "#FFFFFF";

        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = canvas.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setColor(parseColor(bgColor));
        g2d.fillRect(0, 0, width, height);

        if (collage.getBackgroundImageUrl() != null && !collage.getBackgroundImageUrl().isEmpty()) {
            BufferedImage bgImage = loadImage(collage.getBackgroundImageUrl());
            if (bgImage != null) {
                g2d.drawImage(bgImage, 0, 0, width, height, null);
            }
        }

        String templateType = collage.getTemplateType() != null ? collage.getTemplateType() : "grid2";

        if (collage.getItems() != null && !collage.getItems().isEmpty()) {
            try {
                List<Map<String, Object>> items = objectMapper.readValue(collage.getItems(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));

                switch (templateType) {
                    case "grid2":
                        renderGridCollage(g2d, items, width, height, 2, gap, borderRadius, borderWidth, borderColor);
                        break;
                    case "grid4":
                        renderGridCollage(g2d, items, width, height, 4, gap, borderRadius, borderWidth, borderColor);
                        break;
                    case "grid6":
                        renderGridCollage(g2d, items, width, height, 6, gap, borderRadius, borderWidth, borderColor);
                        break;
                    case "grid9":
                        renderGridCollage(g2d, items, width, height, 9, gap, borderRadius, borderWidth, borderColor);
                        break;
                    case "vertical":
                        renderVerticalCollage(g2d, items, width, height, gap, borderRadius, borderWidth, borderColor);
                        break;
                    case "horizontal":
                        renderHorizontalCollage(g2d, items, width, height, gap, borderRadius, borderWidth, borderColor);
                        break;
                    case "free":
                        renderFreeCollage(g2d, items, borderRadius, borderWidth, borderColor);
                        break;
                    case "poster_top":
                    case "poster_magazine":
                        renderPosterCollage(g2d, items, width, height, templateType, gap, borderRadius, borderWidth, borderColor);
                        break;
                    default:
                        renderGridCollage(g2d, items, width, height, 2, gap, borderRadius, borderWidth, borderColor);
                        break;
                }
            } catch (Exception e) {
                throw new RuntimeException("解析拼图项目失败: " + e.getMessage());
            }
        }

        drawTexts(g2d, collage.getTexts());
        drawStickers(g2d, collage.getStickers());

        g2d.dispose();

        String fileName = UUID.randomUUID().toString() + ".png";
        File outputFile = new File(uploadPath + fileName);
        try {
            ImageIO.write(canvas, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException("保存拼图图片失败: " + e.getMessage());
        }

        String url = "/images/" + fileName;
        collage.setUrl(url);
        collageRepository.save(collage);

        return url;
    }

    private String buildItemsFromPictureIds(List<Long> pictureIds, Long userId, String templateType) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (Long pid : pictureIds) {
            Picture picture = pictureRepository.findById(pid).orElse(null);
            if (picture == null || !picture.getUserId().equals(userId) || Boolean.TRUE.equals(picture.getDeleted())) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("pictureId", pid);
            item.put("url", picture.getUrl());
            items.add(item);
        }

        try {
            return objectMapper.writeValueAsString(items);
        } catch (Exception e) {
            return "[]";
        }
    }

    private void renderGridCollage(Graphics2D g2d, List<Map<String, Object>> items, int canvasWidth, int canvasHeight,
                                   int gridCount, int gap, int borderRadius, int borderWidth, String borderColor) {
        int cols, rows;
        if (gridCount == 2) { cols = 2; rows = 1; }
        else if (gridCount == 4) { cols = 2; rows = 2; }
        else if (gridCount == 6) { cols = 3; rows = 2; }
        else if (gridCount == 9) { cols = 3; rows = 3; }
        else { cols = 2; rows = 1; }

        int cellWidth = (canvasWidth - gap * (cols + 1)) / cols;
        int cellHeight = (canvasHeight - gap * (rows + 1)) / rows;

        for (int i = 0; i < items.size() && i < gridCount; i++) {
            int col = i % cols;
            int row = i / cols;
            int x = gap + col * (cellWidth + gap);
            int y = gap + row * (cellHeight + gap);

            Map<String, Object> item = items.get(i);
            String url = item.get("url") != null ? item.get("url").toString() : null;
            if (url == null) continue;

            Integer itemX = item.get("x") != null ? ((Number) item.get("x")).intValue() : null;
            Integer itemY = item.get("y") != null ? ((Number) item.get("y")).intValue() : null;
            Integer itemW = item.get("width") != null ? ((Number) item.get("width")).intValue() : null;
            Integer itemH = item.get("height") != null ? ((Number) item.get("height")).intValue() : null;

            int drawX = itemX != null ? itemX : x;
            int drawY = itemY != null ? itemY : y;
            int drawW = itemW != null ? itemW : cellWidth;
            int drawH = itemH != null ? itemH : cellHeight;

            BufferedImage image = loadImage(url);
            if (image != null) {
                drawRoundedImage(g2d, image, drawX, drawY, drawW, drawH, borderRadius, borderWidth, borderColor);
            }
        }
    }

    private void renderVerticalCollage(Graphics2D g2d, List<Map<String, Object>> items, int canvasWidth, int canvasHeight,
                                       int gap, int borderRadius, int borderWidth, String borderColor) {
        int count = items.size();
        if (count == 0) return;

        int cellHeight = (canvasHeight - gap * (count + 1)) / count;
        int cellWidth = canvasWidth - gap * 2;

        for (int i = 0; i < count; i++) {
            Map<String, Object> item = items.get(i);
            String url = item.get("url") != null ? item.get("url").toString() : null;
            if (url == null) continue;

            Integer itemX = item.get("x") != null ? ((Number) item.get("x")).intValue() : null;
            Integer itemY = item.get("y") != null ? ((Number) item.get("y")).intValue() : null;
            Integer itemW = item.get("width") != null ? ((Number) item.get("width")).intValue() : null;
            Integer itemH = item.get("height") != null ? ((Number) item.get("height")).intValue() : null;

            int drawX = itemX != null ? itemX : gap;
            int drawY = itemY != null ? itemY : gap + i * (cellHeight + gap);
            int drawW = itemW != null ? itemW : cellWidth;
            int drawH = itemH != null ? itemH : cellHeight;

            BufferedImage image = loadImage(url);
            if (image != null) {
                drawRoundedImage(g2d, image, drawX, drawY, drawW, drawH, borderRadius, borderWidth, borderColor);
            }
        }
    }

    private void renderHorizontalCollage(Graphics2D g2d, List<Map<String, Object>> items, int canvasWidth, int canvasHeight,
                                          int gap, int borderRadius, int borderWidth, String borderColor) {
        int count = items.size();
        if (count == 0) return;

        int cellWidth = (canvasWidth - gap * (count + 1)) / count;
        int cellHeight = canvasHeight - gap * 2;

        for (int i = 0; i < count; i++) {
            Map<String, Object> item = items.get(i);
            String url = item.get("url") != null ? item.get("url").toString() : null;
            if (url == null) continue;

            Integer itemX = item.get("x") != null ? ((Number) item.get("x")).intValue() : null;
            Integer itemY = item.get("y") != null ? ((Number) item.get("y")).intValue() : null;
            Integer itemW = item.get("width") != null ? ((Number) item.get("width")).intValue() : null;
            Integer itemH = item.get("height") != null ? ((Number) item.get("height")).intValue() : null;

            int drawX = itemX != null ? itemX : gap + i * (cellWidth + gap);
            int drawY = itemY != null ? itemY : gap;
            int drawW = itemW != null ? itemW : cellWidth;
            int drawH = itemH != null ? itemH : cellHeight;

            BufferedImage image = loadImage(url);
            if (image != null) {
                drawRoundedImage(g2d, image, drawX, drawY, drawW, drawH, borderRadius, borderWidth, borderColor);
            }
        }
    }

    private void renderFreeCollage(Graphics2D g2d, List<Map<String, Object>> items, int borderRadius, int borderWidth, String borderColor) {
        for (Map<String, Object> item : items) {
            String url = item.get("url") != null ? item.get("url").toString() : null;
            if (url == null) continue;

            int x = item.get("x") != null ? ((Number) item.get("x")).intValue() : 0;
            int y = item.get("y") != null ? ((Number) item.get("y")).intValue() : 0;
            int w = item.get("width") != null ? ((Number) item.get("width")).intValue() : 200;
            int h = item.get("height") != null ? ((Number) item.get("height")).intValue() : 200;

            BufferedImage image = loadImage(url);
            if (image != null) {
                drawRoundedImage(g2d, image, x, y, w, h, borderRadius, borderWidth, borderColor);
            }
        }
    }

    private void renderPosterCollage(Graphics2D g2d, List<Map<String, Object>> items, int canvasWidth, int canvasHeight,
                                     String templateType, int gap, int borderRadius, int borderWidth, String borderColor) {
        if ("poster_top".equals(templateType)) {
            if (items.size() > 0) {
                Map<String, Object> mainItem = items.get(0);
                String url = mainItem.get("url") != null ? mainItem.get("url").toString() : null;
                if (url != null) {
                    int mainH = (int) (canvasHeight * 0.7);
                    BufferedImage image = loadImage(url);
                    if (image != null) {
                        drawRoundedImage(g2d, image, gap, gap, canvasWidth - gap * 2, mainH - gap, borderRadius, borderWidth, borderColor);
                    }
                }
            }
            int bottomY = (int) (canvasHeight * 0.7);
            int bottomH = (int) (canvasHeight * 0.3) - gap;
            int count = Math.min(items.size() - 1, 3);
            for (int i = 0; i < count; i++) {
                Map<String, Object> item = items.get(i + 1);
                String url = item.get("url") != null ? item.get("url").toString() : null;
                if (url == null) continue;
                int cellW = (canvasWidth - gap * (count + 1)) / count;
                int x = gap + i * (cellW + gap);
                BufferedImage image = loadImage(url);
                if (image != null) {
                    drawRoundedImage(g2d, image, x, bottomY, cellW, bottomH, borderRadius, borderWidth, borderColor);
                }
            }
        } else {
            if (items.size() > 0) {
                Map<String, Object> mainItem = items.get(0);
                String url = mainItem.get("url") != null ? mainItem.get("url").toString() : null;
                if (url != null) {
                    int mainW = (int) (canvasWidth * 0.6);
                    BufferedImage image = loadImage(url);
                    if (image != null) {
                        drawRoundedImage(g2d, image, gap, gap, mainW - gap, canvasHeight - gap * 2, borderRadius, borderWidth, borderColor);
                    }
                }
            }
            int rightX = (int) (canvasWidth * 0.6);
            int rightW = (int) (canvasWidth * 0.4) - gap;
            int count = Math.min(items.size() - 1, 3);
            for (int i = 0; i < count; i++) {
                Map<String, Object> item = items.get(i + 1);
                String url = item.get("url") != null ? item.get("url").toString() : null;
                if (url == null) continue;
                int cellH = (canvasHeight - gap * (count + 1)) / count;
                int y = gap + i * (cellH + gap);
                BufferedImage image = loadImage(url);
                if (image != null) {
                    drawRoundedImage(g2d, image, rightX, y, rightW, cellH, borderRadius, borderWidth, borderColor);
                }
            }
        }
    }

    private void drawRoundedImage(Graphics2D g2d, BufferedImage image, int x, int y, int width, int height,
                                  int borderRadius, int borderWidth, String borderColor) {
        BufferedImage scaled = scaleImage(image, width, height);

        if (borderRadius > 0) {
            BufferedImage roundedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D rg2d = roundedImage.createGraphics();
            rg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            rg2d.setClip(new RoundRectangle2D.Float(0, 0, width, height, borderRadius, borderRadius));
            rg2d.drawImage(scaled, 0, 0, null);
            rg2d.dispose();
            g2d.drawImage(roundedImage, x, y, null);
        } else {
            g2d.drawImage(scaled, x, y, null);
        }

        if (borderWidth > 0) {
            g2d.setColor(parseColor(borderColor));
            g2d.setStroke(new BasicStroke(borderWidth));
            if (borderRadius > 0) {
                g2d.draw(new RoundRectangle2D.Float(x, y, width, height, borderRadius, borderRadius));
            } else {
                g2d.drawRect(x, y, width, height);
            }
        }
    }

    private void drawTexts(Graphics2D g2d, String textsJson) {
        if (textsJson == null || textsJson.isEmpty()) return;
        try {
            List<Map<String, Object>> texts = objectMapper.readValue(textsJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            for (Map<String, Object> text : texts) {
                String content = text.get("content") != null ? text.get("content").toString() : "";
                if (content.isEmpty()) continue;

                int x = text.get("x") != null ? ((Number) text.get("x")).intValue() : 0;
                int y = text.get("y") != null ? ((Number) text.get("y")).intValue() : 0;
                int fontSize = text.get("fontSize") != null ? ((Number) text.get("fontSize")).intValue() : 24;
                String fontFamily = text.get("fontFamily") != null ? text.get("fontFamily").toString() : "SansSerif";
                String color = text.get("color") != null ? text.get("color").toString() : "#000000";
                Double opacity = text.get("opacity") != null ? ((Number) text.get("opacity")).doubleValue() : 1.0;
                Boolean bold = text.get("bold") != null ? (Boolean) text.get("bold") : false;
                Boolean italic = text.get("italic") != null ? (Boolean) text.get("italic") : false;

                int style = Font.PLAIN;
                if (bold) style |= Font.BOLD;
                if (italic) style |= Font.ITALIC;

                Font font = new Font(fontFamily, style, fontSize);
                g2d.setFont(font);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                Color c = parseColor(color);
                g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (opacity * 255)));
                g2d.drawString(content, x, y + fontSize);
            }
        } catch (Exception e) {
        }
    }

    private void drawStickers(Graphics2D g2d, String stickersJson) {
        if (stickersJson == null || stickersJson.isEmpty()) return;
        try {
            List<Map<String, Object>> stickers = objectMapper.readValue(stickersJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            for (Map<String, Object> sticker : stickers) {
                String url = sticker.get("url") != null ? sticker.get("url").toString() : null;
                if (url == null) continue;

                int x = sticker.get("x") != null ? ((Number) sticker.get("x")).intValue() : 0;
                int y = sticker.get("y") != null ? ((Number) sticker.get("y")).intValue() : 0;
                int w = sticker.get("width") != null ? ((Number) sticker.get("width")).intValue() : 100;
                int h = sticker.get("height") != null ? ((Number) sticker.get("height")).intValue() : 100;
                Double opacity = sticker.get("opacity") != null ? ((Number) sticker.get("opacity")).doubleValue() : 1.0;
                Double rotation = sticker.get("rotation") != null ? ((Number) sticker.get("rotation")).doubleValue() : 0.0;

                BufferedImage image = loadImage(url);
                if (image != null) {
                    BufferedImage scaled = scaleImage(image, w, h);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity.floatValue()));
                    if (rotation != 0.0) {
                        g2d.rotate(Math.toRadians(rotation), x + w / 2.0, y + h / 2.0);
                    }
                    g2d.drawImage(scaled, x, y, null);
                    if (rotation != 0.0) {
                        g2d.rotate(-Math.toRadians(rotation), x + w / 2.0, y + h / 2.0);
                    }
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
            }
        } catch (Exception e) {
        }
    }

    private BufferedImage loadImage(String url) {
        if (url == null || url.isEmpty()) return null;
        try {
            String imagePath = uploadPath + url.replace("/images/", "");
            File file = new File(imagePath);
            if (file.exists()) {
                return ImageIO.read(file);
            }
        } catch (Exception e) {
        }
        return null;
    }

    private BufferedImage scaleImage(BufferedImage source, int targetWidth, int targetHeight) {
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int srcW = source.getWidth();
        int srcH = source.getHeight();
        double scaleW = (double) targetWidth / srcW;
        double scaleH = (double) targetHeight / srcH;
        double scale = Math.max(scaleW, scaleH);

        int drawW = (int) (srcW * scale);
        int drawH = (int) (srcH * scale);
        int drawX = (targetWidth - drawW) / 2;
        int drawY = (targetHeight - drawH) / 2;

        g.drawImage(source, drawX, drawY, drawW, drawH, null);
        g.dispose();
        return scaled;
    }

    private Color parseColor(String colorStr) {
        try {
            if (colorStr.startsWith("#")) {
                colorStr = colorStr.substring(1);
            }
            if (colorStr.length() == 6) {
                int r = Integer.parseInt(colorStr.substring(0, 2), 16);
                int g = Integer.parseInt(colorStr.substring(2, 4), 16);
                int b = Integer.parseInt(colorStr.substring(4, 6), 16);
                return new Color(r, g, b);
            }
        } catch (NumberFormatException e) {
        }
        return Color.WHITE;
    }

    private CollageDTO toDTO(Collage collage) {
        CollageDTO dto = new CollageDTO();
        dto.setId(collage.getId());
        dto.setUserId(collage.getUserId());
        dto.setName(collage.getName());
        dto.setUrl(collage.getUrl());
        dto.setTemplateType(collage.getTemplateType());
        dto.setCanvasWidth(collage.getCanvasWidth());
        dto.setCanvasHeight(collage.getCanvasHeight());
        dto.setBackgroundColor(collage.getBackgroundColor());
        dto.setBackgroundImageUrl(collage.getBackgroundImageUrl());
        dto.setGap(collage.getGap());
        dto.setBorderRadius(collage.getBorderRadius());
        dto.setBorderWidth(collage.getBorderWidth());
        dto.setBorderColor(collage.getBorderColor());
        dto.setItems(collage.getItems());
        dto.setTexts(collage.getTexts());
        dto.setStickers(collage.getStickers());
        dto.setSizePreset(collage.getSizePreset());
        dto.setQuality(collage.getQuality());
        dto.setCreateTime(collage.getCreateTime());
        dto.setUpdateTime(collage.getUpdateTime());
        return dto;
    }
}
