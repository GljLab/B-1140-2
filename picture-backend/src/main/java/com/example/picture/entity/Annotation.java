package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "picture_annotation")
public class Annotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "picture_id", nullable = false)
    private Long pictureId;

    @Column(name = "version_id")
    private Long versionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

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

    @Column(name = "is_resolved", nullable = false)
    private Boolean isResolved = false;

    @Column(name = "resolved_by")
    private Long resolvedBy;

    @Column(name = "resolved_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date resolvedTime;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Annotation)) return false;
        Annotation that = (Annotation) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
