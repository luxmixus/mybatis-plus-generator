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
package io.github.luxmixus.mybatisplus.generator.engine;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.github.luxmixus.mybatisplus.generator.config.Configurer;
import io.github.luxmixus.mybatisplus.generator.config.po.CustomFile;
import io.github.luxmixus.mybatisplus.generator.config.po.TableInfo;
import io.github.luxmixus.mybatisplus.generator.config.support.StrategyConfig;
import io.github.luxmixus.mybatisplus.generator.util.FileUtils;
import io.github.luxmixus.mybatisplus.generator.util.RuntimeUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


/**
 * 模板引擎抽象类
 *
 * @author hubin
 * @since 2018-01-10
 */
public abstract class AbstractTemplateEngine {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * 配置信息
     */
    @Getter
    protected final Configurer configurer;

    public AbstractTemplateEngine(Configurer configurer) {
        this.configurer = configurer;
    }

    /**
     * 输出自定义模板文件
     *
     * @param customFiles 自定义模板文件列表
     * @param tableInfo   表信息
     * @param objectMap   渲染数据
     * @since 3.5.3
     */
    protected void outputCustomFile(List<CustomFile> customFiles, TableInfo tableInfo, Map<String, Object> objectMap) {
        customFiles.forEach(file -> {
            file.validate();
            String outputDir = file.getOutputDir();
            Function<TableInfo, String> formatNameFunction = file.getFormatNameFunction();
            String fileName = outputDir + File.separator + formatNameFunction.apply(tableInfo) + file.getOutputFileSuffix();
            outputFile(new File(fileName), objectMap, file.getTemplatePath(), file.isFileOverride());
        });
    }


    /**
     * 输出文件
     *
     * @param file         文件
     * @param objectMap    渲染信息
     * @param templatePath 模板路径
     * @param fileOverride 是否覆盖已有文件
     * @since 3.5.2
     */
    protected void outputFile(File file, Map<String, Object> objectMap, String templatePath, boolean fileOverride) {
        if (isCreate(file, fileOverride)) {
            try {
                // 全局判断【默认】
                boolean exist = file.exists();
                if (!exist) {
                    File parentFile = file.getParentFile();
                    FileUtils.forceMkdir(parentFile);
                }
                writer(objectMap, templatePath, file);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    /**
     * 批量输出 java xml 文件
     */
    public AbstractTemplateEngine batchOutput() {
        try {
            Configurer config = this.getConfigurer();
            List<TableInfo> tableInfoList = config.getTableInfo();
            tableInfoList.forEach(tableInfo -> {
                Map<String, Object> objectMap = this.getObjectMap(config, tableInfo);
                Optional.ofNullable(config.getInjectionConfig()).ifPresent(injectionConfig -> {
                    // 添加自定义属性
                    injectionConfig.beforeOutputFile(tableInfo, objectMap);
                    
                    // 输出预定义文件
                    outputCustomFile(config.getOutputConfig().getCustomFiles(), tableInfo, objectMap);
                    
                    // 输出自定义文件
                    outputCustomFile(injectionConfig.getCustomFiles(), tableInfo, objectMap);
                });
            });
        } catch (Exception e) {
            throw new RuntimeException("无法创建文件，请检查配置信息！", e);
        }
        return this;
    }


    /**
     * 将模板转化成为字符串
     *
     * @param objectMap      渲染对象 MAP 信息
     * @param templateName   模板名称
     * @param templateString 模板字符串
     * @since 3.5.0
     */
    public abstract String writer(Map<String, Object> objectMap, String templateName, String templateString) throws Exception;

    /**
     * 将模板转化成为文件
     *
     * @param objectMap    渲染对象 MAP 信息
     * @param templatePath 模板文件
     * @param outputFile   文件生成的目录
     * @throws Exception 异常
     * @since 3.5.0
     */
    public abstract void writer(Map<String, Object> objectMap, String templatePath, File outputFile) throws Exception;

    /**
     * 打开输出目录
     */
    public void open() {
        String outDir = getConfigurer().getOutputConfig().getOutputDir();
        if (StringUtils.isBlank(outDir) || !new File(outDir).exists()) {
            System.err.println("未找到输出目录：" + outDir);
        } else if (getConfigurer().getOutputConfig().isOpen()) {
            try {
                RuntimeUtils.openDir(outDir);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 渲染对象 MAP 信息
     *
     * @param config    配置信息
     * @param tableInfo 表信息对象
     * @return ignore
     */
    public Map<String, Object> getObjectMap(Configurer config, TableInfo tableInfo) {
        Map<String, Object> objectMap = new HashMap<>();
        Map<String, Object> globalMap = config.getGlobalConfig().renderData(tableInfo);
        objectMap.putAll(globalMap);
        
        StrategyConfig strategyConfig = config.getStrategyConfig();
        // 启用 schema 处理逻辑
        String schemaName = "";
        if (strategyConfig.isEnableSchema()) {
            // 存在 schemaName 设置拼接 . 组合表名
            schemaName = config.getDataSourceConfig().getSchemaName();
            if (StringUtils.isNotBlank(schemaName)) {
                tableInfo.setSchemaName(schemaName);
                schemaName += ".";
                tableInfo.setConvert(true);
            }
        }
        objectMap.put("schemaName", schemaName);
        objectMap.put("config", config);
        objectMap.put("table", tableInfo);

        // 策略配置
        Map<String, Object> strategyData = strategyConfig.renderData(tableInfo);
        objectMap.putAll(strategyData);

        // 包及输出文件
        Map<String, Object> outputData = config.getOutputConfig().renderData(tableInfo);
        objectMap.putAll(outputData);
        
        // 实体配置
        Map<String, Object> entityData = config.getEntityConfig().renderData(tableInfo);
        objectMap.putAll(entityData);
        
        // 模型配置
        Map<String, Object> modelData = config.getModelConfig().renderData(tableInfo);
        objectMap.putAll(modelData);
        
        // mapper配置
        Map<String, Object> mapperData = config.getMapperConfig().renderData(tableInfo);
        objectMap.putAll(mapperData);
        
        // service配置
        Map<String, Object> serviceData = config.getServiceConfig().renderData(tableInfo);
        objectMap.putAll(serviceData);
        
        // controller配置
        Map<String, Object> controllerData = config.getControllerConfig().renderData(tableInfo);
        objectMap.putAll(controllerData);
        
        return objectMap;
    }

    /**
     * 模板真实文件路径
     *
     * @param filePath 文件路径
     * @return ignore
     */
    public abstract String templateFilePath(String filePath);

    /**
     * 检查文件是否创建文件
     *
     * @param file         文件
     * @param fileOverride 是否覆盖已有文件
     * @return 是否创建文件
     * @since 3.5.2
     */
    protected boolean isCreate(File file, boolean fileOverride) {
        if (file.exists() && !fileOverride) {
            LOGGER.warn("文件[{}]已存在，且未开启文件覆盖配置，需要开启配置可到策略配置中设置！！！", file.getName());
        }
        return !file.exists() || fileOverride;
    }
}
