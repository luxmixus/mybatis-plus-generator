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

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.github.bootystar.mybatisplus.generator.config.enums.OutputFile;
import io.github.bootystar.mybatisplus.generator.config.po.ClassPayload;
import io.github.bootystar.mybatisplus.generator.config.po.MethodPayload;
import io.github.bootystar.mybatisplus.generator.config.po.TableField;
import io.github.bootystar.mybatisplus.generator.config.po.TableInfo;
import io.github.bootystar.mybatisplus.generator.fill.ITemplate;
import io.github.bootystar.mybatisplus.generator.util.ClassUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 控制器属性配置
 *
 * @author nieqiurong 2020/10/11.
 * @since 3.5.0
 */
@Slf4j
public class ControllerConfig implements ITemplate {

    /**
     * 自定义继承的Controller类全称，带包名
     */
    protected String superClass;

    /**
     * 生成 <code>@RestController</code> 控制器
     * <pre>
     *      <code>@ControllerConfig</code>
     *      ->
     *      <code>@RestController</code>
     * </pre>
     */
    protected boolean restController = true;

    /**
     * 驼峰转连字符
     * <pre>
     *      <code>@RequestMapping("/managerUserActionHistory")</code> -> <code>@RequestMapping("/manager-user-action-history")</code>
     * </pre>
     */
    protected boolean hyphenStyle = true;

    /**
     * 请求基础url
     */
    protected String baseUrl;

    /**
     * 跨域注解
     */
    protected boolean crossOrigin;

    /**
     * restful样式
     */
    protected boolean restful;

    /**
     * 请求路径参数
     */
    protected boolean pathVariable = true;

    /**
     * controller是否使用@RequestBody注解
     */
    protected boolean requestBody = true;

    /**
     * 批量查询使用post请求
     */
    protected boolean batchQueryPost = true;

    /**
     * 批量查询body参数是否必填
     */
    protected boolean batchQueryRequiredBody = true;

    /**
     * 返回结果方法
     */
    protected MethodPayload returnMethod = new MethodPayload();

    /**
     * 分页结果方法
     */
    protected MethodPayload pageMethod = new MethodPayload();

    /**
     * 指定查询的参数
     */
    @Deprecated
    protected ClassPayload queryParam = new ClassPayload();

    @Override
    public Map<String, Object> renderData(TableInfo tableInfo) {
        Map<String, Object> data = ITemplate.super.renderData(tableInfo);
        OutputConfig outputConfig = tableInfo.getConfigurer().getOutputConfig();
        Map<String, Boolean> outputClassGenerateMap = outputConfig.getOutputClassGenerateMap();
        Map<String, String> outputClassSimpleNameMap = outputConfig.getOutputClassSimpleNameMap(tableInfo);
        Map<String, String> outputClassClassCanonicalNameMap = outputConfig.getOutputClassCanonicalNameMap(tableInfo);
        data.put("controllerMappingHyphen", StringUtils.camelToHyphen(tableInfo.getEntityPath()));
        data.put("controllerMappingHyphenStyle", this.hyphenStyle);
        data.put("restControllerStyle", this.restController);
        data.put("superControllerClassPackage", StringUtils.isBlank(superClass) ? null : superClass);
        data.put("superControllerClass", ClassUtils.getSimpleName(this.superClass));
        data.put("primaryKeyPropertyType", "Object");
        String url = this.hyphenStyle ? StringUtils.camelToHyphen(tableInfo.getEntityPath()) : tableInfo.getEntityPath();
        String requestBaseUrl = Stream.of(this.baseUrl, outputConfig.getModuleName(), url)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("/"));
        if(!requestBaseUrl.startsWith("/")){
            requestBaseUrl = "/" + requestBaseUrl;
        }
        data.put("requestBaseUrl", requestBaseUrl);
        data.put("crossOrigin", this.crossOrigin);
        data.put("restful", this.restful);
        data.put("returnMethod", this.returnMethod);
        data.put("pageMethod", this.pageMethod);
        data.put("batchQueryMethod", batchQueryPost ? "@PostMapping" : "@GetMapping");
        if (pathVariable) {
            data.put("idPathParams", "/{id}");
            data.put("idMethodParams", "@PathVariable(\"id\") ");
            data.put("pagePathParams", "/{current}/{size}");
            data.put("pageMethodParams", "@PathVariable(\"current\") Long current, @PathVariable(\"size\") Long size");
        } else {
            data.put("pageMethodParams", "Long current, Long size");
        }
        String requiredBodyStr = "@RequestBody ";
        String optionalBodyStr = batchQueryRequiredBody ? "@RequestBody " : "@RequestBody(required = false) ";
        String validatedStr = "@Validated ";
        if (requestBody) {
            data.put("requiredBodyStr", requiredBodyStr);
            if (batchQueryPost){
                data.put("optionalBodyStr", optionalBodyStr);
            }
        }
        TreeSet<String> importPackages = new TreeSet<>();
        GlobalConfig globalConfig = tableInfo.getConfigurer().getGlobalConfig();
        if (superClass != null) {
            importPackages.add(superClass);
        }
        importPackages.add("org.springframework.web.bind.annotation.*");
        boolean generateAny = globalConfig.isGenerateQuery()
                || globalConfig.isGenerateExport()
                || globalConfig.isGenerateImport()
                || globalConfig.isGenerateInsert()
                || globalConfig.isGenerateUpdate()
                || globalConfig.isGenerateDelete();
        if (!restController) {
            importPackages.add("org.springframework.stereotype.Controller");
        }
        if (globalConfig.isLombok()) {
            importPackages.add("lombok.RequiredArgsConstructor");
        }
        if (globalConfig.isSpringdoc()) {
            importPackages.add("io.swagger.v3.oas.annotations.tags.Tag");
            if (generateAny) {
                importPackages.add("io.swagger.v3.oas.annotations.Operation");
//                importPackages.add("io.swagger.v3.oas.annotations.Parameter");
//                importPackages.add("io.swagger.v3.oas.annotations.Parameters");
            }
        }
        if (globalConfig.isSwagger()) {
            importPackages.add("io.swagger.annotations.Api");
            if (generateAny) {
                importPackages.add("io.swagger.annotations.ApiOperation");
//                importPackages.add("io.swagger.annotations.ApiParam");
            }
        }
        if (generateAny && returnMethod.isClassReady()) {
            importPackages.add(returnMethod.getClassCanonicalName());
        }
        if (outputClassGenerateMap.get(OutputFile.service.name())) {
            data.put("baseService", outputClassSimpleNameMap.get(OutputFile.service.name()));
            importPackages.add(outputConfig.getOutputClassCanonicalNameMap(tableInfo).get(OutputFile.service.name()));
        } else {
            data.put("baseService", outputClassSimpleNameMap.get(OutputFile.serviceImpl.name()));
            importPackages.add(outputConfig.getOutputClassCanonicalNameMap(tableInfo).get(OutputFile.serviceImpl.name()));
        }
        String primaryKeyPropertyType = "Object";
        String primaryKeyPropertyClass = null;
        for (TableField field : tableInfo.getFields()) {
            if (field.isKeyFlag()) {
                primaryKeyPropertyType = field.getPropertyType();
                primaryKeyPropertyClass = field.getColumnType().getPkg();
                break;
            }
        }
        data.put("primaryKeyPropertyType", primaryKeyPropertyType);
        if (globalConfig.isValidated()) {
            data.put("validatedStr", validatedStr);
        }
        // 生成方法
        if (globalConfig.isGenerateInsert()) {
            importPackages.add(outputClassClassCanonicalNameMap.get(OutputFile.insertDTO.name()));
            if (globalConfig.isValidated()) {
                importPackages.add("org.springframework.validation.annotation.Validated");
            }
        }
        if (globalConfig.isGenerateUpdate()) {
            importPackages.add(outputClassClassCanonicalNameMap.get(OutputFile.updateDTO.name()));
            if (globalConfig.isValidated()) {
                importPackages.add("org.springframework.validation.annotation.Validated");
            }
        }
        if (globalConfig.isGenerateDelete()) {
            if (primaryKeyPropertyClass != null) {
                importPackages.add(primaryKeyPropertyClass);
            }
        }

        if (globalConfig.isGenerateQuery()) {
            if (primaryKeyPropertyClass != null) {
                importPackages.add(primaryKeyPropertyClass);
            }
            importPackages.add("java.util.List");
            importPackages.add(outputClassClassCanonicalNameMap.get(OutputFile.queryDTO.name()));
            importPackages.add(outputClassClassCanonicalNameMap.get(OutputFile.queryVO.name()));
            if (globalConfig.isEnhancer()){
                importPackages.add(outputClassClassCanonicalNameMap.get(OutputFile.entity.name()));
                importPackages.add("io.github.bootystar.mybatisplus.enhancer.query.helper.SqlHelper");
            }
            if (pageMethod.isMethodReady()) {
                importPackages.add(pageMethod.getClassCanonicalName());
                data.put("pageReturnType", pageMethod.returnGenericTypeStr(outputClassSimpleNameMap.get(OutputFile.queryVO.name())));
            } else {
                importPackages.add("com.baomidou.mybatisplus.core.metadata.IPage");
                data.put("pageReturnType", String.format("IPage<%s>", outputClassSimpleNameMap.get(OutputFile.queryVO.name())));
            }
        }
        String responseClass = globalConfig.resolveJakartaClassCanonicalName("servlet.http.HttpServletResponse");
        if (globalConfig.isGenerateImport()) {
            importPackages.add("org.springframework.web.multipart.MultipartFile");
            importPackages.add("java.io.IOException");
            importPackages.add(outputClassClassCanonicalNameMap.get(OutputFile.insertDTO.name()));
            importPackages.add(responseClass);
        }
        if (globalConfig.isGenerateExport()) {
            importPackages.add("java.io.IOException");
            importPackages.add(responseClass);
        }

        Collection<String> javaPackages = importPackages.stream().filter(pkg -> pkg.startsWith("java")).collect(Collectors.toList());
        Collection<String> frameworkPackages = importPackages.stream().filter(pkg -> !pkg.startsWith("java")).collect(Collectors.toList());
        data.put("controllerImportPackages4Java", javaPackages);
        data.put("controllerImportPackages4Framework", frameworkPackages);

        return data;
    }

    public Adapter adapter() {
        return new Adapter(this);
    }

    public static class Adapter {
        private final ControllerConfig config;

        public Adapter(ControllerConfig config) {
            this.config = config;
        }

        /**
         * 父类控制器
         *
         * @param clazz 父类控制器
         * @return this
         */
        public Adapter superClass(Class<?> clazz) {
            return superClass(clazz.getName());
        }

        /**
         * 父类控制器
         *
         * @param superClass 父类控制器类名
         * @return this
         */
        public Adapter superClass(String superClass) {
            this.config.superClass = superClass;
            return this;
        }

        /**
         * 关闭@RestController控制器
         *
         * @return this
         * @since 3.5.0
         */
        public Adapter disableRestController() {
            this.config.restController = false;
            return this;
        }

        /**
         * 关闭驼峰转连字符
         *
         * @return this
         * @since 3.5.0
         */
        public Adapter disableHyphenStyle() {
            this.config.hyphenStyle = false;
            return this;
        }

        /**
         * controller请求前缀
         *
         * @param url url
         * @return this
         */
        public Adapter baseUrl(String url) {
            if (url == null || url.isEmpty()) {
                this.config.baseUrl = null;
                return this;
            }
            if (!url.startsWith("/")) {
                url = "/" + url;
            }
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            this.config.baseUrl = url;
            return this;
        }

        /**
         * 跨域注解
         *
         * @return this
         */
        public Adapter enableCrossOrigin() {
            this.config.crossOrigin = true;
            return this;
        }

        /**
         * 禁止批量数据查询使用post请求
         *
         * @return this
         */
        public Adapter disableBatchQueryPost() {
            this.config.batchQueryPost = false;
            return this;
        }

        /**
         * 批量查询body参数是否必填
         *
         * @return this
         */
        public Adapter disableBatchQueryRequiredBody() {
            this.config.batchQueryRequiredBody = false;
            return this;
        }

        /**
         * 增删查改使用restful风格
         *
         * @return this
         */
        public Adapter enableRestful() {
            this.config.restful = true;
            return this;
        }

        /**
         * 禁用路径变量
         *
         * @return this
         */
        public Adapter disablePathVariable() {
            this.config.pathVariable = false;
            return this;
        }

        /**
         * 禁用消息体接收数据
         *
         * @return this
         */
        public Adapter disableRequestBody() {
            this.config.requestBody = false;
            return this;
        }

        /**
         * 指定controller的返回结果包装类及方法
         *
         * @param methodReference 方法引用
         * @return this
         */
        public <R> Adapter returnMethod(com.baomidou.mybatisplus.core.toolkit.support.SFunction<Object, R> methodReference) {
            this.config.returnMethod = io.github.bootystar.mybatisplus.generator.util.ReflectUtil.lambdaMethodInfo(methodReference, Object.class);
            return this;
        }

        /**
         * 指定controller返回的分页包装类及方法
         *
         * @param methodReference 方法参考
         * @return this
         */
        public <O, R> Adapter pageMethod(com.baomidou.mybatisplus.core.toolkit.support.SFunction<com.baomidou.mybatisplus.core.metadata.IPage<O>, R> methodReference) {
            this.config.pageMethod = io.github.bootystar.mybatisplus.generator.util.ReflectUtil.lambdaMethodInfo(methodReference, com.baomidou.mybatisplus.core.metadata.IPage.class);
            return this;
        }

        /**
         * 指定用于查询的类
         *
         * @return this
         * @deprecated 已弃用的方法
         */
        @Deprecated
        public Adapter queryParam(Class<?> queryDTO) {
//            this.config.queryParam = new io.github.bootystar.mybatisplus.generator.config.po.ClassPayload(queryDTO);
            return this;
        }
    }
}