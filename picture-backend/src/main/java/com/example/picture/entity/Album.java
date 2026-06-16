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
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    private String coverUrl;

    private Long coverPictureId;

    @Column(nullable = false)
    private Boolean isPublic = true;

    @Column(nullable = false)
    private Boolean isDefault = false;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @ManyToMany(mappedBy = "albums", fetch = FetchType.LAZY)
    private Set<Picture> pictures = new HashSet<>();

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
        if (!(o instanceof Album)) return false;
        Album album = (Album) o;
        return id != null && id.equals(album.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
