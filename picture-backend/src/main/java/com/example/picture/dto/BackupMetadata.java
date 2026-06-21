package com.example.picture.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BackupMetadata {
    private String version;
    private String backupTime;
    private String username;
    private List<BackupPicture> pictures;
    private List<BackupAlbum> albums;
    private List<BackupTag> tags;

    @Getter
    @Setter
    public static class BackupPicture {
        private String name;
        private String fileName;
        private Long size;
        private String createTime;
        private String updateTime;
        private List<String> tags;
        private List<String> albums;
        private Boolean isPublic;
    }

    @Getter
    @Setter
    public static class BackupAlbum {
        private String name;
        private String description;
        private String coverPictureName;
        private Integer displayOrder;
        private List<String> pictureNames;
    }

    @Getter
    @Setter
    public static class BackupTag {
        private String name;
        private Integer referenceCount;
    }
}
