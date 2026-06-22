-- 搜索功能索引优化脚本

-- Picture表搜索相关字段索引
CREATE INDEX IF NOT EXISTS idx_picture_user_deleted ON picture(user_id, deleted);
CREATE INDEX IF NOT EXISTS idx_picture_name ON picture(name);
CREATE INDEX IF NOT EXISTS idx_picture_shoot_time ON picture(shoot_time);
CREATE INDEX IF NOT EXISTS idx_picture_dominant_color ON picture(dominant_color);
CREATE INDEX IF NOT EXISTS idx_picture_width_height ON picture(width, height);
CREATE INDEX IF NOT EXISTS idx_picture_size ON picture(size);
CREATE INDEX IF NOT EXISTS idx_picture_has_location ON picture(has_location);
CREATE INDEX IF NOT EXISTS idx_picture_perceptual_hash ON picture(perceptual_hash);
CREATE INDEX IF NOT EXISTS idx_picture_create_time ON picture(create_time);
CREATE INDEX IF NOT EXISTS idx_picture_user_create_time ON picture(user_id, create_time DESC);

-- 多列复合索引用于高级搜索
CREATE INDEX IF NOT EXISTS idx_picture_user_shoot_time ON picture(user_id, shoot_time);
CREATE INDEX IF NOT EXISTS idx_picture_user_color ON picture(user_id, dominant_color);
CREATE INDEX IF NOT EXISTS idx_picture_user_size ON picture(user_id, size);
CREATE INDEX IF NOT EXISTS idx_picture_user_location ON picture(user_id, has_location);

-- 关联表索引优化
CREATE INDEX IF NOT EXISTS idx_picture_album_picture ON picture_album(picture_id);
CREATE INDEX IF NOT EXISTS idx_picture_album_album ON picture_album(album_id);
CREATE INDEX IF NOT EXISTS idx_picture_tag_picture ON picture_tag(picture_id);
CREATE INDEX IF NOT EXISTS idx_picture_tag_tag ON picture_tag(tag_id);

-- Tag表索引
CREATE INDEX IF NOT EXISTS idx_tag_user_name ON tag(user_id, name);
CREATE INDEX IF NOT EXISTS idx_tag_reference_count ON tag(reference_count DESC);

-- Album表索引
CREATE INDEX IF NOT EXISTS idx_album_user_name ON album(user_id, name);

-- 搜索历史表
CREATE TABLE IF NOT EXISTS search_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    keyword VARCHAR(500) NOT NULL,
    search_params TEXT,
    search_time DATETIME,
    INDEX idx_search_history_user (user_id),
    INDEX idx_search_history_user_time (user_id, search_time DESC),
    INDEX idx_search_history_keyword (keyword)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索历史表';

-- 快捷搜索表
CREATE TABLE IF NOT EXISTS saved_search (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    search_params TEXT NOT NULL,
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_saved_search_user (user_id),
    INDEX idx_saved_search_user_time (user_id, create_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快捷搜索表';
