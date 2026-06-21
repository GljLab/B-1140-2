package com.example.picture.util;

import com.example.picture.entity.WatermarkConfig;
import com.example.picture.entity.WatermarkTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class WatermarkUtil {

    public static final String POSITION_TOP_LEFT = "top_left";
    public static final String POSITION_TOP_RIGHT = "top_right";
    public static final String POSITION_BOTTOM_LEFT = "bottom_left";
    public static final String POSITION_BOTTOM_RIGHT = "bottom_right";
    public static final String POSITION_CENTER = "center";
    public static final String POSITION_TILE = "tile";

    public static final String TYPE_TEXT = "text";
    public static final String TYPE_IMAGE = "image";

    public static void addWatermark(File sourceFile, File targetFile, WatermarkConfig config, String baseUploadPath) throws IOException {
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        if (sourceImage == null) {
            throw new IOException("无法读取图片文件");
        }

        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();

        BufferedImage watermarkedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = watermarkedImage.createGraphics();
        g2d.drawImage(sourceImage, 0, 0, null);

        boolean smartMode = Boolean.TRUE.equals(config.getSmartMode());

        if (TYPE_TEXT.equals(config.getType())) {
            applyTextWatermark(g2d, sourceImage, config, smartMode);
        } else if (TYPE_IMAGE.equals(config.getType())) {
            applyImageWatermark(g2d, sourceImage, config, baseUploadPath, smartMode);
        }

        g2d.dispose();

        String format = getFileFormat(sourceFile.getName());
        ImageIO.write(watermarkedImage, format != null ? format : "png", targetFile);
    }

    public static void addWatermark(File sourceFile, File targetFile, WatermarkTemplate template, String baseUploadPath) throws IOException {
        WatermarkConfig config = new WatermarkConfig();
        config.setType(template.getType());
        config.setTextContent(template.getTextContent());
        config.setFontFamily(template.getFontFamily());
        config.setFontSize(template.getFontSize());
        config.setFontColor(template.getFontColor());
        config.setOpacity(template.getOpacity());
        config.setImageUrl(template.getImageUrl());
        config.setImageWidth(template.getImageWidth());
        config.setImageHeight(template.getImageHeight());
        config.setPosition(template.getPosition());
        config.setMarginX(template.getMarginX());
        config.setMarginY(template.getMarginY());
        config.setTileGap(template.getTileGap());
        config.setSmartMode(template.getSmartMode());
        addWatermark(sourceFile, targetFile, config, baseUploadPath);
    }

    private static void applyTextWatermark(Graphics2D g2d, BufferedImage sourceImage, WatermarkConfig config, boolean smartMode) {
        String text = config.getTextContent();
        if (text == null || text.isEmpty()) {
            return;
        }

        String fontFamily = config.getFontFamily() != null ? config.getFontFamily() : "SansSerif";
        int fontSize = config.getFontSize() != null ? config.getFontSize() : 24;
        String fontColor = config.getFontColor() != null ? config.getFontColor() : "#FFFFFF";
        float opacity = config.getOpacity() != null ? config.getOpacity().floatValue() : 0.5f;

        Font font = new Font(fontFamily, Font.PLAIN, fontSize);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics(font);
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        int marginX = config.getMarginX() != null ? config.getMarginX() : 20;
        int marginY = config.getMarginY() != null ? config.getMarginY() : 20;

        String position = config.getPosition() != null ? config.getPosition() : POSITION_BOTTOM_RIGHT;

        if (smartMode) {
            fontColor = getAdaptiveTextColor(sourceImage, position, marginX, marginY, textWidth, textHeight);
        }

        Color color = parseColor(fontColor);
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (opacity * 255)));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (POSITION_TILE.equals(position)) {
            double gap = config.getTileGap() != null ? config.getTileGap() : 100.0;
            for (int y = marginY; y < sourceImage.getHeight(); y += (int) (textHeight + gap)) {
                for (int x = marginX; x < sourceImage.getWidth(); x += (int) (textWidth + gap)) {
                    g2d.drawString(text, x, y + textHeight);
                }
            }
        } else {
            Point pos = calculatePosition(position, sourceImage.getWidth(), sourceImage.getHeight(), textWidth, textHeight, marginX, marginY);
            g2d.drawString(text, pos.x, pos.y);
        }
    }

    private static void applyImageWatermark(Graphics2D g2d, BufferedImage sourceImage, WatermarkConfig config, String baseUploadPath, boolean smartMode) {
        String imageUrl = config.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        String imagePath = baseUploadPath + imageUrl.replace("/images/", "");
        File watermarkFile = new File(imagePath);
        if (!watermarkFile.exists()) {
            return;
        }

        try {
            BufferedImage watermarkImage = ImageIO.read(watermarkFile);
            if (watermarkImage == null) {
                return;
            }

            int wmWidth = config.getImageWidth() != null ? config.getImageWidth() : 100;
            int wmHeight = config.getImageHeight() != null ? config.getImageHeight() : 100;
            float opacity = config.getOpacity() != null ? config.getOpacity().floatValue() : 0.5f;

            int marginX = config.getMarginX() != null ? config.getMarginX() : 20;
            int marginY = config.getMarginY() != null ? config.getMarginY() : 20;
            String position = config.getPosition() != null ? config.getPosition() : POSITION_BOTTOM_RIGHT;

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            if (POSITION_TILE.equals(position)) {
                double gap = config.getTileGap() != null ? config.getTileGap() : 50.0;
                for (int y = marginY; y < sourceImage.getHeight(); y += (int) (wmHeight + gap)) {
                    for (int x = marginX; x < sourceImage.getWidth(); x += (int) (wmWidth + gap)) {
                        g2d.drawImage(watermarkImage, x, y, wmWidth, wmHeight, null);
                    }
                }
            } else {
                Point pos = calculatePosition(position, sourceImage.getWidth(), sourceImage.getHeight(), wmWidth, wmHeight, marginX, marginY);
                g2d.drawImage(watermarkImage, pos.x, pos.y, wmWidth, wmHeight, null);
            }

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        } catch (IOException e) {
        }
    }

    private static Point calculatePosition(String position, int imgWidth, int imgHeight, int wmWidth, int wmHeight, int marginX, int marginY) {
        int x, y;
        switch (position) {
            case POSITION_TOP_LEFT:
                x = marginX;
                y = marginY + wmHeight;
                break;
            case POSITION_TOP_RIGHT:
                x = imgWidth - wmWidth - marginX;
                y = marginY + wmHeight;
                break;
            case POSITION_BOTTOM_LEFT:
                x = marginX;
                y = imgHeight - marginY;
                break;
            case POSITION_CENTER:
                x = (imgWidth - wmWidth) / 2;
                y = (imgHeight + wmHeight) / 2;
                break;
            case POSITION_BOTTOM_RIGHT:
            default:
                x = imgWidth - wmWidth - marginX;
                y = imgHeight - marginY;
                break;
        }
        return new Point(x, y);
    }

    private static String getAdaptiveTextColor(BufferedImage image, String position, int marginX, int marginY, int textWidth, int textHeight) {
        int w = image.getWidth();
        int h = image.getHeight();

        int sampleX = marginX;
        int sampleY = marginY;

        switch (position) {
            case POSITION_TOP_RIGHT:
                sampleX = w - textWidth - marginX;
                sampleY = marginY;
                break;
            case POSITION_BOTTOM_LEFT:
                sampleX = marginX;
                sampleY = h - textHeight - marginY;
                break;
            case POSITION_CENTER:
                sampleX = (w - textWidth) / 2;
                sampleY = (h - textHeight) / 2;
                break;
            case POSITION_BOTTOM_RIGHT:
            default:
                sampleX = w - textWidth - marginX;
                sampleY = h - textHeight - marginY;
                break;
        }

        sampleX = Math.max(0, Math.min(sampleX, w - 1));
        sampleY = Math.max(0, Math.min(sampleY, h - 1));
        int sampleW = Math.min(textWidth, w - sampleX);
        int sampleH = Math.min(textHeight, h - sampleY);

        if (sampleW <= 0 || sampleH <= 0) {
            return "#FFFFFF";
        }

        long totalR = 0, totalG = 0, totalB = 0;
        int count = 0;
        int step = Math.max(1, Math.min(sampleW, sampleH) / 10);

        for (int y = sampleY; y < sampleY + sampleH; y += step) {
            for (int x = sampleX; x < sampleX + sampleW; x += step) {
                int rgb = image.getRGB(x, y);
                totalR += (rgb >> 16) & 0xFF;
                totalG += (rgb >> 8) & 0xFF;
                totalB += rgb & 0xFF;
                count++;
            }
        }

        if (count == 0) return "#FFFFFF";

        int avgR = (int) (totalR / count);
        int avgG = (int) (totalG / count);
        int avgB = (int) (totalB / count);

        double brightness = (avgR * 299 + avgG * 587 + avgB * 114) / 1000.0;

        return brightness > 128 ? "#000000" : "#FFFFFF";
    }

    private static Color parseColor(String colorStr) {
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

    private static String getFileFormat(String fileName) {
        if (fileName == null) return null;
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return null;
    }
}
