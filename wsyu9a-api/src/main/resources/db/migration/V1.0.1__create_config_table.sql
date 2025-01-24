-- 先删除表（如果存在）
DROP TABLE IF EXISTS sys_config;

-- 重新创建表
CREATE TABLE sys_config (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `docker_api` varchar(255) DEFAULT NULL COMMENT 'Docker API地址',
                            `docker_registry` varchar(255) DEFAULT NULL COMMENT 'Docker镜像仓库地址',
                            `docker_max_port` int DEFAULT NULL COMMENT 'Docker容器最大端口',
                            `docker_min_port` int DEFAULT NULL COMMENT 'Docker容器最小端口',
                            `docker_time` int DEFAULT NULL COMMENT 'Docker容器超时时间(秒)',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入默认配置
INSERT INTO `sys_config` (`id`, `docker_api`, `docker_registry`, `docker_max_port`, `docker_min_port`, `docker_time`)
VALUES
    (1, 'tcp://localhost:2376', 'http://hub-mirror.c.163.com', 50000, 40000, 7200),
    (2, 'tcp://localhost:2376', 'http://hub-mirror.c.163.com', 50000, 40000, 7200);