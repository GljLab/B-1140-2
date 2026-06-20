package com.example.picture.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "album_collaborator", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"albumId", "userId"})
})
public class AlbumCollaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long albumId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String role = "COLLABORATOR";

    @Temporal(TemporalType.TIMESTAMP)
    private Date joinedTime;

    @PrePersist
    protected void onCreate() {
        joinedTime = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlbumCollaborator)) return false;
        AlbumCollaborator that = (AlbumCollaborator) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
