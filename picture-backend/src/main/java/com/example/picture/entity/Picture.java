package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String name;
    private String url;
    private String originalUrl;
    private Long size;
    private Integer width;
    private Integer height;
    private String md5Hash;
    private String perceptualHash;
    private Boolean hasWatermark = false;
    private Date createTime;
    private Date updateTime;
    private Boolean deleted = false;
    private Date deleteTime;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isPublic = false;

    private Date shootTime;
    private String dominantColor;
    private Double latitude;
    private Double longitude;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean hasLocation = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "picture_album",
        joinColumns = @JoinColumn(name = "picture_id"),
        inverseJoinColumns = @JoinColumn(name = "album_id")
    )
    private Set<Album> albums = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "picture_tag",
        joinColumns = @JoinColumn(name = "picture_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

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
        if (!(o instanceof Picture)) return false;
        Picture picture = (Picture) o;
        return id != null && id.equals(picture.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
