package com.example.picture.service;

import com.example.picture.dto.GlobalStatsDTO;
import com.example.picture.dto.TagDTO;
import com.example.picture.repository.AlbumRepository;
import com.example.picture.repository.PictureRepository;
import com.example.picture.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private InteractionService interactionService;

    public GlobalStatsDTO getGlobalStats(Long userId) {
        GlobalStatsDTO stats = new GlobalStatsDTO();
        stats.setAlbumCount(albumRepository.countByUserId(userId));
        stats.setPictureCount(pictureRepository.countByUserId(userId));
        Long totalSize = pictureRepository.sumAllSizesByUserId(userId);
        stats.setTotalStorageSize(totalSize != null ? totalSize : 0L);
        List<TagDTO> topTags = tagService.getTopTags(10, userId);
        stats.setTopTags(topTags);
        stats.setReceivedLikeCount(interactionService.countReceivedLikes(userId));
        stats.setReceivedCommentCount(interactionService.countReceivedComments(userId));
        return stats;
    }
}
