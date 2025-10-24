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
package io.github.luxmixus.mybatisplus.generator.config.po;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.github.luxmixus.mybatisplus.generator.config.Configurer;
import io.github.luxmixus.mybatisplus.generator.config.common.IKeyWordsHandler;
import io.github.luxmixus.mybatisplus.generator.config.rules.IColumnType;
import io.github.luxmixus.mybatisplus.generator.config.rules.NamingStrategy;
import io.github.luxmixus.mybatisplus.generator.config.support.DataSourceConfig;
import io.github.luxmixus.mybatisplus.generator.config.support.EntityConfig;
import io.github.luxmixus.mybatisplus.generator.config.support.GlobalConfig;
import io.github.luxmixus.mybatisplus.generator.fill.Column;
import io.github.luxmixus.mybatisplus.generator.fill.Property;
import io.github.luxmixus.mybatisplus.generator.jdbc.DatabaseMetaDataWrapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.JdbcType;

import java.util.Map;

/**
 * 表字段信息
 *
 * @author YangHu
 * @since 2016-12-03
 */
@ToString
public class TableField {

    /**
     * 是否做注解转换
     */
    @Getter
    private boolean convert;

    /**
     * 是否主键
     */
    @Getter
    private boolean keyFlag;

    /**
     * 主键是否为自增类型
     */
    @Getter
    private boolean keyIdentityFlag;

    /**
     * 字段名称
     */
    @Getter
    private String name;

    /**
     * 字段类型（已弃用，使用 {@link #columnType} 代替）
     */
    @Getter
    @Deprecated
    private String type;

    /**
     * 属性名称
     */
    @Getter
    private String propertyName;

    /**
     * 字段类型
     */
    @Getter
    private IColumnType columnType;

    /**
     * 字段注释
     */
    @Getter
    private String comment;

    /**
     * 填充
     */
    private String fill;

    /**
     * 是否关键字
     *
     * @since 3.3.2
     */
    @Getter
    private boolean keyWords;

    /**
     * 数据库字段（关键字含转义符号）
     *
     * @since 3.3.2
     */
    @Getter
    private String columnName;

    /**
     * 自定义查询字段列表
     */
    @Getter
    private Map<String, Object> customMap;

    /**
     * 字段元数据信息
     *
     * @since 3.5.0
     */
    @Setter
    @Getter
    private MetaInfo metaInfo;

    /**
     * 实体属性配置
     */
    @Getter
    private final EntityConfig entity;

    /**
     * 数据库配置
     */
    private final DataSourceConfig dataSourceConfig;

    /**
     * 全局配置
     */
    private final GlobalConfig globalConfig;

    /**
     * 构造方法
     *
     * @param configAdapter 配置构建
     * @param name          数据库字段名称
     * @since 3.5.0
     */
    public TableField(Configurer configAdapter, String name) {
        this.name = name;
        this.columnName = name;
        this.entity = configAdapter.getEntityConfig();
        this.dataSourceConfig = configAdapter.getDataSourceConfig();
        this.globalConfig = configAdapter.getGlobalConfig();
    }

    /**
     * 设置属性名称
     *
     * @param propertyName 属性名
     * @param columnType   字段类型
     * @return this
     * @since 3.5.0
     */
    public TableField setPropertyName(String propertyName, IColumnType columnType) {
        this.columnType = columnType;
        if (entity.isBooleanColumnRemoveIsPrefix()
            && "boolean".equalsIgnoreCase(this.getPropertyType()) && propertyName.startsWith("is")) {
            this.convert = true;
            this.propertyName = StringUtils.removePrefixAfterPrefixToLower(propertyName, 2);
            return this;
        }
        // 下划线转驼峰策略
        if (NamingStrategy.underline_to_camel.equals(this.entity.getColumnNaming())) {
            this.convert = !propertyName.equalsIgnoreCase(NamingStrategy.underlineToCamel(this.columnName));
        }
        // 原样输出策略
        if (NamingStrategy.no_change.equals(this.entity.getColumnNaming())) {
            this.convert = !propertyName.equalsIgnoreCase(this.columnName);
        }
        if (entity.isTableFieldAnnotationEnable()) {
            this.convert = true;
        } else {
            if (this.keyFlag) {
                this.convert = !"id".equals(propertyName);
            }
        }
        this.propertyName = propertyName;
        return this;
    }

    public String getPropertyType() {
        if (null != columnType) {
            return columnType.getType();
        }
        return null;
    }

    /**
     * 按 JavaBean 规则来生成 get 和 set 方法后面的属性名称
     * 需要处理一下特殊情况：
     * <p>
     * 1、如果只有一位，转换为大写形式
     * 2、如果多于 1 位，只有在第二位是小写的情况下，才会把第一位转为小写
     * <p>
     * 我们并不建议在数据库对应的对象中使用基本类型，因此这里不会考虑基本类型的情况
     */
    public String getCapitalName() {
        if (propertyName.length() == 1) {
            return propertyName.toUpperCase();
        }
        if (Character.isLowerCase(propertyName.charAt(1))) {
            return Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        }
        return propertyName;
    }

    /**
     * 获取注解字段名称
     *
     * @return 字段
     * @since 3.3.2
     */
    public String getAnnotationColumnName() {
        if (keyWords) {
            if (columnName.startsWith("\"")) {
                return String.format("\\\"%s\\\"", name);
            }
        }
        return columnName;
    }

    /**
     * 是否为乐观锁字段
     *
     * @return 是否为乐观锁字段
     * @since 3.5.0
     */
    public boolean isVersionField() {
        String propertyName = entity.getVersionPropertyName();
        String columnName = entity.getVersionColumnName();
        return StringUtils.isNotBlank(propertyName) && this.propertyName.equals(propertyName)
            || StringUtils.isNotBlank(columnName) && this.name.equalsIgnoreCase(columnName);
    }

    /**
     * 是否为逻辑删除字段
     *
     * @return 是否为逻辑删除字段
     * @since 3.5.0
     */
    public boolean isLogicDeleteField() {
        String propertyName = entity.getLogicDeletePropertyName();
        String columnName = entity.getLogicDeleteColumnName();
        return StringUtils.isNotBlank(propertyName) && this.propertyName.equals(propertyName)
            || StringUtils.isNotBlank(columnName) && this.name.equalsIgnoreCase(columnName);
    }

    /**
     * 设置主键
     *
     * @param autoIncrement 自增标识
     * @return this
     * @since 3.5.0
     */
    public TableField primaryKey(boolean autoIncrement) {
        this.keyFlag = true;
        this.keyIdentityFlag = autoIncrement;
        return this;
    }

    /**
     * @param type 类型
     * @return this
     */
    public TableField setType(String type) {
        this.type = type;
        return this;
    }

    public TableField setComment(String comment) {
        // 待重构此处
        this.comment = (this.globalConfig.isSwagger() || this.globalConfig.isSpringdoc())
            && StringUtils.isNotBlank(comment) ? comment.replace("\"", "\\\"") : comment;
        return this;
    }

    public TableField setColumnName(String columnName) {
        this.columnName = columnName;
        IKeyWordsHandler keyWordsHandler = dataSourceConfig.getKeyWordsHandler();
        if (keyWordsHandler != null && keyWordsHandler.isKeyWords(columnName)) {
            this.keyWords = true;
            this.columnName = keyWordsHandler.formatColumn(columnName);
        }
        return this;
    }

    public TableField setCustomMap(Map<String, Object> customMap) {
        this.customMap = customMap;
        return this;
    }

    public String getFill() {
        if (StringUtils.isBlank(fill)) {
            entity.getTableFillList().stream()
                //忽略大写字段问题
                .filter(tf -> tf instanceof Column && tf.getName().equalsIgnoreCase(name)
                    || tf instanceof Property && tf.getName().equals(propertyName))
                .findFirst().ifPresent(tf -> this.fill = tf.getFieldFill().name());
        }
        return fill;
    }

    /**
     * 元数据信息
     *
     * @author nieqiurong 2021/2/8
     * @since 3.5.0
     */
    public static class MetaInfo {

        /**
         * 表名称
         */
        @Getter
        private String tableName;

        /**
         * 字段名称
         */
        @Getter
        private String columnName;

        /**
         * 字段长度
         */
        @Getter
        private int length;

        /**
         * 是否非空
         */
        @Getter
        private boolean nullable;

        /**
         * 字段注释
         */
        @Getter
        private String remarks;

        /**
         * 字段默认值
         */
        @Getter
        private String defaultValue;

        /**
         * 字段精度
         */
        @Getter
        private int scale;

        /**
         * JDBC类型
         */
        @Getter
        private JdbcType jdbcType;

        /**
         * 类型名称(可用做额外判断处理,例如在pg下,json,uuid,jsonb,tsquery这种都认为是OHTER 1111)
         *
         * @since 3.5.3
         */
        @Getter
        private String typeName;

        /**
         * 是否为生成列
         *
         * @since 3.5.8
         */
        private boolean generatedColumn;

        public MetaInfo(DatabaseMetaDataWrapper.Column column, TableInfo tableInfo) {
            if (column != null) {
                this.tableName = tableInfo.getName();
                this.columnName = column.getName();
                this.length = column.getLength();
                this.nullable = column.isNullable();
                this.remarks = column.getRemarks();
                this.defaultValue = column.getDefaultValue();
                this.scale = column.getScale();
                this.jdbcType = column.getJdbcType();
                this.typeName = column.getTypeName();
                this.generatedColumn = column.isGeneratedColumn();
            }
        }
    }
}
