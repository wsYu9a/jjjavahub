/*
 Navicat Premium Dump SQL

 Source Server         : wsyu9a
 Source Server Type    : MySQL
 Source Server Version : 50726 (5.7.26)
 Source Host           : localhost:3306
 Source Schema         : wsyu9a

 Target Server Type    : MySQL
 Target Server Version : 50726 (5.7.26)
 File Encoding         : 65001

 Date: 09/02/2025 07:57:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `important` tinyint(1) NULL DEFAULT 0,
  `enabled` tinyint(1) NULL DEFAULT 1,
  `publisher_id` int(11) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcement
-- ----------------------------
INSERT INTO `announcement` VALUES (1, '测试', '这是一条测试的公告！', 0, 1, 1, '2024-01-01 17:56:12', '2025-01-26 06:30:27', 0);
INSERT INTO `announcement` VALUES (2, '测试2', '这也是测试的公告', 0, 1, 1, '2024-12-31 17:56:24', '2025-01-26 06:30:46', 0);
INSERT INTO `announcement` VALUES (3, '最新通知', '新增题目，easysql', 1, 1, 1, '2025-01-01 20:39:38', '2025-01-01 20:39:38', 0);

-- ----------------------------
-- Table structure for problem
-- ----------------------------
DROP TABLE IF EXISTS `problem`;
CREATE TABLE `problem`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '漏洞标题',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '漏洞简述',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目说明文件路径',
  `flag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'flag',
  `score` int(11) NOT NULL COMMENT '积分',
  `difficulty` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '难度',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `docker_compose_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'docker-compose文件路径',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `attachment_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件路径',
  `submit_count` int(11) NULL DEFAULT 0,
  `solved_count` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category_id`) USING BTREE,
  CONSTRAINT `fk_problem_category` FOREIGN KEY (`category_id`) REFERENCES `problem_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of problem
-- ----------------------------
INSERT INTO `problem` VALUES (25, '强网杯 2019 随便注', 'SQL 堆叠注入', 'readme/ad4db43f-06f5-4689-ac84-749ee803147f/README.md', 'flag{2DFATOoBBAGZ7XBS}', 50, 'MEDIUM', 1, 'docker-compose/4f43bf32-7d5e-4070-8fc8-5000842a654d/docker-compose.yml', 1, '2025-01-25 02:08:06', '2025-02-09 06:48:03', 'fujian/bddffa35-2805-4d87-9d1d-2e873bcb4308/1.zip', 5, 3);
INSERT INTO `problem` VALUES (26, 'easyupload', 'easy上传', 'readme/9673ca44-6fd3-4c40-8c88-65f1cac40dec/README.md', 'flag{F2D7WTPt3v6sGCIp}', 50, 'MEDIUM', 3, 'docker-compose/d8ce0048-845e-47cf-819c-1b53cb36284d/docker-compose.yml', 1, '2025-01-25 14:36:55', '2025-01-26 00:55:56', 'fujian/e8699946-f948-496a-8468-7b87a328383e/附件.txt', 0, 0);
INSERT INTO `problem` VALUES (28, 'test', 'xxx', 'readme/8749183f-96a1-410d-b881-c2c4489cacfc/README.md', 'flag{vm4cwtA0zHMuAmkB}', 100, 'HARD', 3, 'docker-compose/8cd885e2-a934-4996-aeef-805c5cc70524/docker-compose.yml', 1, '2025-01-26 03:09:00', '2025-01-26 03:09:00', 'fujian/13ce5bc2-12ad-4a07-b200-b1052f62ffeb/附件.txt', 0, NULL);

-- ----------------------------
-- Table structure for problem_category
-- ----------------------------
DROP TABLE IF EXISTS `problem_category`;
CREATE TABLE `problem_category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '题目分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of problem_category
-- ----------------------------
INSERT INTO `problem_category` VALUES (1, 'SQL注入', 'SQL注入类漏洞，包括各种数据库的注入漏洞', '2025-01-01 17:35:14', '2025-01-01 17:35:14');
INSERT INTO `problem_category` VALUES (2, 'XSS', '跨站脚本攻击漏洞，包括反射型、存储型和DOM型XSS', '2025-01-01 17:35:14', '2025-01-01 17:35:14');
INSERT INTO `problem_category` VALUES (3, '文件上传', '文件上传漏洞，包括绕过文件类型限制、文件解析漏洞等', '2025-01-01 17:35:14', '2025-01-01 17:35:14');
INSERT INTO `problem_category` VALUES (4, '命令注入', '命令注入漏洞，包括系统命令注入、代码执行等', '2025-01-01 17:35:14', '2025-01-01 17:35:14');
INSERT INTO `problem_category` VALUES (5, '逻辑漏洞', '业务逻辑漏洞，包括越权、支付漏洞等', '2025-01-01 17:35:14', '2025-01-01 17:35:14');
INSERT INTO `problem_category` VALUES (6, 'qwe', 'qwe', '2025-01-26 03:08:09', '2025-01-26 03:08:09');

-- ----------------------------
-- Table structure for submission
-- ----------------------------
DROP TABLE IF EXISTS `submission`;
CREATE TABLE `submission`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `problem_id` bigint(20) NOT NULL,
  `flag` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `correct` tinyint(1) NOT NULL,
  `submit_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `problem_id`(`problem_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 142 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of submission
-- ----------------------------
INSERT INTO `submission` VALUES (1, 1, 25, 'flag{2DFATOoBBAGZ7XBS}', 1, '2025-01-25 23:07:37');
INSERT INTO `submission` VALUES (141, 1, 25, 'flag{2DFATOoBBAGZ7XB}', 0, '2025-01-27 14:12:26');
INSERT INTO `submission` VALUES (140, 3, 28, 'flag{}', 1, '2025-01-09 03:10:13');
INSERT INTO `submission` VALUES (139, 3, 25, '1232', 0, '2025-01-26 02:16:05');
INSERT INTO `submission` VALUES (2, 2, 25, 'flag{2DFATOoBBAGZ7XBS}', 1, '2025-01-26 01:19:39');
INSERT INTO `submission` VALUES (3, 3, 25, 'flag{2DFATOoBBAGZ7XBS}', 1, '2025-01-26 01:28:47');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `docker_api` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Docker API地址',
  `docker_registry` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Docker镜像仓库地址',
  `docker_max_port` int(11) NULL DEFAULT NULL COMMENT 'Docker容器最大端口',
  `docker_min_port` int(11) NULL DEFAULT NULL COMMENT 'Docker容器最小端口',
  `docker_time` int(11) NULL DEFAULT NULL COMMENT 'Docker容器超时时间(秒)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'tcp://localhost:2376', 'http://hub-mirror.c.163.com', 50000, 40000, 7200, '2025-01-24 14:31:14', '2025-01-24 14:31:14');
INSERT INTO `sys_config` VALUES (2, 'tcp://localhost:2376', 'http://hub-mirror.c.163.com', 50000, 40000, 7200, '2025-01-24 14:31:14', '2025-02-09 07:16:52');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'USER' COMMENT '角色',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `score` int(11) NULL DEFAULT 0,
  `avatar` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户头像URL',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE,
  UNIQUE INDEX `uk_email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'admin@wsyu9a.com', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'ADMIN', 1, '2025-01-01 17:35:14', '2025-02-09 05:48:44', 0, 9999, '/9j/4AAQSkZJRgepDUF3KFwxz1qRxcI77cYHvXO9RrDvc5z/AIRK0HH2Ff8AvinDwpaFgTYqfqlbvmzdz/49R50o7/8Aj1H7s2VK2xjDwhYXQ==');
INSERT INTO `sys_user` VALUES (2, 'test', 'test@qq.com', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'ADMIN', 1, '2025-01-01 17:48:26', '2025-02-09 04:37:16', 0, 100, '');
INSERT INTO `sys_user` VALUES (3, 'test2', 'test2@qq.com', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'USER', 1, '2025-01-24 04:56:40', '2025-02-09 04:37:19', 0, 50, '');
INSERT INTO `sys_user` VALUES (4, 'test3', 'test3@qq.com', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'USER', 1, '2025-01-25 05:01:01', '2025-02-09 04:37:21', 0, 0, '');

SET FOREIGN_KEY_CHECKS = 1;
