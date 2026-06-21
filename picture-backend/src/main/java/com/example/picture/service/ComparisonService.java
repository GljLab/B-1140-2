package com.example.picture.service;

import com.example.picture.dto.*;
import com.example.picture.entity.Comparison;
import com.example.picture.entity.ComparisonAnnotation;
import com.example.picture.entity.Picture;
import com.example.picture.entity.User;
import com.example.picture.repository.ComparisonAnnotationRepository;
import com.example.picture.repository.ComparisonRepository;
import com.example.picture.repository.PictureRepository;
import com.example.picture.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComparisonService {

    @Autowired
    private ComparisonRepository comparisonRepository;

    @Autowired
    private ComparisonAnnotationRepository annotationRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ComparisonDTO createComparison(ComparisonCreateRequest request, Long userId) {
        if (request.getPictureIds() == null || request.getPictureIds().size() < 2) {
            throw new RuntimeException("请选择至少2张图片进行对比");
        }
        if (request.getPictureIds().size() > 4) {
            throw new RuntimeException("最多只能选择4张图片进行对比");
        }

        Comparison comparison = new Comparison();
        comparison.setUserId(userId);
        comparison.setTitle(request.getTitle() != null ? request.getTitle() : "图片对比");
        comparison.setMode(request.getMode() != null ? request.getMode() : "side-by-side");
        comparison.setPictureIds(request.getPictureIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        comparison.setViewConfig(request.getViewConfig());
        comparison.setThumbnailUrl(request.getThumbnailUrl());
        comparison.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : false);

        Comparison saved = comparisonRepository.save(comparison);
        return toDTO(saved, userId, true);
    }

    @Transactional
    public ComparisonDTO updateComparison(Long id, ComparisonUpdateRequest request, Long userId) {
        Comparison comparison = comparisonRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("对比记录不存在"));

        if (request.getTitle() != null) {
            comparison.setTitle(request.getTitle());
        }
        if (request.getMode() != null) {
            comparison.setMode(request.getMode());
        }
        if (request.getPictureIds() != null) {
            if (request.getPictureIds().size() < 2) {
                throw new RuntimeException("请选择至少2张图片进行对比");
            }
            if (request.getPictureIds().size() > 4) {
                throw new RuntimeException("最多只能选择4张图片进行对比");
            }
            comparison.setPictureIds(request.getPictureIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
        }
        if (request.getViewConfig() != null) {
            comparison.setViewConfig(request.getViewConfig());
        }
        if (request.getThumbnailUrl() != null) {
            comparison.setThumbnailUrl(request.getThumbnailUrl());
        }
        if (request.getIsPublic() != null) {
            comparison.setIsPublic(request.getIsPublic());
        }

        Comparison saved = comparisonRepository.save(comparison);
        return toDTO(saved, userId, true);
    }

    public ComparisonDTO getComparison(Long id, Long userId) {
        Comparison comparison = comparisonRepository.findAccessibleById(id, userId)
                .orElseThrow(() -> new RuntimeException("对比记录不存在或无权限访问"));
        return toDTO(comparison, userId, true);
    }

    public List<ComparisonDTO> listMyComparisons(Long userId) {
        return comparisonRepository.findByUserId(userId).stream()
                .map(c -> toDTO(c, userId, false))
                .collect(Collectors.toList());
    }

    public List<ComparisonDTO> listPublicComparisons() {
        Long anonymousUserId = null;
        return comparisonRepository.findPublicComparisons().stream()
                .map(c -> toDTO(c, anonymousUserId, false))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComparison(Long id, Long userId) {
        Comparison comparison = comparisonRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("对比记录不存在"));
        annotationRepository.deleteByComparisonId(id);
        comparisonRepository.delete(comparison);
    }

    @Transactional
    public ComparisonAnnotationDTO addAnnotation(ComparisonAnnotationCreateRequest request, Long userId) {
        Comparison comparison = comparisonRepository.findByIdAndUserId(request.getComparisonId(), userId)
                .orElseThrow(() -> new RuntimeException("对比记录不存在"));

        ComparisonAnnotation annotation = new ComparisonAnnotation();
        annotation.setComparisonId(request.getComparisonId());
        annotation.setPictureId(request.getPictureId());
        annotation.setToolType(request.getToolType());
        annotation.setStartX(request.getStartX());
        annotation.setStartY(request.getStartY());
        annotation.setEndX(request.getEndX());
        annotation.setEndY(request.getEndY());
        annotation.setPointsData(request.getPointsData());
        annotation.setColor(request.getColor() != null ? request.getColor() : "#ef4444");
        annotation.setStrokeWidth(request.getStrokeWidth() != null ? request.getStrokeWidth() : 3);
        annotation.setTextContent(request.getTextContent());
        annotation.setFontSize(request.getFontSize() != null ? request.getFontSize() : 16);

        ComparisonAnnotation saved = annotationRepository.save(annotation);
        return annotationToDTO(saved);
    }

    @Transactional
    public ComparisonAnnotationDTO updateAnnotation(Long id, ComparisonAnnotationCreateRequest request, Long userId) {
        ComparisonAnnotation annotation = annotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标注不存在"));

        Comparison comparison = comparisonRepository.findByIdAndUserId(annotation.getComparisonId(), userId)
                .orElseThrow(() -> new RuntimeException("无权限操作此标注"));

        if (request.getToolType() != null) {
            annotation.setToolType(request.getToolType());
        }
        if (request.getStartX() != null) annotation.setStartX(request.getStartX());
        if (request.getStartY() != null) annotation.setStartY(request.getStartY());
        if (request.getEndX() != null) annotation.setEndX(request.getEndX());
        if (request.getEndY() != null) annotation.setEndY(request.getEndY());
        if (request.getPointsData() != null) annotation.setPointsData(request.getPointsData());
        if (request.getColor() != null) annotation.setColor(request.getColor());
        if (request.getStrokeWidth() != null) annotation.setStrokeWidth(request.getStrokeWidth());
        if (request.getTextContent() != null) annotation.setTextContent(request.getTextContent());
        if (request.getFontSize() != null) annotation.setFontSize(request.getFontSize());

        ComparisonAnnotation saved = annotationRepository.save(annotation);
        return annotationToDTO(saved);
    }

    @Transactional
    public void deleteAnnotation(Long id, Long userId) {
        ComparisonAnnotation annotation = annotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标注不存在"));

        comparisonRepository.findByIdAndUserId(annotation.getComparisonId(), userId)
                .orElseThrow(() -> new RuntimeException("无权限操作此标注"));

        annotationRepository.delete(annotation);
    }

    public List<ComparisonAnnotationDTO> listAnnotations(Long comparisonId, Long userId) {
        Comparison comparison = comparisonRepository.findAccessibleById(comparisonId, userId)
                .orElseThrow(() -> new RuntimeException("对比记录不存在或无权限访问"));

        return annotationRepository.findByComparisonIdOrderByCreateTimeAsc(comparisonId).stream()
                .map(this::annotationToDTO)
                .collect(Collectors.toList());
    }

    private ComparisonDTO toDTO(Comparison comparison, Long currentUserId, boolean includeDetails) {
        ComparisonDTO dto = new ComparisonDTO();
        dto.setId(comparison.getId());
        dto.setUserId(comparison.getUserId());
        dto.setTitle(comparison.getTitle());
        dto.setMode(comparison.getMode());
        dto.setViewConfig(comparison.getViewConfig());
        dto.setThumbnailUrl(comparison.getThumbnailUrl());
        dto.setIsPublic(comparison.getIsPublic());
        dto.setCreateTime(comparison.getCreateTime());
        dto.setUpdateTime(comparison.getUpdateTime());

        List<Long> pids = Arrays.stream(comparison.getPictureIds().split(","))
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
        dto.setPictureIds(pids);

        userRepository.findById(comparison.getUserId()).ifPresent(user -> {
            dto.setCreatorNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        });

        if (includeDetails) {
            List<PictureDTO> pictureDTOs = new ArrayList<>();
            for (Long pid : pids) {
                pictureRepository.findById(pid).ifPresent(picture -> {
                    if (!Boolean.TRUE.equals(picture.getDeleted())) {
                        pictureDTOs.add(pictureService.toDTO(picture, currentUserId));
                    }
                });
            }
            dto.setPictures(pictureDTOs);

            List<ComparisonAnnotationDTO> annotations = annotationRepository
                    .findByComparisonIdOrderByCreateTimeAsc(comparison.getId()).stream()
                    .map(this::annotationToDTO)
                    .collect(Collectors.toList());
            dto.setAnnotations(annotations);
        }

        return dto;
    }

    private ComparisonAnnotationDTO annotationToDTO(ComparisonAnnotation annotation) {
        ComparisonAnnotationDTO dto = new ComparisonAnnotationDTO();
        dto.setId(annotation.getId());
        dto.setComparisonId(annotation.getComparisonId());
        dto.setPictureId(annotation.getPictureId());
        dto.setToolType(annotation.getToolType());
        dto.setStartX(annotation.getStartX());
        dto.setStartY(annotation.getStartY());
        dto.setEndX(annotation.getEndX());
        dto.setEndY(annotation.getEndY());
        dto.setPointsData(annotation.getPointsData());
        dto.setColor(annotation.getColor());
        dto.setStrokeWidth(annotation.getStrokeWidth());
        dto.setTextContent(annotation.getTextContent());
        dto.setFontSize(annotation.getFontSize());
        dto.setCreateTime(annotation.getCreateTime());
        dto.setUpdateTime(annotation.getUpdateTime());
        return dto;
    }
}
