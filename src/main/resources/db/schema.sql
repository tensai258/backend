CREATE DATABASE IF NOT EXISTS zhixuebanxing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE zhixuebanxing;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    nickname VARCHAR(64) COMMENT '昵称',
    email VARCHAR(128) COMMENT '邮箱',
    phone VARCHAR(32) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    role INT DEFAULT 0 COMMENT '角色：0学生 1教师 2管理员',
    status INT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    class_id VARCHAR(64) COMMENT '班级ID',
    grade VARCHAR(32) COMMENT '年级',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 课程表
CREATE TABLE IF NOT EXISTS course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(128) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(64) COMMENT '课程代码',
    description TEXT COMMENT '课程描述',
    teacher_id BIGINT COMMENT '教师ID',
    subject VARCHAR(64) COMMENT '学科',
    grade VARCHAR(32) COMMENT '适用年级',
    status INT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_teacher (teacher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 知识点表
CREATE TABLE IF NOT EXISTS knowledge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL COMMENT '知识点名称',
    description TEXT COMMENT '描述',
    course_id BIGINT COMMENT '所属课程ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父知识点ID',
    level INT DEFAULT 1 COMMENT '层级',
    category VARCHAR(64) COMMENT '分类',
    tags VARCHAR(255) COMMENT '标签',
    difficulty DOUBLE DEFAULT 0.5 COMMENT '难度系数',
    importance INT DEFAULT 1 COMMENT '重要程度',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_course (course_id),
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';

-- 题库表
CREATE TABLE IF NOT EXISTS question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL COMMENT '题目内容',
    options TEXT COMMENT '选项(JSON)',
    answer TEXT COMMENT '答案',
    analysis TEXT COMMENT '解析',
    type INT DEFAULT 0 COMMENT '题型：0单选 1多选 2填空 3判断 4简答 5论述',
    course_id BIGINT COMMENT '课程ID',
    knowledge_id BIGINT COMMENT '知识点ID',
    difficulty DOUBLE DEFAULT 0.5 COMMENT '难度',
    score INT DEFAULT 0 COMMENT '分值',
    tags VARCHAR(255) COMMENT '标签',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_course (course_id),
    INDEX idx_knowledge (knowledge_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题库表';

-- 作业表
CREATE TABLE IF NOT EXISTS assignment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '标题',
    description TEXT COMMENT '描述',
    course_id BIGINT COMMENT '课程ID',
    teacher_id BIGINT COMMENT '教师ID',
    question_ids VARCHAR(512) COMMENT '题目ID列表',
    status INT DEFAULT 1 COMMENT '状态：0草稿 1已发布 2已关闭',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '截止时间',
    total_score INT DEFAULT 100 COMMENT '总分',
    time_limit INT COMMENT '时间限制(分钟)',
    allow_retry INT DEFAULT 0 COMMENT '是否允许重做',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_course (course_id),
    INDEX idx_teacher (teacher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';

-- 作业提交表
CREATE TABLE IF NOT EXISTS submission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assignment_id BIGINT NOT NULL COMMENT '作业ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    answers TEXT COMMENT '答案(JSON)',
    score INT COMMENT '得分',
    feedback TEXT COMMENT '评语',
    status INT DEFAULT 0 COMMENT '状态：0待批改 1已批改',
    submit_time DATETIME COMMENT '提交时间',
    time_spent INT COMMENT '耗时(秒)',
    ai_graded INT DEFAULT 0 COMMENT '是否AI批改',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_assignment (assignment_id),
    INDEX idx_student (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交表';

-- 对话记录表
CREATE TABLE IF NOT EXISTS dialogue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '用户ID',
    session_id VARCHAR(64) COMMENT '会话ID',
    role VARCHAR(16) COMMENT '角色：user/assistant/system',
    content TEXT COMMENT '内容',
    model VARCHAR(64) COMMENT '模型',
    tokens INT COMMENT 'token数',
    status INT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_user (user_id),
    INDEX idx_session (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话记录表';

-- 学习记录表
CREATE TABLE IF NOT EXISTS study_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT COMMENT '课程ID',
    knowledge_id BIGINT COMMENT '知识点ID',
    study_type INT DEFAULT 0 COMMENT '学习类型',
    duration INT COMMENT '学习时长(秒)',
    score INT COMMENT '得分',
    content TEXT COMMENT '学习内容',
    study_date DATE COMMENT '学习日期',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_user (user_id),
    INDEX idx_course (course_id),
    INDEX idx_date (study_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习记录表';

-- 知识图谱关系表
CREATE TABLE IF NOT EXISTS kg_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_id BIGINT NOT NULL COMMENT '源知识点ID',
    target_id BIGINT NOT NULL COMMENT '目标知识点ID',
    relation_type VARCHAR(64) COMMENT '关系类型：prerequisite/related/successor',
    description VARCHAR(255) COMMENT '关系描述',
    weight DOUBLE DEFAULT 1.0 COMMENT '权重',
    course_id BIGINT COMMENT '课程ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_source (source_id),
    INDEX idx_target (target_id),
    INDEX idx_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识图谱关系表';
