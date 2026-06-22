-- 图片笔记表
CREATE TABLE IF NOT EXISTS picture_note (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    picture_id BIGINT NOT NULL COMMENT '关联的图片ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) COMMENT '笔记标题',
    content LONGTEXT COMMENT '笔记内容（Markdown格式）',
    word_count INT DEFAULT 0 COMMENT '字数统计',
    is_draft TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否为草稿',
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_picture_id (picture_id),
    INDEX idx_user_id (user_id),
    INDEX idx_user_update_time (user_id, update_time),
    UNIQUE KEY uk_picture_user (picture_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片笔记表';

-- 笔记模板表
CREATE TABLE IF NOT EXISTS picture_note_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '用户ID，NULL表示系统模板',
    name VARCHAR(100) NOT NULL COMMENT '模板名称',
    description VARCHAR(500) COMMENT '模板描述',
    content TEXT COMMENT '模板内容（Markdown格式）',
    category VARCHAR(50) COMMENT '模板分类：shooting, postprocessing, travel, food, custom',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_user_id (user_id),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记模板表';

-- 笔记关联图片表
CREATE TABLE IF NOT EXISTS picture_note_reference (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    note_id BIGINT NOT NULL COMMENT '笔记ID',
    picture_id BIGINT NOT NULL COMMENT '关联的图片ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME,
    INDEX idx_note_id (note_id),
    INDEX idx_picture_id (picture_id),
    INDEX idx_user_id (user_id),
    UNIQUE KEY uk_note_picture (note_id, picture_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记关联图片表';

-- 插入系统默认模板
INSERT INTO picture_note_template (user_id, name, description, content, category, sort_order, create_time, update_time) VALUES
(NULL, '拍摄参数记录', '记录拍摄时的相机参数、设置等信息', '## 拍摄参数

- **相机型号**: 
- **镜头**: 
- **焦距**: 
- **光圈**: f/
- **快门速度**: 1/
- **ISO**: 
- **白平衡**: 
- **拍摄模式**: 

## 拍摄场景

- **地点**: 
- **时间**: 
- **光线条件**: 
- **天气**: 

## 拍摄心得

', 'shooting', 1, NOW(), NOW()),

(NULL, '后期处理流程', '记录照片后期处理的步骤和参数', '## 后期处理

### 基础调整
- **曝光**: 
- **对比度**: 
- **高光**: 
- **阴影**: 
- **白色**: 
- **黑色**: 

### 色彩调整
- **色温**: 
- **色调**: 
- **自然饱和度**: 
- **饱和度**: 
- **HSL调整**: 

### 细节处理
- **锐化**: 
- **降噪**: 
- **畸变校正**: 

### 创意效果
- **滤镜**: 
- **晕影**: 
- **颗粒**: 

## 后期思路总结

', 'postprocessing', 2, NOW(), NOW()),

(NULL, '旅行日记', '记录旅行中的美好瞬间和故事', '## 旅行日记

### 日期与地点
- **日期**: 
- **地点**: 
- **天气**: 

### 今日亮点
- **最难忘的瞬间**: 
- **最美的风景**: 
- **最有趣的事**: 

### 行程记录

### 美食体验

### 心情感悟

## 旅行照片
', 'travel', 3, NOW(), NOW()),

(NULL, '美食记录', '记录美食的味道、制作和背后的故事', '## 美食记录

### 基本信息
- **菜品名称**: 
- **餐厅/家**: 
- **价格**: 
- **评分**: ⭐⭐⭐⭐⭐

### 外观与口感
- **外观描述**: 
- **香气**: 
- **口感**: 
- **味道**: 

### 食材与制作
- **主要食材**: 
- **制作方法**: 
- **特色之处**: 

### 食后感想

## 美食照片
', 'food', 4, NOW(), NOW());
