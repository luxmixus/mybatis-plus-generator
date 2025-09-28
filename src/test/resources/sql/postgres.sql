DROP TABLE IF EXISTS "public"."sys_user";
CREATE TABLE "public"."sys_user" (
                                     "id" int8 NOT NULL,
                                     "deleted" int4 NOT NULL DEFAULT 0,
                                     "version" int4 NOT NULL DEFAULT 1,
                                     "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     "name" varchar(50) COLLATE "pg_catalog"."default" NULL,
                                     "name_like" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
                                     "age" int4 NOT NULL,
                                     "birth_date" date NOT NULL,
                                     "state" int4 NOT NULL DEFAULT 1
)
;
COMMENT ON COLUMN "public"."sys_user"."id" IS 'id';
COMMENT ON COLUMN "public"."sys_user"."deleted" IS '逻辑删除标志';
COMMENT ON COLUMN "public"."sys_user"."version" IS '版本号';
COMMENT ON COLUMN "public"."sys_user"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_user"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_user"."name" IS '姓名';
COMMENT ON COLUMN "public"."sys_user"."name_like" IS '姓名Like后缀';
COMMENT ON COLUMN "public"."sys_user"."age" IS '年龄';
COMMENT ON COLUMN "public"."sys_user"."birth_date" IS '生日';
COMMENT ON COLUMN "public"."sys_user"."state" IS '状态';
COMMENT ON TABLE "public"."sys_user" IS '用户';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO "public"."sys_user" VALUES (1, 0, 1, '2024-12-13 11:38:20', '2024-12-13 11:38:20', '张三', '张三', 3, '2003-01-01', 1);
INSERT INTO "public"."sys_user" VALUES (2, 0, 1, '2024-12-13 11:38:20', '2024-12-13 11:38:20', '李四', '李四', 4, '2004-01-01', 1);
INSERT INTO "public"."sys_user" VALUES (3, 0, 1, '2024-12-13 11:38:20', '2024-12-13 11:38:20', '王五', '王五', 5, '2005-01-01', 1);
INSERT INTO "public"."sys_user" VALUES (4, 0, 1, '2024-12-13 11:38:20', '2024-12-13 11:38:20', '赵六', '赵六', 6, '2006-01-01', 1);
INSERT INTO "public"."sys_user" VALUES (5, 0, 1, '2024-12-13 11:38:20', '2024-12-13 11:38:20', '田七', '田七', 7, '2007-01-01', 1);
