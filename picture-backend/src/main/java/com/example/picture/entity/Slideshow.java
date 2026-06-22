package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "slideshow")
public class Slideshow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    private String coverUrl;

    @Column(nullable = false, length = 20)
    private String transition = "fade";

    @Column(nullable = false)
    private Integer interval = 5;

    @Column(nullable = false)
    private Boolean loop = true;

    @Column(nullable = false)
    private Boolean autoStart = true;

    @Column(length = 500)
    private String bgMusicUrl;

    @Column(length = 20)
    private String bgMusicType = "custom";

    @Column(nullable = false)
    private Double bgMusicVolume = 0.5;

    @Column(nullable = false)
    private Boolean bgMusicLoop = true;

    @Column(length = 20)
    private String effect = "none";

    @Column(nullable = false)
    private Double effectIntensity = 0.5;

    @Column(length = 200)
    private String titlePageText;

    @Column(length = 20)
    private String textAnimation = "fade";

    @Column(length = 50)
    private String textStyle = "{}";

    @Column(length = 20)
    private String textPosition = "bottom";

    @Column(nullable = false)
    private Integer status = 0;

    @Column(nullable = false)
    private Boolean isPublic = false;

    @Column(nullable = false, unique = true, length = 100)
    private String shareToken;

    @Column(nullable = false)
    private Boolean allowDownload = false;

    private Long viewCount = 0L;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date publishTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
        if (shareToken == null || shareToken.isEmpty()) {
            shareToken = java.util.UUID.randomUUID().toString().replace("-", "");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Slideshow)) return false;
        Slideshow that = (Slideshow) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
