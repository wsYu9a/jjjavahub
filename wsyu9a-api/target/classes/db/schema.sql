CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `email` varchar(100) NOT NULL COMMENT '邮箱',
    `password` varchar(100) NOT NULL COMMENT '密码',
    `role` varchar(20) NOT NULL DEFAULT 'USER' COMMENT '角色',
    `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建默认管理员账户，密码为 admin123（如果不存在）
INSERT INTO `sys_user` (`username`, `email`, `password`, `role`) 
SELECT 'admin', 'admin@wsyu9a.com', '$2a$10$X/hX4qvWTWNJ0TwRo4nK3OiR0b9YhGHXQHBUF5pXHxgNsL8fNYFVu', 'ADMIN'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_user` WHERE `username` = 'admin');

-- 先创建分类表，因为题目表需要引用它作为外键
CREATE TABLE IF NOT EXISTS `problem_category` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` varchar(50) NOT NULL COMMENT '分类名称',
    `description` varchar(200) DEFAULT NULL COMMENT '分类描述',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目分类表';

-- 题目表
CREATE TABLE IF NOT EXISTS `problem` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` varchar(100) NOT NULL COMMENT '漏洞标题',
    `summary` text NOT NULL COMMENT '漏洞简述',
    `detail` text NOT NULL COMMENT '漏洞详解',
    `flag` varchar(100) NOT NULL COMMENT 'flag',
    `score` int(11) NOT NULL COMMENT '积分',
    `difficulty` varchar(20) NOT NULL COMMENT '难度',
    `category_id` bigint(20) NOT NULL COMMENT '分类ID',
    `docker_compose_path` varchar(200) DEFAULT NULL COMMENT 'docker-compose文件路径',
    `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    CONSTRAINT `fk_problem_category` FOREIGN KEY (`category_id`) REFERENCES `problem_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 添加默认分类数据（如果不存在）
INSERT IGNORE INTO `problem_category` (`name`, `description`) VALUES 
('SQL注入', 'SQL注入类漏洞，包括各种数据库的注入漏洞'),
('XSS', '跨站脚本攻击漏洞，包括反射型、存储型和DOM型XSS'),
('文件上传', '文件上传漏洞，包括绕过文件类型限制、文件解析漏洞等'),
('命令注入', '命令注入漏洞，包括系统命令注入、代码执行等'),
('逻辑漏洞', '业务逻辑漏洞，包括越权、支付漏洞等'); 

CREATE TABLE IF NOT EXISTS `announcement` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` varchar(100) NOT NULL COMMENT '公告标题',
    `content` text NOT NULL COMMENT '公告内容',
    `important` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否重要',
    `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
    `publisher_id` bigint(20) NOT NULL COMMENT '发布者ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_publisher` (`publisher_id`),
    CONSTRAINT `fk_announcement_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表'; 