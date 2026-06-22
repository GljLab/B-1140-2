package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "search_history")
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String keyword;

    @Column(length = 2000)
    private String searchParams;

    @Temporal(TemporalType.TIMESTAMP)
    private Date searchTime;

    @PrePersist
    protected void onCreate() {
        searchTime = new Date();
    }
}
