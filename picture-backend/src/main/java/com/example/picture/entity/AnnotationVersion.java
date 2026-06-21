package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "picture_annotation_version")
public class AnnotationVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "picture_id", nullable = false)
    private Long pictureId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 200)
    private String versionName;

    @Column(length = 1000)
    private String description;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnnotationVersion)) return false;
        AnnotationVersion that = (AnnotationVersion) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
