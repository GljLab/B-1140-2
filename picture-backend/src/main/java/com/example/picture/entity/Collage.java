package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "collage")
public class Collage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String name;

    private String url;

    @Column(length = 50)
    private String templateType;

    private Integer canvasWidth = 1080;

    private Integer canvasHeight = 1080;

    @Column(length = 20)
    private String backgroundColor = "#FFFFFF";

    @Column(length = 500)
    private String backgroundImageUrl;

    private Integer gap = 10;

    private Integer borderRadius = 0;

    private Integer borderWidth = 0;

    @Column(length = 20)
    private String borderColor = "#000000";

    @Column(columnDefinition = "TEXT")
    private String items;

    @Column(columnDefinition = "TEXT")
    private String texts;

    @Column(columnDefinition = "TEXT")
    private String stickers;

    @Column(length = 50)
    private String sizePreset;

    @Column(length = 20)
    private String quality = "high";

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}
