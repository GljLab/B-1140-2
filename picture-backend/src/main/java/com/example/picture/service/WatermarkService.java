package com.example.picture.service;

import com.example.picture.dto.WatermarkConfigDTO;
import com.example.picture.dto.WatermarkConfigSaveRequest;
import com.example.picture.dto.WatermarkTemplateDTO;
import com.example.picture.dto.WatermarkTemplateSaveRequest;
import com.example.picture.entity.User;
import com.example.picture.entity.WatermarkConfig;
import com.example.picture.entity.WatermarkTemplate;
import com.example.picture.repository.UserRepository;
import com.example.picture.repository.WatermarkConfigRepository;
import com.example.picture.repository.WatermarkTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WatermarkService {

    @Autowired
    private WatermarkConfigRepository watermarkConfigRepository;

    @Autowired
    private WatermarkTemplateRepository watermarkTemplateRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    @Transactional
    public void initSystemTemplates() {
        List<WatermarkTemplate> systemTemplates = watermarkTemplateRepository.findByIsSystemTrue();
        if (systemTemplates != null && !systemTemplates.isEmpty()) {
            return;
        }

        WatermarkTemplate t1 = new WatermarkTemplate();
        t1.setName("版权所有 © 用户名");
        t1.setIsSystem(true);
        t1.setType("text");
        t1.setTextContent("版权所有 © {username}");
        t1.setFontFamily("SansSerif");
        t1.setFontSize(20);
        t1.setFontColor("#FFFFFF");
        t1.setOpacity(0.6);
        t1.setPosition("bottom_right");
        t1.setMarginX(20);
        t1.setMarginY(20);
        t1.setSmartMode(true);
        watermarkTemplateRepository.save(t1);

        WatermarkTemplate t2 = new WatermarkTemplate();
        t2.setName("未经授权禁止转载");
        t2.setIsSystem(true);
        t2.setType("text");
        t2.setTextContent("未经授权禁止转载");
        t2.setFontFamily("SansSerif");
        t2.setFontSize(18);
        t2.setFontColor("#FFFFFF");
        t2.setOpacity(0.7);
        t2.setPosition("tile");
        t2.setMarginX(50);
        t2.setMarginY(50);
        t2.setTileGap(150.0);
        t2.setSmartMode(true);
        watermarkTemplateRepository.save(t2);

        WatermarkTemplate t3 = new WatermarkTemplate();
        t3.setName("居中版权声明");
        t3.setIsSystem(true);
        t3.setType("text");
        t3.setTextContent("© {username} 版权所有");
        t3.setFontFamily("SansSerif");
        t3.setFontSize(28);
        t3.setFontColor("#FFFFFF");
        t3.setOpacity(0.5);
        t3.setPosition("center");
        t3.setSmartMode(true);
        watermarkTemplateRepository.save(t3);

        WatermarkTemplate t4 = new WatermarkTemplate();
        t4.setName("左上角文字水印");
        t4.setIsSystem(true);
        t4.setType("text");
        t4.setTextContent("{username}");
        t4.setFontFamily("SansSerif");
        t4.setFontSize(24);
        t4.setFontColor("#FFFFFF");
        t4.setOpacity(0.6);
        t4.setPosition("top_left");
        t4.setMarginX(15);
        t4.setMarginY(15);
        t4.setSmartMode(true);
        watermarkTemplateRepository.save(t4);
    }

    public WatermarkConfigDTO getConfig(Long userId) {
        Optional<WatermarkConfig> opt = watermarkConfigRepository.findByUserId(userId);
        return opt.map(this::toConfigDTO).orElse(null);
    }

    @Transactional
    public WatermarkConfigDTO saveConfig(Long userId, WatermarkConfigSaveRequest request) {
        WatermarkConfig config = watermarkConfigRepository.findByUserId(userId).orElse(null);
        if (config == null) {
            config = new WatermarkConfig();
            config.setUserId(userId);
        }
        copyConfigFromRequest(config, request);
        WatermarkConfig saved = watermarkConfigRepository.save(config);
        return toConfigDTO(saved);
    }

    public WatermarkConfig getConfigEntity(Long userId) {
        return watermarkConfigRepository.findByUserId(userId).orElse(null);
    }

    public List<WatermarkTemplateDTO> listTemplates(Long userId) {
        List<WatermarkTemplate> templates = watermarkTemplateRepository.findByUserIdOrIsSystemTrue(userId);
        return templates.stream().map(this::toTemplateDTO).collect(Collectors.toList());
    }

    public List<WatermarkTemplateDTO> listMyTemplates(Long userId) {
        List<WatermarkTemplate> templates = watermarkTemplateRepository.findByUserId(userId);
        return templates.stream().map(this::toTemplateDTO).collect(Collectors.toList());
    }

    @Transactional
    public WatermarkTemplateDTO createTemplate(Long userId, WatermarkTemplateSaveRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("模板名称不能为空");
        }
        WatermarkTemplate template = new WatermarkTemplate();
        template.setUserId(userId);
        template.setName(request.getName());
        template.setIsSystem(false);
        copyTemplateFromRequest(template, request);
        WatermarkTemplate saved = watermarkTemplateRepository.save(template);
        return toTemplateDTO(saved);
    }

    @Transactional
    public WatermarkTemplateDTO updateTemplate(Long userId, Long templateId, WatermarkTemplateSaveRequest request) {
        WatermarkTemplate template = watermarkTemplateRepository.findById(templateId).orElse(null);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        if (Boolean.TRUE.equals(template.getIsSystem())) {
            throw new RuntimeException("系统模板不可修改");
        }
        if (!template.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此模板");
        }
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            template.setName(request.getName());
        }
        copyTemplateFromRequest(template, request);
        WatermarkTemplate saved = watermarkTemplateRepository.save(template);
        return toTemplateDTO(saved);
    }

    @Transactional
    public void deleteTemplate(Long userId, Long templateId) {
        WatermarkTemplate template = watermarkTemplateRepository.findById(templateId).orElse(null);
        if (template == null) {
            return;
        }
        if (Boolean.TRUE.equals(template.getIsSystem())) {
            throw new RuntimeException("系统模板不可删除");
        }
        if (!template.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此模板");
        }
        watermarkTemplateRepository.delete(template);
    }

    public WatermarkTemplate getTemplateEntity(Long templateId, Long userId) {
        WatermarkTemplate template = watermarkTemplateRepository.findById(templateId).orElse(null);
        if (template == null) {
            return null;
        }
        if (Boolean.TRUE.equals(template.getIsSystem())) {
            return template;
        }
        if (template.getUserId().equals(userId)) {
            return template;
        }
        return null;
    }

    public WatermarkConfig templateToConfig(WatermarkTemplate template, Long userId) {
        WatermarkConfig config = new WatermarkConfig();
        config.setType(template.getType());
        String textContent = template.getTextContent();
        if (textContent != null && textContent.contains("{username}")) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String displayName = user.getNickname() != null ? user.getNickname() : user.getUsername();
                textContent = textContent.replace("{username}", displayName);
            }
        }
        config.setTextContent(textContent);
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
        return config;
    }

    private void copyConfigFromRequest(WatermarkConfig config, WatermarkConfigSaveRequest request) {
        if (request.getType() != null) config.setType(request.getType());
        if (request.getTextContent() != null) config.setTextContent(request.getTextContent());
        if (request.getFontFamily() != null) config.setFontFamily(request.getFontFamily());
        if (request.getFontSize() != null) config.setFontSize(request.getFontSize());
        if (request.getFontColor() != null) config.setFontColor(request.getFontColor());
        if (request.getOpacity() != null) config.setOpacity(request.getOpacity());
        if (request.getImageUrl() != null) config.setImageUrl(request.getImageUrl());
        if (request.getImageWidth() != null) config.setImageWidth(request.getImageWidth());
        if (request.getImageHeight() != null) config.setImageHeight(request.getImageHeight());
        if (request.getPosition() != null) config.setPosition(request.getPosition());
        if (request.getMarginX() != null) config.setMarginX(request.getMarginX());
        if (request.getMarginY() != null) config.setMarginY(request.getMarginY());
        if (request.getTileGap() != null) config.setTileGap(request.getTileGap());
        if (request.getSmartMode() != null) config.setSmartMode(request.getSmartMode());
        if (request.getEnabled() != null) config.setEnabled(request.getEnabled());
    }

    private void copyTemplateFromRequest(WatermarkTemplate template, WatermarkTemplateSaveRequest request) {
        if (request.getType() != null) template.setType(request.getType());
        if (request.getTextContent() != null) template.setTextContent(request.getTextContent());
        if (request.getFontFamily() != null) template.setFontFamily(request.getFontFamily());
        if (request.getFontSize() != null) template.setFontSize(request.getFontSize());
        if (request.getFontColor() != null) template.setFontColor(request.getFontColor());
        if (request.getOpacity() != null) template.setOpacity(request.getOpacity());
        if (request.getImageUrl() != null) template.setImageUrl(request.getImageUrl());
        if (request.getImageWidth() != null) template.setImageWidth(request.getImageWidth());
        if (request.getImageHeight() != null) template.setImageHeight(request.getImageHeight());
        if (request.getPosition() != null) template.setPosition(request.getPosition());
        if (request.getMarginX() != null) template.setMarginX(request.getMarginX());
        if (request.getMarginY() != null) template.setMarginY(request.getMarginY());
        if (request.getTileGap() != null) template.setTileGap(request.getTileGap());
        if (request.getSmartMode() != null) template.setSmartMode(request.getSmartMode());
    }

    public WatermarkConfigDTO toConfigDTO(WatermarkConfig config) {
        WatermarkConfigDTO dto = new WatermarkConfigDTO();
        dto.setId(config.getId());
        dto.setUserId(config.getUserId());
        dto.setType(config.getType());
        dto.setTextContent(config.getTextContent());
        dto.setFontFamily(config.getFontFamily());
        dto.setFontSize(config.getFontSize());
        dto.setFontColor(config.getFontColor());
        dto.setOpacity(config.getOpacity());
        dto.setImageUrl(config.getImageUrl());
        dto.setImageWidth(config.getImageWidth());
        dto.setImageHeight(config.getImageHeight());
        dto.setPosition(config.getPosition());
        dto.setMarginX(config.getMarginX());
        dto.setMarginY(config.getMarginY());
        dto.setTileGap(config.getTileGap());
        dto.setSmartMode(config.getSmartMode());
        dto.setEnabled(config.getEnabled());
        dto.setCreateTime(config.getCreateTime());
        dto.setUpdateTime(config.getUpdateTime());
        return dto;
    }

    public WatermarkTemplateDTO toTemplateDTO(WatermarkTemplate template) {
        WatermarkTemplateDTO dto = new WatermarkTemplateDTO();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setUserId(template.getUserId());
        dto.setIsSystem(template.getIsSystem());
        dto.setType(template.getType());
        dto.setTextContent(template.getTextContent());
        dto.setFontFamily(template.getFontFamily());
        dto.setFontSize(template.getFontSize());
        dto.setFontColor(template.getFontColor());
        dto.setOpacity(template.getOpacity());
        dto.setImageUrl(template.getImageUrl());
        dto.setImageWidth(template.getImageWidth());
        dto.setImageHeight(template.getImageHeight());
        dto.setPosition(template.getPosition());
        dto.setMarginX(template.getMarginX());
        dto.setMarginY(template.getMarginY());
        dto.setTileGap(template.getTileGap());
        dto.setSmartMode(template.getSmartMode());
        dto.setCreateTime(template.getCreateTime());
        dto.setUpdateTime(template.getUpdateTime());
        return dto;
    }
}
