package com.example.picture.service;

import com.example.picture.dto.*;
import com.example.picture.entity.Picture;
import com.example.picture.entity.PrivateSpaceConfig;
import com.example.picture.entity.User;
import com.example.picture.repository.PictureRepository;
import com.example.picture.repository.PrivateSpaceConfigRepository;
import com.example.picture.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrivateSpaceService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCK_DURATION_MINUTES = 5;

    @Autowired
    private PrivateSpaceConfigRepository privateSpaceConfigRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private UserRepository userRepository;

    public PrivateSpaceConfigDTO getConfig(Long userId) {
        PrivateSpaceConfig config = getOrCreateConfig(userId);
        return toDTO(config);
    }

    @Transactional
    public PrivateSpaceConfigDTO setPassword(Long userId, PrivatePasswordSetRequest request) {
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        String password = request.getPassword().trim();
        if (password.length() < 4 || password.length() > 6) {
            throw new RuntimeException("密码长度需在4-6位之间");
        }

        PrivateSpaceConfig config = getOrCreateConfig(userId);
        config.setPrivatePassword(encryptPassword(password));
        config.setPasswordType(request.getPasswordType() != null ? request.getPasswordType() : "pin");
        config.setFailedAttempts(0);
        config.setIsLocked(true);
        privateSpaceConfigRepository.save(config);
        return toDTO(config);
    }

    @Transactional
    public PrivateSpaceConfigDTO verifyPassword(Long userId, PrivatePasswordVerifyRequest request) {
        PrivateSpaceConfig config = getOrCreateConfig(userId);

        if (config.getPrivatePassword() == null) {
            throw new RuntimeException("请先设置私密密码");
        }

        if (config.getLockUntil() != null && config.getLockUntil().after(new Date())) {
            long remainingSeconds = (config.getLockUntil().getTime() - System.currentTimeMillis()) / 1000;
            throw new RuntimeException("密码错误次数过多，请 " + (remainingSeconds / 60 + 1) + " 分钟后再试");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("请输入密码");
        }

        String inputPassword = encryptPassword(request.getPassword().trim());
        if (!inputPassword.equals(config.getPrivatePassword())) {
            int failedAttempts = config.getFailedAttempts() + 1;
            config.setFailedAttempts(failedAttempts);
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                Date lockUntil = new Date(System.currentTimeMillis() + LOCK_DURATION_MINUTES * 60 * 1000L);
                config.setLockUntil(lockUntil);
                config.setFailedAttempts(0);
                privateSpaceConfigRepository.save(config);
                throw new RuntimeException("密码错误次数过多，请 " + LOCK_DURATION_MINUTES + " 分钟后再试");
            }
            privateSpaceConfigRepository.save(config);
            throw new RuntimeException("密码错误，还剩 " + (MAX_FAILED_ATTEMPTS - failedAttempts) + " 次机会");
        }

        config.setFailedAttempts(0);
        config.setLockUntil(null);
        config.setIsLocked(false);
        config.setLastActivityTime(new Date());
        privateSpaceConfigRepository.save(config);

        return toDTO(config);
    }

    @Transactional
    public PrivateSpaceConfigDTO changePassword(Long userId, PrivatePasswordChangeRequest request) {
        PrivateSpaceConfig config = getOrCreateConfig(userId);

        if (config.getPrivatePassword() == null) {
            throw new RuntimeException("请先设置私密密码");
        }

        if (request.getOldPassword() == null || request.getOldPassword().trim().isEmpty()) {
            throw new RuntimeException("请输入旧密码");
        }

        String oldPasswordEncrypted = encryptPassword(request.getOldPassword().trim());
        if (!oldPasswordEncrypted.equals(config.getPrivatePassword())) {
            throw new RuntimeException("旧密码错误");
        }

        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }

        String newPassword = request.getNewPassword().trim();
        if (newPassword.length() < 4 || newPassword.length() > 6) {
            throw new RuntimeException("新密码长度需在4-6位之间");
        }

        config.setPrivatePassword(encryptPassword(newPassword));
        privateSpaceConfigRepository.save(config);

        return toDTO(config);
    }

    @Transactional
    public PrivateSpaceConfigDTO resetPassword(Long userId, String accountPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        String accountPasswordEncrypted = encryptPasswordWithMD5(accountPassword);
        if (!accountPasswordEncrypted.equals(user.getPassword())) {
            throw new RuntimeException("账号密码错误");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }

        String password = newPassword.trim();
        if (password.length() < 4 || password.length() > 6) {
            throw new RuntimeException("密码长度需在4-6位之间");
        }

        PrivateSpaceConfig config = getOrCreateConfig(userId);
        config.setPrivatePassword(encryptPassword(password));
        config.setFailedAttempts(0);
        config.setLockUntil(null);
        config.setIsLocked(true);
        privateSpaceConfigRepository.save(config);

        return toDTO(config);
    }

    @Transactional
    public PrivateSpaceConfigDTO setSecurityQuestion(Long userId, SecurityQuestionSetRequest request) {
        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            throw new RuntimeException("安全问题不能为空");
        }
        if (request.getAnswer() == null || request.getAnswer().trim().isEmpty()) {
            throw new RuntimeException("安全答案不能为空");
        }

        PrivateSpaceConfig config = getOrCreateConfig(userId);
        config.setSecurityQuestion(request.getQuestion().trim());
        config.setSecurityAnswer(encryptPassword(request.getAnswer().trim()));
        privateSpaceConfigRepository.save(config);

        return toDTO(config);
    }

    public boolean verifySecurityQuestion(Long userId, SecurityQuestionVerifyRequest request) {
        PrivateSpaceConfig config = getOrCreateConfig(userId);
        if (config.getSecurityQuestion() == null || config.getSecurityAnswer() == null) {
            throw new RuntimeException("未设置安全问题");
        }
        if (request.getAnswer() == null || request.getAnswer().trim().isEmpty()) {
            throw new RuntimeException("请输入答案");
        }
        String answerEncrypted = encryptPassword(request.getAnswer().trim());
        return answerEncrypted.equals(config.getSecurityAnswer());
    }

    @Transactional
    public PrivateSpaceConfigDTO updateSettings(Long userId, PrivateSpaceSettingsRequest request) {
        PrivateSpaceConfig config = getOrCreateConfig(userId);

        if (request.getDisguiseType() != null) {
            config.setDisguiseType(request.getDisguiseType());
        }
        if (request.getAutoLockTime() != null) {
            int lockTime = request.getAutoLockTime();
            if (lockTime != 1 && lockTime != 5 && lockTime != 10) {
                throw new RuntimeException("自动锁定时间只能是1、5、10分钟");
            }
            config.setAutoLockTime(lockTime);
        }
        if (request.getGuestModeEnabled() != null) {
            config.setGuestModeEnabled(request.getGuestModeEnabled());
        }

        privateSpaceConfigRepository.save(config);
        return toDTO(config);
    }

    @Transactional
    public void lockPrivateSpace(Long userId) {
        PrivateSpaceConfig config = getOrCreateConfig(userId);
        config.setIsLocked(true);
        privateSpaceConfigRepository.save(config);
    }

    @Transactional
    public boolean checkAutoLock(Long userId) {
        PrivateSpaceConfig config = getOrCreateConfig(userId);
        if (Boolean.TRUE.equals(config.getIsLocked())) {
            return true;
        }
        if (config.getAutoLockTime() == null || config.getLastActivityTime() == null) {
            return false;
        }

        long autoLockMs = config.getAutoLockTime() * 60 * 1000L;
        long elapsed = System.currentTimeMillis() - config.getLastActivityTime().getTime();
        if (elapsed >= autoLockMs) {
            config.setIsLocked(true);
            privateSpaceConfigRepository.save(config);
            return true;
        }

        config.setLastActivityTime(new Date());
        privateSpaceConfigRepository.save(config);
        return false;
    }

    public void ensureUnlocked(Long userId) {
        PrivateSpaceConfig config = getOrCreateConfig(userId);
        if (config.getPrivatePassword() == null) {
            throw new RuntimeException("请先设置私密密码");
        }
        if (Boolean.TRUE.equals(config.getIsLocked())) {
            throw new RuntimeException("私密空间已锁定，请先验证密码");
        }
        if (config.getAutoLockTime() != null && config.getLastActivityTime() != null) {
            long autoLockMs = config.getAutoLockTime() * 60 * 1000L;
            long elapsed = System.currentTimeMillis() - config.getLastActivityTime().getTime();
            if (elapsed >= autoLockMs) {
                config.setIsLocked(true);
                privateSpaceConfigRepository.save(config);
                throw new RuntimeException("私密空间已超时锁定，请重新验证密码");
            }
        }
    }

    @Transactional
    public void refreshActivity(Long userId) {
        PrivateSpaceConfig config = getOrCreateConfig(userId);
        config.setLastActivityTime(new Date());
        privateSpaceConfigRepository.save(config);
    }

    @Transactional
    public PictureDTO moveToPrivate(Long pictureId, Long userId) {
        ensureUnlocked(userId);
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null || !picture.getUserId().equals(userId) || Boolean.TRUE.equals(picture.getDeleted())) {
            throw new RuntimeException("图片不存在");
        }
        if (Boolean.TRUE.equals(picture.getIsPrivate())) {
            throw new RuntimeException("图片已在私密空间中");
        }
        picture.setIsPrivate(true);
        picture.setIsPublic(false);
        Picture saved = pictureRepository.save(picture);
        refreshActivity(userId);
        return pictureService.toDTO(saved, userId);
    }

    @Transactional
    public PictureDTO moveFromPrivate(Long pictureId, Long userId) {
        ensureUnlocked(userId);
        Picture picture = pictureRepository.findPrivateByIdAndUserId(pictureId, userId).orElse(null);
        if (picture == null) {
            throw new RuntimeException("私密图片不存在");
        }
        picture.setIsPrivate(false);
        Picture saved = pictureRepository.save(picture);
        refreshActivity(userId);
        return pictureService.toDTO(saved, userId);
    }

    @Transactional
    public int batchMoveToPrivate(List<Long> pictureIds, Long userId) {
        ensureUnlocked(userId);
        if (pictureIds == null || pictureIds.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (Long pid : pictureIds) {
            try {
                Picture picture = pictureRepository.findById(pid).orElse(null);
                if (picture != null && picture.getUserId().equals(userId)
                        && !Boolean.TRUE.equals(picture.getDeleted())
                        && !Boolean.TRUE.equals(picture.getIsPrivate())) {
                    picture.setIsPrivate(true);
                    picture.setIsPublic(false);
                    pictureRepository.save(picture);
                    count++;
                }
            } catch (Exception ignored) {
            }
        }
        refreshActivity(userId);
        return count;
    }

    @Transactional
    public int batchMoveFromPrivate(List<Long> pictureIds, Long userId) {
        ensureUnlocked(userId);
        if (pictureIds == null || pictureIds.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (Long pid : pictureIds) {
            try {
                Picture picture = pictureRepository.findPrivateByIdAndUserId(pid, userId).orElse(null);
                if (picture != null) {
                    picture.setIsPrivate(false);
                    pictureRepository.save(picture);
                    count++;
                }
            } catch (Exception ignored) {
            }
        }
        refreshActivity(userId);
        return count;
    }

    public List<PictureDTO> listPrivatePictures(Long userId) {
        ensureUnlocked(userId);
        List<Picture> pictures = pictureRepository.findPrivateByUserId(userId);
        refreshActivity(userId);
        return pictures.stream().map(p -> pictureService.toDTO(p, userId)).collect(Collectors.toList());
    }

    public PictureDTO getPrivatePicture(Long pictureId, Long userId) {
        ensureUnlocked(userId);
        Picture picture = pictureRepository.findPrivateByIdAndUserId(pictureId, userId).orElse(null);
        if (picture == null) {
            throw new RuntimeException("私密图片不存在");
        }
        refreshActivity(userId);
        return pictureService.toDTO(picture, userId);
    }

    public long countPrivatePictures(Long userId) {
        return pictureRepository.countPrivateByUserId(userId);
    }

    private PrivateSpaceConfig getOrCreateConfig(Long userId) {
        return privateSpaceConfigRepository.findByUserId(userId).orElseGet(() -> {
            PrivateSpaceConfig config = new PrivateSpaceConfig();
            config.setUserId(userId);
            return privateSpaceConfigRepository.save(config);
        });
    }

    private PrivateSpaceConfigDTO toDTO(PrivateSpaceConfig config) {
        PrivateSpaceConfigDTO dto = new PrivateSpaceConfigDTO();
        dto.setUserId(config.getUserId());
        dto.setHasPassword(config.getPrivatePassword() != null);
        dto.setPasswordType(config.getPasswordType());
        dto.setDisguiseType(config.getDisguiseType());
        dto.setAutoLockTime(config.getAutoLockTime());
        dto.setGuestModeEnabled(Boolean.TRUE.equals(config.getGuestModeEnabled()));
        dto.setHasSecurityQuestion(config.getSecurityQuestion() != null && config.getSecurityAnswer() != null);
        dto.setSecurityQuestion(config.getSecurityQuestion());
        dto.setIsLocked(Boolean.TRUE.equals(config.getIsLocked()));
        dto.setFailedAttempts(config.getFailedAttempts());
        dto.setLastActivityTime(config.getLastActivityTime());
        return dto;
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
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

    private String encryptPasswordWithMD5(String password) {
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
