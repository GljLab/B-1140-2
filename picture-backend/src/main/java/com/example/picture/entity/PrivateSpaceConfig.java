package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "private_space_config")
public class PrivateSpaceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(length = 100)
    private String privatePassword;

    @Column(length = 20)
    private String passwordType;

    @Column(length = 50)
    private String disguiseType;

    private Integer autoLockTime;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean guestModeEnabled = false;

    @Column(length = 200)
    private String securityQuestion;

    @Column(length = 200)
    private String securityAnswer;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer failedAttempts = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lockUntil;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastActivityTime;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isLocked = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
        if (failedAttempts == null) {
            failedAttempts = 0;
        }
        if (guestModeEnabled == null) {
            guestModeEnabled = false;
        }
        if (isLocked == null) {
            isLocked = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}
