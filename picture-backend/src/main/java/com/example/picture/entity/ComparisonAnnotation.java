package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "picture_comparison_annotation")
public class ComparisonAnnotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comparison_id", nullable = false)
    private Long comparisonId;

    @Column(name = "picture_id", nullable = false)
    private Long pictureId;

    @Column(nullable = false, length = 50)
    private String toolType;

    @Column(name = "start_x")
    private Double startX;

    @Column(name = "start_y")
    private Double startY;

    @Column(name = "end_x")
    private Double endX;

    @Column(name = "end_y")
    private Double endY;

    @Column(name = "points_data", columnDefinition = "TEXT")
    private String pointsData;

    @Column(length = 50)
    private String color;

    @Column(name = "stroke_width")
    private Integer strokeWidth;

    @Column(name = "text_content", length = 1000)
    private String textContent;

    @Column(name = "font_size")
    private Integer fontSize;

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
