DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `id` bigint NOT NULL COMMENT '主键',
                             `deleted` int NOT NULL DEFAULT 0 COMMENT '删除标志',
                             `version` int NOT NULL DEFAULT 1 COMMENT '版本号',
                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                             `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '姓名',
    `name_like` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名Like后缀',
    `age` int NOT NULL COMMENT '年龄',
    `birth_date` date NOT NULL COMMENT '生日',
    `state` int NOT NULL DEFAULT 1 COMMENT '状态',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 0, 1, '2024-12-13 11:38:20', '2024-12-13 11:38:20', '张三', '张三', 3, '2003-01-01', 1);
INSERT INTO `sys_user` VALUES (2, 0, 1, '2024-12-13 11:38:20', '2024-12-13 11:38:20', '李四', '李四', 4, '2004-01-01', 2);
INSERT INTO `sys_user` VALUES (3, 0, 1, '2024-12-13 11:38:20', '2024-12-13 11:38:20', '王五', '王五', 5, '2005-01-01', 3);
INSERT INTO `sys_user` VALUES (4, 0, 1, '2024-12-13 11:38:20', '2024-12-13 11:38:20', '赵六', '赵六', 6, '2006-01-01', 4);
INSERT INTO `sys_user` VALUES (5, 0, 1, '2024-12-13 11:38:20', '2024-12-13 11:38:20', '田七', '田七', 7, '2007-01-01', 5);
