package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "picture_annotation_permission")
public class AnnotationPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "picture_id", nullable = false)
    private Long pictureId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "can_annotate", nullable = false)
    private Boolean canAnnotate = false;

    @Column(name = "can_view", nullable = false)
    private Boolean canView = true;

    @Column(name = "is_public_annotations", nullable = false)
    private Boolean isPublicAnnotations = true;

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
        if (!(o instanceof AnnotationPermission)) return false;
        AnnotationPermission that = (AnnotationPermission) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
