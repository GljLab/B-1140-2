package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.ApiResponse;
import com.example.picture.dto.DiscoverPictureDTO;
import com.example.picture.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/discover")
public class DiscoverController {

    @Autowired
    private InteractionService interactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DiscoverPictureDTO>>> getDiscoverPictures() {
        Long userId = UserContext.getCurrentUserId();
        List<DiscoverPictureDTO> pictures = interactionService.getDiscoverPictures(userId);
        return ResponseEntity.ok(ApiResponse.success(pictures));
    }

    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<DiscoverPictureDTO>>> getMyFavorites() {
        Long userId = UserContext.getCurrentUserId();
        List<DiscoverPictureDTO> favorites = interactionService.getMyFavorites(userId);
        return ResponseEntity.ok(ApiResponse.success(favorites));
    }
}
