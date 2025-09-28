/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.bootystar.mybatisplus.generator.config.support;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.github.bootystar.mybatisplus.generator.config.converts.MySqlTypeConvert;
import io.github.bootystar.mybatisplus.generator.config.converts.TypeConverts;
import io.github.bootystar.mybatisplus.generator.config.common.IDbQuery;
import io.github.bootystar.mybatisplus.generator.config.common.IKeyWordsHandler;
import io.github.bootystar.mybatisplus.generator.config.common.ITypeConvert;
import io.github.bootystar.mybatisplus.generator.config.querys.DbQueryDecorator;
import io.github.bootystar.mybatisplus.generator.config.querys.DbQueryRegistry;
import io.github.bootystar.mybatisplus.generator.query.AbstractDatabaseQuery;
import io.github.bootystar.mybatisplus.generator.query.DefaultQuery;
import io.github.bootystar.mybatisplus.generator.query.SQLQuery;
import io.github.bootystar.mybatisplus.generator.type.ITypeConvertHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * 数据库配置
 *
 * @author YangHu, hcl, hubin
 * @since 2016/8/30
 */
@Slf4j
public class DataSourceConfig {
    public DataSourceConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * 数据库信息查询
     */
    protected IDbQuery dbQuery;
    public IDbQuery getDbQuery() {
        if (null == dbQuery) {
            DbType dbType = getDbType();
            DbQueryRegistry dbQueryRegistry = new DbQueryRegistry();
            // 默认 MYSQL
            dbQuery = Optional.ofNullable(dbQueryRegistry.getDbQuery(dbType)).orElseGet(() -> dbQueryRegistry.getDbQuery(DbType.MYSQL));
        }
        return dbQuery;
    }

    /**
     * schemaName
     */
    @Getter
    protected String schemaName;

    /**
     * 类型转换
     */
    protected ITypeConvert typeConvert;

    /**
     * 关键字处理器
     *
     * @since 3.3.2
     */
    @Getter
    protected IKeyWordsHandler keyWordsHandler;

    /**
     * 驱动连接的URL
     */
    @Getter
    protected String url;

    /**
     * 数据库连接用户名
     */
    @Getter
    protected String username;

    /**
     * 数据库连接密码
     */
    @Getter
    protected String password;

    /**
     * 数据源实例
     *
     * @since 3.5.0
     */
    protected DataSource dataSource;

    /**
     * 数据库连接
     *
     * @since 3.5.0
     */
    protected Connection connection;

    /**
     * 数据库连接属性
     *
     * @since 3.5.3
     */
    protected final Map<String, String> connectionProperties = new HashMap<>();

    /**
     * 查询方式
     *
     * @since 3.5.3
     */
    @Getter
    protected Class<? extends AbstractDatabaseQuery> databaseQueryClass = DefaultQuery.class;

    /**
     * 类型转换处理
     *
     * @since 3.5.3
     */
    @Getter
    protected ITypeConvertHandler typeConvertHandler;
    public ITypeConvert getTypeConvert() {
        if (null == typeConvert) {
            DbType dbType = getDbType();
            // 默认 MYSQL
            typeConvert = TypeConverts.getTypeConvert(dbType);
            if (null == typeConvert) {
                typeConvert = MySqlTypeConvert.INSTANCE;
            }
        }
        return typeConvert;
    }

    /**
     * 驱动全类名
     *
     * @since 3.5.8
     */
    @Getter
    protected String driverClassName;

    /**
     * 判断数据库类型
     *
     * @return 类型枚举值
     */
    public DbType getDbType() {
        return this.getDbType(this.url.toLowerCase());
    }

    /**
     * 判断数据库类型
     *
     * @param str url
     * @return 类型枚举值，如果没找到，则返回 null
     */
    protected DbType getDbType(String str) {
        if (str.contains(":mysql:") || str.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (str.contains(":oracle:")) {
            return DbType.ORACLE;
        } else if (str.contains(":postgresql:")) {
            return DbType.POSTGRE_SQL;
        } else if (str.contains(":sqlserver:")) {
            return DbType.SQL_SERVER;
        } else if (str.contains(":db2:")) {
            return DbType.DB2;
        } else if (str.contains(":mariadb:")) {
            return DbType.MARIADB;
        } else if (str.contains(":sqlite:")) {
            return DbType.SQLITE;
        } else if (str.contains(":h2:")) {
            return DbType.H2;
//        } else if (str.contains(":lealone:")) {
//            return DbType.LEALONE;
        } else if (str.contains(":kingbase:") || str.contains(":kingbase8:")) {
            return DbType.KINGBASE_ES;
        } else if (str.contains(":dm:")) {
            return DbType.DM;
        } else if (str.contains(":zenith:")) {
            return DbType.GAUSS;
        } else if (str.contains(":oscar:")) {
            return DbType.OSCAR;
        } else if (str.contains(":firebird:")) {
            return DbType.FIREBIRD;
        } else if (str.contains(":xugu:")) {
            return DbType.XU_GU;
        } else if (str.contains(":clickhouse:")) {
            return DbType.CLICK_HOUSE;
        } else if (str.contains(":sybase:")) {
            return DbType.SYBASE;
        } else {
            return DbType.OTHER;
        }
    }

    /**
     * 创建数据库连接对象
     * 这方法建议只调用一次，毕竟只是代码生成，用一个连接就行。
     *
     * @return Connection
     */
    public Connection getConn() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            } else {
                synchronized (this) {
                    if (dataSource != null) {
                        connection = dataSource.getConnection();
                    } else {
                        Properties properties = new Properties();
                        connectionProperties.forEach(properties::setProperty);
                        properties.put("user", username);
                        properties.put("password", password);
                        // 使用元数据查询方式时，有些数据库需要增加属性才能读取注释
                        this.processProperties(properties);
                        this.connection = DriverManager.getConnection(url, properties);
                        if (StringUtils.isBlank(this.schemaName)) {
                            try {
                                this.schemaName = connection.getSchema();
                            } catch (Exception exception) {
                                // ignore 老古董1.7以下的驱动不支持.
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    protected void processProperties(Properties properties) {
        if (this.databaseQueryClass.getName().equals(DefaultQuery.class.getName())) {
            switch (this.getDbType()) {
                case MYSQL:
                    properties.put("remarks", "true");
                    properties.put("useInformationSchema", "true");
                    break;
                case ORACLE:
                    properties.put("remarks", "true");
                    properties.put("remarksReporting", "true");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 获取数据库默认schema
     *
     * @return 默认schema
     * @since 3.5.0
     */
    protected String getDefaultSchema() {
        DbType dbType = getDbType();
        String schema = null;
        if (DbType.POSTGRE_SQL == dbType) {
            //pg 默认 schema=public
            schema = "public";
        } else if (DbType.KINGBASE_ES == dbType) {
            //kingbase 默认 schema=PUBLIC
            schema = "PUBLIC";
        } else if (DbType.DB2 == dbType) {
            //db2 默认 schema=current schema
            schema = "current schema";
        } else if (DbType.ORACLE == dbType) {
            //oracle 默认 schema=username
            schema = this.username.toUpperCase();
        }
        return schema;
    }

    public Adapter adapter() {
        return new Adapter(this);
    }

    public static class Adapter {
        private final DataSourceConfig config;

        public Adapter(DataSourceConfig config) {
            this.config = config;
        }

        /**
         * 设置数据库查询实现
         *
         * @param dbQuery 数据库查询实现
         * @return this
         */
        public Adapter dbQuery(IDbQuery dbQuery) {
            this.config.dbQuery = dbQuery;
            return this;
        }

        /**
         * 设置数据库schema
         *
         * @param schemaName 数据库schema
         * @return this
         */
        public Adapter schema(String schemaName) {
            this.config.schemaName = schemaName;
            return this;
        }

        /**
         * 设置类型转换器
         *
         * @param typeConvert 类型转换器
         * @return this
         */
        public Adapter typeConvert(ITypeConvert typeConvert) {
            this.config.typeConvert = typeConvert;
            return this;
        }

        /**
         * 设置数据库关键字处理器
         *
         * @param keyWordsHandler 关键字处理器
         * @return this
         */
        public Adapter keyWordsHandler(IKeyWordsHandler keyWordsHandler) {
            this.config.keyWordsHandler = keyWordsHandler;
            return this;
        }

        /**
         * 指定数据库查询方式
         *
         * @param databaseQueryClass 查询类
         * @return this
         * @since 3.5.3
         */
        public Adapter databaseQueryClass(Class<? extends AbstractDatabaseQuery> databaseQueryClass) {
            this.config.databaseQueryClass = databaseQueryClass;
            return this;
        }

        /**
         * 指定类型转换器
         *
         * @param typeConvertHandler 类型转换器
         * @return this
         * @since 3.5.3
         */
        public Adapter typeConvertHandler(ITypeConvertHandler typeConvertHandler) {
            this.config.typeConvertHandler = typeConvertHandler;
            return this;
        }

        /**
         * 增加数据库连接属性
         *
         * @param key   属性名
         * @param value 属性值
         * @return this
         * @since 3.5.3
         */
        public Adapter addConnectionProperty(String key, String value) {
            this.config.connectionProperties.put(key, value);
            return this;
        }

        /**
         * 指定连接驱动
         * <li>对于一些老驱动(低于4.0规范)没有实现SPI不能自动加载的,手动指定加载让其初始化注册到驱动列表去.</li>
         *
         * @param className 驱动全类名
         * @return this
         * @since 3.5.8
         */
        public Adapter driverClassName(String className) {
            com.baomidou.mybatisplus.core.toolkit.ClassUtils.toClassConfident(className);
            this.config.driverClassName = className;
            return this;
        }
    }
}