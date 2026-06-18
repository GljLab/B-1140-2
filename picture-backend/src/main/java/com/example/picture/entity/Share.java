package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "share")
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String shareType;

    private Long pictureId;

    private Long albumId;

    @Column(length = 100)
    private String title;

    @Column(length = 500)
    private String coverUrl;

    @Column(nullable = false, unique = true, length = 100)
    private String shareToken;

    @Column(length = 100)
    private String password;

    @Column(nullable = false)
    private Integer expireDays;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expireTime;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
        if (expireDays != null && expireDays > 0) {
            expireTime = new Date(System.currentTimeMillis() + expireDays * 24L * 60 * 60 * 1000);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }

    public boolean isExpired() {
        if (expireTime == null) {
            return false;
        }
        return new Date().after(expireTime);
    }
}
