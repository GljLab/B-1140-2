package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.ApiResponse;
import com.example.picture.dto.LoginRequest;
import com.example.picture.dto.RegisterRequest;
import com.example.picture.dto.UserDTO;
import com.example.picture.service.AlbumService;
import com.example.picture.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AlbumService albumService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@RequestBody RegisterRequest request) {
        try {
            UserDTO user = userService.register(request);
            albumService.ensureDefaultAlbum(user.getId());
            return ResponseEntity.ok(ApiResponse.success("注册成功", user));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDTO>> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            UserDTO user = userService.login(request);
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("user", user);
            return ResponseEntity.ok(ApiResponse.success("登录成功", user));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        UserContext.clear();
        return ResponseEntity.ok(ApiResponse.success("已退出登录", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        if (UserContext.isLoggedIn()) {
            return ResponseEntity.ok(ApiResponse.success(UserContext.getCurrentUser()));
        }
        return ResponseEntity.status(401).body(ApiResponse.error("未登录"));
    }
}
