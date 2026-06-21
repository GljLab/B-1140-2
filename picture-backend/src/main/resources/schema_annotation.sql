-- 批注表
CREATE TABLE IF NOT EXISTS picture_annotation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    picture_id BIGINT NOT NULL,
    version_id BIGINT,
    user_id BIGINT NOT NULL,
    tool_type VARCHAR(50) NOT NULL COMMENT '工具类型: brush, arrow, text, circle, rect, mosaic',
    start_x DOUBLE,
    start_y DOUBLE,
    end_x DOUBLE,
    end_y DOUBLE,
    points_data TEXT COMMENT '自由绘制的坐标点JSON数据',
    color VARCHAR(50) COMMENT '画笔颜色',
    stroke_width INT COMMENT '画笔粗细',
    text_content VARCHAR(1000) COMMENT '文字内容',
    is_resolved TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已解决',
    resolved_by BIGINT COMMENT '解决人ID',
    resolved_time DATETIME COMMENT '解决时间',
    is_public TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否公开',
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_picture_id (picture_id),
    INDEX idx_version_id (version_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片批注表';

-- 批注评论表
CREATE TABLE IF NOT EXISTS picture_annotation_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    annotation_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_id BIGINT COMMENT '父评论ID',
    content VARCHAR(2000) NOT NULL,
    create_time DATETIME,
    INDEX idx_annotation_id (annotation_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批注评论表';

-- 批注版本表
CREATE TABLE IF NOT EXISTS picture_annotation_version (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    picture_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    version_name VARCHAR(200),
    description VARCHAR(1000),
    version_number INT NOT NULL,
    create_time DATETIME,
    INDEX idx_picture_id (picture_id),
    UNIQUE KEY uk_picture_version (picture_id, version_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批注版本表';

-- 审阅表
CREATE TABLE IF NOT EXISTS picture_annotation_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    picture_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL COMMENT '状态: PENDING待审阅, ANNOTATED已批注, APPROVED已通过, NEEDS_REVISION需修改',
    comment VARCHAR(2000),
    request_time DATETIME,
    review_time DATETIME,
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_picture_id (picture_id),
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_requester_id (requester_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片审阅表';

-- 批注权限表
CREATE TABLE IF NOT EXISTS picture_annotation_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    picture_id BIGINT NOT NULL,
    user_id BIGINT COMMENT '用户ID, NULL表示公共权限',
    can_annotate TINYINT(1) NOT NULL DEFAULT 0 COMMENT '可否批注',
    can_view TINYINT(1) NOT NULL DEFAULT 1 COMMENT '可否查看',
    is_public_annotations TINYINT(1) NOT NULL DEFAULT 1 COMMENT '批注是否公开显示',
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_picture_id (picture_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批注权限表';
