package com.example.picture.service;

import com.example.picture.dto.LoginRequest;
import com.example.picture.dto.RegisterRequest;
import com.example.picture.dto.UserDTO;
import com.example.picture.entity.User;
import com.example.picture.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDTO register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        String username = request.getUsername().trim();
        if (username.length() < 3 || username.length() > 20) {
            throw new RuntimeException("用户名长度需在3-20个字符之间");
        }
        String password = request.getPassword().trim();
        if (password.length() < 6 || password.length() > 20) {
            throw new RuntimeException("密码长度需在6-20个字符之间");
        }
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptPassword(password));
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            user.setNickname(request.getNickname().trim());
        } else {
            user.setNickname(username);
        }

        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    public UserDTO login(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        String username = request.getUsername().trim();
        String password = request.getPassword().trim();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!encryptPassword(password).equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        return toDTO(user);
    }

    public UserDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return toDTO(user);
    }

    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setCreateTime(user.getCreateTime());
        return dto;
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败");
        }
    }
}
