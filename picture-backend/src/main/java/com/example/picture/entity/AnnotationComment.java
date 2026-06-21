package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "picture_annotation_comment")
public class AnnotationComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation_id", nullable = false)
    private Long annotationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false, length = 2000)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnnotationComment)) return false;
        AnnotationComment that = (AnnotationComment) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
