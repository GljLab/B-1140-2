package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "slideshow_item")
public class SlideshowItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long slideshowId;

    @Column(nullable = false)
    private Long pictureId;

    @Column(length = 500)
    private String caption;

    @Column(length = 20)
    private String effect = "none";

    @Column(nullable = false)
    private Double effectIntensity = 0.5;

    @Column(length = 20)
    private String textAnimation = "fade";

    @Column(length = 50)
    private String textStyle = "{}";

    @Column(length = 20)
    private String textPosition = "bottom";

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private Integer customInterval;

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
