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
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer referenceCount = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Picture> pictures = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return id != null && id.equals(tag.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
