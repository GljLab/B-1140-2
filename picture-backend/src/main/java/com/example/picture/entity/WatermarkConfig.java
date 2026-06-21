package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "watermark_config")
public class WatermarkConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(length = 20)
    private String type;

    @Column(length = 500)
    private String textContent;

    @Column(length = 50)
    private String fontFamily;

    private Integer fontSize;

    @Column(length = 20)
    private String fontColor;

    private Double opacity;

    @Column(length = 500)
    private String imageUrl;

    private Integer imageWidth;

    private Integer imageHeight;

    @Column(length = 20)
    private String position;

    private Integer marginX;

    private Integer marginY;

    private Double tileGap;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean smartMode = false;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean enabled = false;

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
