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
package io.github.luxmixus.mybatisplus.generator.config.support;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.github.luxmixus.mybatisplus.generator.config.common.INameConvert;
import io.github.luxmixus.mybatisplus.generator.config.po.TableInfo;
import io.github.luxmixus.mybatisplus.generator.config.rules.IColumnType;
import io.github.luxmixus.mybatisplus.generator.config.rules.NamingStrategy;
import io.github.luxmixus.mybatisplus.generator.fill.IFill;
import io.github.luxmixus.mybatisplus.generator.fill.ITemplate;
import io.github.luxmixus.mybatisplus.generator.util.ClassUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实体属性配置
 *
 * @author nieqiurong 2020/10/11.
 * @since 3.5.0
 */
@Slf4j
public class EntityConfig implements ITemplate {

    /**
     * 自定义继承的Entity类全称，带包名
     */
    @Getter
    protected String superClass;

    /**
     * 乐观锁字段名称(数据库字段)
     *
     * @since 3.5.0
     */
    @Getter
    protected String versionColumnName;

    /**
     * 乐观锁属性名称(实体字段)
     *
     * @since 3.5.0
     */
    @Getter
    protected String versionPropertyName;

    /**
     * 逻辑删除字段名称(数据库字段)
     *
     * @since 3.5.0
     */
    @Getter
    protected String logicDeleteColumnName;

    /**
     * 逻辑删除属性名称(实体字段)
     *
     * @since 3.5.0
     */
    @Getter
    protected String logicDeletePropertyName;

    /**
     * 数据库表映射到实体的命名策略，默认下划线转驼峰命名
     */
    @Getter
    protected NamingStrategy naming = NamingStrategy.underline_to_camel;

    @Getter
    @Setter
    protected INameConvert nameConvert;
    /**
     * 数据库表字段映射到实体的命名策略
     * <p>未指定按照 naming 执行</p>
     */
    @Setter
    protected NamingStrategy columnNaming = null;
    public NamingStrategy getColumnNaming() {
        // 未指定以 naming 策略为准
        return Optional.ofNullable(columnNaming).orElse(naming);
    }

    /**
     * 自定义基础的Entity类，公共字段
     */
    protected final Set<String> superEntityColumns = new HashSet<>();

    /**
     * 自定义忽略字段
     * <a href="https://github.com/baomidou/generator/issues/46">...</a>
     */
    protected final Set<String> ignoreColumns = new HashSet<>();

    /**
     * 表填充字段
     */
    @Getter
    protected final List<IFill> tableFillList = new ArrayList<>();

    /**
     * 指定生成的主键的ID类型
     *
     * @since 3.5.0
     */
    @Getter
    protected IdType idType;

    /**
     * 实体是否生成 serialVersionUID
     */
    protected boolean serialVersionUID = true;

    /**
     * 是否启用 {@link java.io.Serial} (需JAVA 14) 注解
     *
     * @since 3.5.11
     */
    protected boolean serialAnnotation;

    /**
     * 【实体】是否生成字段常量（默认 false）<br>
     * -----------------------------------<br>
     * public static final String ID = "test_id";
     */
    protected boolean columnConstant;

    /**
     * Boolean类型字段是否移除is前缀（默认 false）<br>
     * 比如 : 数据库字段名称 : 'is_xxx',类型为 : tinyint. 在映射实体的时候则会去掉is,在实体类中映射最终结果为 xxx
     */
    @Getter
    protected boolean booleanColumnRemoveIsPrefix;

    /**
     * 是否生成实体时，生成字段注解（默认 false）
     */
    @Getter
    protected boolean tableFieldAnnotationEnable;

    /**
     * 开启 ActiveRecord 模式（默认 false）
     *
     * @since 3.5.0
     */
    @Getter
    protected boolean activeRecord;

    /**
     * <p>
     * 父类 Class 反射属性转换为公共字段
     * </p>
     *
     * @param clazz 实体父类 Class
     */
    public void convertSuperEntityColumns(Class<?> clazz) {
        List<Field> fields = TableInfoHelper.getAllFields(clazz);
        this.superEntityColumns.addAll(fields.stream().map(field -> {
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null && StringUtils.isNotBlank(tableId.value())) {
                return tableId.value();
            }
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField != null && StringUtils.isNotBlank(tableField.value())) {
                return tableField.value();
            }
            if (null == columnNaming || columnNaming == NamingStrategy.no_change) {
                return field.getName();
            }
            return StringUtils.camelToUnderline(field.getName());
        }).collect(Collectors.toSet()));
    }

    /**
     * 匹配父类字段(忽略大小写)
     *
     * @param fieldName 字段名
     * @return 是否匹配
     * @since 3.5.0
     */
    public boolean matchSuperEntityColumns(String fieldName) {
        // 公共字段判断忽略大小写【 部分数据库大小写不敏感 】
        return superEntityColumns.stream().anyMatch(e -> e.equalsIgnoreCase(fieldName));
    }

    /**
     * 匹配忽略字段(忽略大小写)
     *
     * @param fieldName 字段名
     * @return 是否匹配
     * @since 3.5.0
     */
    public boolean matchIgnoreColumns(String fieldName) {
        return ignoreColumns.stream().anyMatch(e -> e.equalsIgnoreCase(fieldName));
    }

    @Override
    public Map<String, Object> renderData(TableInfo tableInfo) {
        Map<String, Object> data = ITemplate.super.renderData(tableInfo);
        data.put("idType", idType == null ? null : idType.toString());
        data.put("logicDeleteFieldName", this.logicDeleteColumnName);
        data.put("versionFieldName", this.versionColumnName);
        data.put("activeRecord", this.activeRecord);
        data.put("entitySerialVersionUID", this.serialVersionUID);
        data.put("entitySerialAnnotation", this.serialAnnotation);
        data.put("entityColumnConstant", this.columnConstant);
        data.put("entityBooleanColumnRemoveIsPrefix", this.booleanColumnRemoveIsPrefix);
        data.put("superEntityClass", ClassUtils.getSimpleName(this.superClass));
        GlobalConfig globalConfig = tableInfo.getConfigurer().getGlobalConfig();
        ModelConfig modelConfig = tableInfo.getConfigurer().getModelConfig();

        boolean excelImport = globalConfig.isGenerateImport() && modelConfig.isQueryDTOExtendsEntity();
        boolean excelExport = globalConfig.isGenerateExport() && modelConfig.isQueryVOExtendsEntity();
        data.put("excelOnEntity", excelImport || excelExport);

        // 导入包
        Set<String> importPackages = this.entityImportPackages(tableInfo);
        Collection<String> javaPackages = importPackages.stream().filter(pkg -> pkg.startsWith("java")).collect(Collectors.toList());
        Collection<String> frameworkPackages = importPackages.stream().filter(pkg -> !pkg.startsWith("java")).collect(Collectors.toList());
        data.put("entityImportPackages4Java", javaPackages);
        data.put("entityImportPackages4Framework", frameworkPackages);
   
        return data;
    }

    /**
     * 导包处理
     *
     * @since 3.5.0
     */
    public Set<String> entityImportPackages(TableInfo tableInfo) {
        GlobalConfig globalConfig = tableInfo.getConfigurer().getGlobalConfig();
        ModelConfig modelConfig = tableInfo.getConfigurer().getModelConfig();
        TreeSet<String> importPackages = new TreeSet<>();
        if (StringUtils.isNotBlank(this.superClass)) {
            importPackages.add(this.superClass);
        } else {
            if (this.activeRecord) {
                // 无父类开启 AR 模式
                importPackages.add("com.baomidou.mybatisplus.extension.activerecord.Model");
            }
        }
        if (this.serialVersionUID || this.activeRecord) {
            importPackages.add(Serializable.class.getCanonicalName());
            if (this.serialAnnotation) {
                importPackages.add("java.io.Serial");
            }
        }
        if (tableInfo.isConvert()) {
            importPackages.add(TableName.class.getCanonicalName());
        }
        if (null != this.idType && tableInfo.isHavePrimaryKey()) {
            // 指定需要 IdType 场景
            importPackages.add(IdType.class.getCanonicalName());
            importPackages.add(TableId.class.getCanonicalName());
        }
        tableInfo.getFields().forEach(field -> {
            IColumnType columnType = field.getColumnType();
            if (null != columnType && null != columnType.getPkg()) {
                importPackages.add(columnType.getPkg());
            }
            if (field.isKeyFlag()) {
                // 主键
                if (field.isConvert() || field.isKeyIdentityFlag()) {
                    importPackages.add(TableId.class.getCanonicalName());
                }
                // 自增
                if (field.isKeyIdentityFlag()) {
                    importPackages.add(IdType.class.getCanonicalName());
                }
            } else if (field.isConvert()) {
                // 普通字段
                importPackages.add(com.baomidou.mybatisplus.annotation.TableField.class.getCanonicalName());
            }
            if (null != field.getFill()) {
                // 填充字段
                importPackages.add(com.baomidou.mybatisplus.annotation.TableField.class.getCanonicalName());
                importPackages.add(FieldFill.class.getCanonicalName());
            }
            if (field.isVersionField()) {
                importPackages.add(Version.class.getCanonicalName());
            }
            if (field.isLogicDeleteField()) {
                importPackages.add(TableLogic.class.getCanonicalName());
            }
        });
        if (globalConfig.isSpringdoc()) {
            importPackages.add("io.swagger.v3.oas.annotations.media.Schema");
        }
        if (globalConfig.isLombok()) {
            if (globalConfig.isChainModel()) {
                importPackages.add("lombok.experimental.Accessors");
            }
            if (this.superClass != null) {
                importPackages.add("lombok.EqualsAndHashCode");
            }
            if (superClass!=null || activeRecord){
                importPackages.add("lombok.EqualsAndHashCode");
            }
            importPackages.add("lombok.Data");
        }
        if (globalConfig.isSwagger()) {
            importPackages.add("io.swagger.annotations.ApiModel");
            importPackages.add("io.swagger.annotations.ApiModelProperty");
        }
        if (globalConfig.isGenerateExport() && modelConfig.isQueryVOExtendsEntity()) {
            String excelIgnoreUnannotated = globalConfig.resolveExcelClassCanonicalName("annotation.ExcelIgnoreUnannotated");
            String excelProperty = globalConfig.resolveExcelClassCanonicalName("annotation.ExcelProperty");
            importPackages.add(excelIgnoreUnannotated);
            importPackages.add(excelProperty);
        }
        return importPackages;
    }
    
    public Adapter adapter() {
        return new Adapter(this);
    }

    public static class Adapter {
        private final EntityConfig config;

        public Adapter(EntityConfig config) {
            this.config = config;
        }

        /**
         * 自定义继承的Entity类全称
         *
         * @param clazz 类
         * @return this
         */
        public Adapter superClass(Class<?> clazz) {
            return superClass(clazz.getName());
        }

        /**
         * 自定义继承的Entity类全称，带包名
         *
         * @param superEntityClass 类全称
         * @return this
         */
        public Adapter superClass(String superEntityClass) {
            this.config.superClass = superEntityClass;
            if (StringUtils.isNotBlank(superEntityClass)) {
                try {
                    Optional.of(ClassUtils.toClassConfident(superEntityClass)).ifPresent(this.config::convertSuperEntityColumns);
                } catch (Exception e) {
                    //当父类实体存在类加载器的时候,识别父类实体字段，不存在的情况就只有通过指定superEntityColumns属性了。
                }
            } else {
                if (!this.config.superEntityColumns.isEmpty()) {
                    log.warn("Forgot to set entity supper class ?");
                }
            }
            return this;
        }

        /**
         * 设置乐观锁数据库表字段名称
         *
         * @param versionColumnName 乐观锁数据库字段名称
         * @return this
         */
        public Adapter versionColumnName(String versionColumnName) {
            this.config.versionColumnName = versionColumnName;
            return this;
        }

        /**
         * 设置乐观锁实体属性字段名称
         *
         * @param versionPropertyName 乐观锁实体属性字段名称
         * @return this
         */
        public Adapter versionPropertyName(String versionPropertyName) {
            this.config.versionPropertyName = versionPropertyName;
            return this;
        }

        /**
         * 逻辑删除数据库字段名称
         *
         * @param logicDeleteColumnName 逻辑删除字段名称
         * @return this
         */
        public Adapter logicDeleteColumnName(String logicDeleteColumnName) {
            this.config.logicDeleteColumnName = logicDeleteColumnName;
            return this;
        }

        /**
         * 逻辑删除实体属性名称
         *
         * @param logicDeletePropertyName 逻辑删除实体属性名称
         * @return this
         */
        public Adapter logicDeletePropertyName(String logicDeletePropertyName) {
            this.config.logicDeletePropertyName = logicDeletePropertyName;
            return this;
        }

        /**
         * 数据库表映射到实体的命名策略
         *
         * @param namingStrategy 数据库表映射到实体的命名策略
         * @return this
         */
        public Adapter naming(NamingStrategy namingStrategy) {
            this.config.naming = namingStrategy;
            return this;
        }

        /**
         * 数据库表字段映射到实体的命名策略
         *
         * @param namingStrategy 数据库表字段映射到实体的命名策略
         * @return this
         */
        public Adapter columnNaming(NamingStrategy namingStrategy) {
            this.config.columnNaming = namingStrategy;
            return this;
        }

        /**
         * 添加父类公共字段
         *
         * @param superEntityColumns 父类字段(数据库字段列名)
         * @return this
         * @since 3.5.0
         */
        public Adapter addSuperEntityColumns(String... superEntityColumns) {
            return addSuperEntityColumns(Arrays.asList(superEntityColumns));
        }

        public Adapter addSuperEntityColumns(List<String> superEntityColumnList) {
            this.config.superEntityColumns.addAll(superEntityColumnList);
            return this;
        }

        /**
         * 添加忽略字段
         *
         * @param ignoreColumns 需要忽略的字段(数据库字段列名)
         * @return this
         * @since 3.5.0
         */
        public Adapter addIgnoreColumns(String... ignoreColumns) {
            return addIgnoreColumns(Arrays.asList(ignoreColumns));
        }

        public Adapter addIgnoreColumns(List<String> ignoreColumnList) {
            this.config.ignoreColumns.addAll(ignoreColumnList);
            return this;
        }

        /**
         * 添加表字段填充
         *
         * @param tableFills 填充字段
         * @return this
         * @since 3.5.0
         */
        public Adapter addTableFills(IFill... tableFills) {
            return addTableFills(Arrays.asList(tableFills));
        }

        /**
         * 添加表字段填充
         *
         * @param tableFillList 填充字段集合
         * @return this
         * @since 3.5.0
         */
        public Adapter addTableFills(List<IFill> tableFillList) {
            this.config.tableFillList.addAll(tableFillList);
            return this;
        }

        /**
         * 指定生成的主键的ID类型
         *
         * @param idType ID类型
         * @return this
         * @since 3.5.0
         */
        public Adapter idType(IdType idType) {
            this.config.idType = idType;
            return this;
        }

        /**
         * 启用生成 {@link java.io.Serial} (需JAVA 14)
         * <p>当开启了 {@link #serialVersionUID} 时,会增加 {@link java.io.Serial} 注解在此字段上</p>
         *
         * @return this
         * @since 3.5.11
         */
        public Adapter enableSerialAnnotation() {
            this.config.serialAnnotation = true;
            return this;
        }

        /**
         * 开启生成字段常量
         *
         * @return this
         * @since 3.5.0
         */
        public Adapter enableColumnConstant() {
            this.config.columnConstant = true;
            return this;
        }

        /**
         * 开启Boolean类型字段移除is前缀
         *
         * @return this
         * @since 3.5.0
         */
        public Adapter enableRemoveIsPrefix() {
            this.config.booleanColumnRemoveIsPrefix = true;
            return this;
        }

        /**
         * 开启生成实体时生成字段注解
         *
         * @return this
         * @since 3.5.0
         */
        public Adapter enableTableFieldAnnotation() {
            this.config.tableFieldAnnotationEnable = true;
            return this;
        }

        /**
         * 开启 ActiveRecord 模式
         *
         * @return this
         * @since 3.5.0
         */
        public Adapter enableActiveRecord() {
            this.config.activeRecord = true;
            return this;
        }

        /**
         * 禁用生成serialVersionUID
         *
         * @return this
         * @since 3.5.0
         */
        public Adapter disableSerialVersionUID() {
            this.config.serialVersionUID = false;
            return this;
        }
    }
}