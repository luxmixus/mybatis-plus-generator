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

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.github.luxmixus.mybatisplus.generator.config.enums.OutputFile;
import io.github.luxmixus.mybatisplus.generator.config.po.TableField;
import io.github.luxmixus.mybatisplus.generator.config.po.TableInfo;
import io.github.luxmixus.mybatisplus.generator.fill.ITemplate;
import io.github.luxmixus.mybatisplus.generator.util.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.decorators.LoggingCache;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mapper属性配置
 *
 * @author nieqiurong 2020/10/11.
 * @since 3.5.0
 */
@Slf4j
public class MapperConfig implements ITemplate {

    /**
     * 自定义继承的Mapper类全称，带包名
     */
    protected String superClass = "com.baomidou.mybatisplus.core.mapper.BaseMapper";

    /**
     * Mapper标记注解
     *
     * @since 3.5.3
     */
    protected Class<? extends Annotation> mapperAnnotationClass = org.apache.ibatis.annotations.Mapper.class;

    /**
     * 是否开启BaseResultMap（默认 false）
     *
     * @since 3.5.0
     */
    protected boolean baseResultMap;

    /**
     * 是否开启baseColumnList（默认 false）
     *
     * @since 3.5.0
     */
    protected boolean baseColumnList;

    /**
     * 设置缓存实现类
     *
     * @since 3.5.0
     */
    protected Class<? extends Cache> cache;
    public Class<? extends Cache> getCache() {
        return this.cache == null ? LoggingCache.class : this.cache;
    }

    /**
     * 排序字段map
     * 字段名 -> 是否倒序
     */
    protected Map<String, Boolean> sortColumnMap = new LinkedHashMap<>();

    @Override
    public Map<String, Object> renderData(TableInfo tableInfo) {
        Map<String, Object> data = ITemplate.super.renderData(tableInfo);
        boolean enableCache = this.cache != null;
        data.put("enableCache", enableCache);
        data.put("mapperAnnotation", mapperAnnotationClass != null);
        data.put("mapperAnnotationClass", mapperAnnotationClass);
        data.put("baseResultMap", this.baseResultMap);
        data.put("baseColumnList", this.baseColumnList);
        data.put("superMapperClassPackage", this.superClass);
        if (enableCache) {
            Class<? extends Cache> cacheClass = this.getCache();
            data.put("cache", cacheClass);
            data.put("cacheClassName", cacheClass.getName());
        }
        data.put("superMapperClass", ClassUtils.getSimpleName(this.superClass));
        // 排序字段sql
        List<TableField> sortFields = tableInfo.getFields();
        List<String> existColumnNames = sortFields.stream().map(TableField::getColumnName).collect(Collectors.toList());
        if (sortColumnMap != null && !sortColumnMap.isEmpty()) {
            sortColumnMap.entrySet().stream().filter(e -> existColumnNames.contains(e.getKey())).map(e -> String.format("a.%s%s", e.getKey(), e.getValue() ? " DESC" : "")).reduce((e1, e2) -> e1 + ", " + e2).ifPresent(e -> data.put("orderBySql", e));
        }

        Set<String> mapperImportPackages = this.mapperImportPackages(tableInfo);
        Collection<String> javaPackages = mapperImportPackages.stream().filter(pkg -> pkg.startsWith("java")).collect(Collectors.toList());
        Collection<String> frameworkPackages = mapperImportPackages.stream().filter(pkg -> !pkg.startsWith("java")).collect(Collectors.toList());
        data.put("mapperImportPackages4Java", javaPackages);
        data.put("mapperImportPackages4Framework", frameworkPackages);

        return data;
    }
    
    public Set<String> mapperImportPackages(TableInfo tableInfo){
        Set<String> importPackages = new TreeSet<>();
        if (StringUtils.isNotBlank(superClass)) {
            importPackages.add(superClass);
        }
        if (mapperAnnotationClass != null) {
            importPackages.add(mapperAnnotationClass.getName());
        }
        Map<String, String> classCanonicalNameMap = tableInfo.getConfigurer().getOutputConfig().getOutputClassCanonicalNameMap(tableInfo);
        importPackages.add(classCanonicalNameMap.get(OutputFile.entity.name()));
        GlobalConfig globalConfig = tableInfo.getConfigurer().getGlobalConfig();
        if (globalConfig.isEnhancer()){
            importPackages.add("io.github.luxmixus.mybatisplus.enhancer.EnhancedMapper");
            importPackages.add(classCanonicalNameMap.get(OutputFile.queryVO.name()));
        }
        if (globalConfig.isGenerateQuery()){
            importPackages.add(List.class.getCanonicalName());
            importPackages.add(classCanonicalNameMap.get(OutputFile.queryVO.name()));
            importPackages.add("com.baomidou.mybatisplus.core.metadata.IPage");
        }
        return importPackages;
    }

    public Adapter adapter() {
        return new Adapter(this);
    }

    public static class Adapter {
        private final MapperConfig config;

        public Adapter(MapperConfig config) {
            this.config = config;
        }

        /**
         * 父类Mapper
         *
         * @param superClass 类名
         * @return this
         */
        public Adapter superClass(String superClass) {
            this.config.superClass = superClass;
            return this;
        }

        /**
         * 父类Mapper
         *
         * @param superClass 类
         * @return this
         * @since 3.5.0
         */
        public Adapter superClass(Class<?> superClass) {
            return superClass(superClass.getName());
        }

        /**
         * 标记 MapperConfig 注解
         *
         * @param annotationClass 注解Class
         * @return this
         * @since 3.5.3
         */
        public Adapter mapperAnnotation(Class<? extends Annotation> annotationClass) {
            this.config.mapperAnnotationClass = annotationClass;
            return this;
        }

        /**
         * 开启baseResultMap
         *
         * @return this
         * @since 3.5.0
         */
        public Adapter enableBaseResultMap() {
            this.config.baseResultMap = true;
            return this;
        }

        /**
         * 开启baseColumnList
         *
         * @return this
         * @since 3.5.0
         */
        public Adapter enableBaseColumnList() {
            this.config.baseColumnList = true;
            return this;
        }

        /**
         * 设置缓存实现类
         *
         * @param cache 缓存实现
         * @return this
         * @since 3.5.0
         */
        public Adapter cache(Class<? extends Cache> cache) {
            this.config.cache = cache;
            return this;
        }

        /**
         * 清空排序字段
         *
         * @return this
         */
        public Adapter clearSortColumnMap() {
            this.config.sortColumnMap.clear();
            return this;
        }

        /**
         * 添加排序字段
         *
         * @param columnName 字段名
         * @param isDesc     是否倒排
         * @return this
         */
        public Adapter sortColumn(String columnName, boolean isDesc) {
            this.config.sortColumnMap.put(columnName, isDesc);
            return this;
        }
    }

}