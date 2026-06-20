package com.example.picture.service;

import com.example.picture.dto.CommentDTO;
import com.example.picture.dto.DiscoverPictureDTO;
import com.example.picture.dto.TagDTO;
import com.example.picture.entity.*;
import com.example.picture.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InteractionService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagService tagService;

    @Transactional
    public boolean toggleLike(Long pictureId, Long userId) {
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null || Boolean.TRUE.equals(picture.getDeleted())) {
            throw new RuntimeException("图片不存在");
        }
        if (!picture.getUserId().equals(userId) && !Boolean.TRUE.equals(picture.getIsPublic())) {
            throw new RuntimeException("无法操作私密图片");
        }
        if (likeRepository.existsByUserIdAndPictureId(userId, pictureId)) {
            likeRepository.deleteByUserIdAndPictureId(userId, pictureId);
            return false;
        } else {
            Like like = new Like();
            like.setUserId(userId);
            like.setPictureId(pictureId);
            likeRepository.save(like);
            return true;
        }
    }

    public boolean isLiked(Long pictureId, Long userId) {
        if (userId == null) return false;
        return likeRepository.existsByUserIdAndPictureId(userId, pictureId);
    }

    public long getLikeCount(Long pictureId) {
        return likeRepository.countByPictureId(pictureId);
    }

    @Transactional
    public CommentDTO addComment(Long pictureId, Long userId, String content) {
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null || Boolean.TRUE.equals(picture.getDeleted())) {
            throw new RuntimeException("图片不存在");
        }
        if (!picture.getUserId().equals(userId) && !Boolean.TRUE.equals(picture.getIsPublic())) {
            throw new RuntimeException("无法评论私密图片");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("评论内容不能为空");
        }
        if (content.length() > 500) {
            throw new RuntimeException("评论内容不能超过500字");
        }
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPictureId(pictureId);
        comment.setContent(content.trim());
        Comment saved = commentRepository.save(comment);
        return toCommentDTO(saved);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        Picture picture = pictureRepository.findById(comment.getPictureId()).orElse(null);
        boolean isPictureAuthor = picture != null && picture.getUserId().equals(userId);
        boolean isCommentAuthor = comment.getUserId().equals(userId);
        if (!isPictureAuthor && !isCommentAuthor) {
            throw new RuntimeException("无权删除此评论");
        }
        commentRepository.delete(comment);
    }

    public List<CommentDTO> getComments(Long pictureId) {
        return commentRepository.findByPictureIdOrderByCreateTimeDesc(pictureId).stream()
                .map(this::toCommentDTO)
                .collect(Collectors.toList());
    }

    public long getCommentCount(Long pictureId) {
        return commentRepository.countByPictureId(pictureId);
    }

    @Transactional
    public boolean toggleFavorite(Long pictureId, Long userId) {
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null || Boolean.TRUE.equals(picture.getDeleted())) {
            throw new RuntimeException("图片不存在");
        }
        if (!picture.getUserId().equals(userId) && !Boolean.TRUE.equals(picture.getIsPublic())) {
            throw new RuntimeException("无法收藏私密图片");
        }
        if (favoriteRepository.existsByUserIdAndPictureId(userId, pictureId)) {
            favoriteRepository.deleteByUserIdAndPictureId(userId, pictureId);
            return false;
        } else {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setPictureId(pictureId);
            favoriteRepository.save(favorite);
            return true;
        }
    }

    public boolean isFavorited(Long pictureId, Long userId) {
        if (userId == null) return false;
        return favoriteRepository.existsByUserIdAndPictureId(userId, pictureId);
    }

    public long getFavoriteCount(Long pictureId) {
        return favoriteRepository.countByPictureId(pictureId);
    }

    public List<DiscoverPictureDTO> getDiscoverPictures(Long currentUserId) {
        List<Picture> publicPictures = pictureRepository.findPublicPictures();
        return publicPictures.stream().map(p -> toDiscoverDTO(p, currentUserId)).collect(Collectors.toList());
    }

    public List<DiscoverPictureDTO> getMyFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserIdOrderByCreateTimeDesc(userId);
        return favorites.stream().map(fav -> {
            Picture picture = pictureRepository.findById(fav.getPictureId()).orElse(null);
            if (picture == null || Boolean.TRUE.equals(picture.getDeleted())) return null;
            DiscoverPictureDTO dto = toDiscoverDTO(picture, userId);
            dto.setFavoriteTime(fav.getCreateTime());
            return dto;
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }

    public long countReceivedLikes(Long userId) {
        List<Picture> userPictures = pictureRepository.findPublicPicturesByUserId(userId);
        long total = 0;
        for (Picture p : userPictures) {
            total += likeRepository.countByPictureId(p.getId());
        }
        return total;
    }

    public long countReceivedComments(Long userId) {
        List<Picture> userPictures = pictureRepository.findPublicPicturesByUserId(userId);
        long total = 0;
        for (Picture p : userPictures) {
            total += commentRepository.countByPictureId(p.getId());
        }
        return total;
    }

    private DiscoverPictureDTO toDiscoverDTO(Picture picture, Long currentUserId) {
        DiscoverPictureDTO dto = new DiscoverPictureDTO();
        dto.setId(picture.getId());
        dto.setName(picture.getName());
        dto.setUrl(picture.getUrl());
        dto.setCreateTime(picture.getCreateTime());
        dto.setAuthorId(picture.getUserId());
        dto.setLikeCount(likeRepository.countByPictureId(picture.getId()));
        dto.setCommentCount(commentRepository.countByPictureId(picture.getId()));
        dto.setFavoriteCount(favoriteRepository.countByPictureId(picture.getId()));

        userRepository.findById(picture.getUserId()).ifPresent(user -> {
            dto.setAuthorNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        });

        List<TagDTO> tagDTOs = picture.getTags().stream()
                .sorted(Comparator.comparing(Tag::getReferenceCount).reversed().thenComparing(Tag::getCreateTime))
                .map(tagService::toDTO)
                .collect(Collectors.toList());
        dto.setTags(tagDTOs);

        return dto;
    }

    private CommentDTO toCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUserId());
        dto.setPictureId(comment.getPictureId());
        dto.setContent(comment.getContent());
        dto.setCreateTime(comment.getCreateTime());
        userRepository.findById(comment.getUserId()).ifPresent(user -> {
            dto.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        });
        return dto;
    }
}
