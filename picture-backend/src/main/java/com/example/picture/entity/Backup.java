package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "backup")
public class Backup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String fileName;

    @Column(nullable = false, length = 500)
    private String filePath;

    private Long fileSize;

    private Integer pictureCount;

    private Integer albumCount;

    private Integer tagCount;

    @Column(length = 20)
    private String status;

    @Column(length = 500)
    private String errorMessage;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date completeTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        if (status == null) {
            status = "PENDING";
        }
    }
}
