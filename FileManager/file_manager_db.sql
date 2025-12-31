-- 创建数据库
CREATE DATABASE IF NOT EXISTS file_manager_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE file_manager_db;

-- 创建文件操作日志表
CREATE TABLE IF NOT EXISTS file_operation_log (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：复制/剪切/粘贴/重命名/删除',
    file_path VARCHAR(512) NOT NULL COMMENT '操作的文件/文件夹路径',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    result VARCHAR(10) NOT NULL COMMENT '操作结果：成功/失败',
    detail VARCHAR(255) DEFAULT '' COMMENT '操作详情（如重命名的新旧名称）'
) COMMENT '文件操作日志表';