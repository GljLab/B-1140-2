package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "picture_annotation_review")
public class AnnotationReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "picture_id", nullable = false)
    private Long pictureId;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(length = 2000)
    private String comment;

    @Column(name = "request_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestTime;

    @Column(name = "review_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reviewTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
        if (requestTime == null) {
            requestTime = new Date();
        }
        if (status == null) {
            status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnnotationReview)) return false;
        AnnotationReview that = (AnnotationReview) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
