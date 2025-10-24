package io.github.luxmixus.mybatisplus.generator.config.support;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.github.luxmixus.mybatisplus.generator.config.Configurer;
import io.github.luxmixus.mybatisplus.generator.config.enums.OutputFile;
import io.github.luxmixus.mybatisplus.generator.config.po.CustomFile;
import io.github.luxmixus.mybatisplus.generator.config.po.TableInfo;
import io.github.luxmixus.mybatisplus.generator.config.po.TemplateFile;
import io.github.luxmixus.mybatisplus.generator.fill.ITemplate;
import lombok.Getter;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 输出文件配置
 *
 * @author luxmixus
 */
public class OutputConfig implements ITemplate {

    /**
     * 生成文件的输出目录
     */
    @Getter
    protected String outputDir = System.getProperty("user.dir") + "/src/main/java";

    /**
     * 父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
     */
    protected String parentPackage = "io.github.luxmixus";

    /**
     * 父包模块名
     */
    @Getter
    protected String moduleName = "";

    /**
     * 全局文件覆盖
     */
    @Getter
    protected boolean globalFileOverride;

    /**
     * 是否打开输出目录
     */
    @Getter
    protected boolean open = true;

    @Getter
    protected TemplateFile entity = new TemplateFile(
            OutputFile.entity.name(),
            "%s",
            "entity",
            "/templates/entity.java",
            ".java"
    );
    @Getter
    protected TemplateFile mapper = new TemplateFile(
            OutputFile.mapper.name(),
            "%sMapper",
            "mapper",
            "/templates/mapper.java",
            ".java"
    );

    @Getter
    protected TemplateFile mapperXml = new TemplateFile(
            OutputFile.mapperXml.name(),
            "%sMapper",
            "mapper.xml",
            "/templates/mapper.xml",
            ".xml"
    );
    @Getter
    protected TemplateFile service = new TemplateFile(
            OutputFile.service.name(),
            "I%sService",
            "service",
            "/templates/service.java",
            ".java"
    );
    @Getter
    protected TemplateFile serviceImpl = new TemplateFile(
            OutputFile.serviceImpl.name(),
            "%sServiceImpl",
            "service.impl",
            "/templates/serviceImpl.java",
            ".java"
    );
    @Getter
    protected TemplateFile controller = new TemplateFile(
            OutputFile.controller.name(),
            "%sController",
            "controller",
            "/templates/controller.java",
            ".java"
    );
    @Getter
    protected TemplateFile insertDTO = new TemplateFile(
            OutputFile.insertDTO.name(),
            "%sInsertDTO",
            "dto",
            "/templates/insertDTO.java",
            ".java"
    );
    @Getter
    protected TemplateFile updateDTO = new TemplateFile(
            OutputFile.updateDTO.name(),
            "%sUpdateDTO",
            "dto",
            "/templates/updateDTO.java",
            ".java"
    );
    @Getter
    protected TemplateFile queryDTO = new TemplateFile(
            OutputFile.queryDTO.name(),
            "%sQueryDTO",
            "dto",
            "/templates/queryDTO.java",
            ".java"
    );
    @Getter
    protected TemplateFile queryVO = new TemplateFile(
            OutputFile.queryVO.name(),
            "%sQueryVO",
            "vo",
            "/templates/queryVO.java",
            ".java"
    );

    protected Stream<TemplateFile> templateFileStream() {
        return Stream.of(entity, mapper, mapperXml, service, serviceImpl, controller, insertDTO, updateDTO, queryDTO, queryVO);
    }

    /**
     * 父包名
     */
    public String getParentPackage() {
        if (StringUtils.isNotBlank(moduleName)) {
            return parentPackage + StringPool.DOT + moduleName;
        }
        return parentPackage;
    }

    /**
     * 连接父子包名
     *
     * @param subPackage 子包名
     * @return 连接后的包名
     */
    public String joinPackage(String subPackage) {
        String parent = getParentPackage();
        return StringUtils.isBlank(parent) ? subPackage : (parent + StringPool.DOT + subPackage);
    }

    /**
     * 连接路径字符串
     *
     * @param parentDir   路径常量字符串
     * @param packageName 包名
     * @return 连接后的路径
     */
    public String joinPath(String parentDir, String packageName) {
        if (StringUtils.isBlank(parentDir)) {
            parentDir = System.getProperty("java.io.tmpdir");
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        packageName = packageName.replaceAll("\\.", StringPool.BACK_SLASH + File.separator);
        return parentDir + packageName;
    }

    /**
     * 获取输出文件类规范名称map
     *
     * @param tableInfo 表信息
     * @see OutputFile#name()
     */
    public Map<String, String> getOutputClassCanonicalNameMap(TableInfo tableInfo) {
        return templateFileStream().collect(Collectors.toMap(
                TemplateFile::getKey,
                e -> joinPackage(e.getSubPackage()) + "." + e.convertFormatName(tableInfo)
        ));
    }

    /**
     * 获取类简单名称map
     *
     * @param tableInfo 表信息
     * @see OutputFile#name() 
     */
    public Map<String, String> getOutputClassSimpleNameMap(TableInfo tableInfo) {
        return templateFileStream().collect(Collectors.toMap(
                TemplateFile::getKey,
                e -> e.convertFormatName(tableInfo)
        ));
    }

    /**
     * 获取类生成信息
     *
     */
    public Map<String, Boolean> getOutputClassGenerateMap() {
        return templateFileStream().collect(Collectors.toMap(TemplateFile::getKey, TemplateFile::isGenerate));
    }

    /**
     * 获取包信息
     */
    public Map<String, String> getOutputClassPackageInfoMap() {
        return templateFileStream().collect(Collectors.toMap(TemplateFile::getKey, e -> joinPackage(e.getSubPackage())));
    }

    /**
     * 获取输出文件
     */
    public List<CustomFile> getCustomFiles() {
        return templateFileStream().filter(TemplateFile::isGenerate).map(e -> {
            CustomFile customFile = new CustomFile();
            String fileOutputDir = e.getOutputDir();
            if (fileOutputDir == null) {
                String joinPackage = joinPackage(e.getSubPackage());
                fileOutputDir = joinPath(outputDir, joinPackage);
            }
            customFile.setFormatNameFunction(e::convertFormatName).setTemplatePath(e.getTemplatePath()).setOutputFileSuffix(e.getOutputFileSuffix()).setOutputDir(fileOutputDir).setFileOverride(e.isFileOverride() || this.globalFileOverride);
            return customFile;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> renderData(TableInfo tableInfo) {
        Map<String, Object> map = ITemplate.super.renderData(tableInfo);
        map.putAll(this.getOutputClassSimpleNameMap(tableInfo));
        map.put("package", this.getOutputClassPackageInfoMap());
        map.put("class", this.getOutputClassCanonicalNameMap(tableInfo));
        map.put("generate", this.getOutputClassGenerateMap());
        return map;
    }

    /**
     * 根据设置处理文件是否构建
     *
     * @param configurer 配置器
     */
    public void processOutput(Configurer configurer) {
        GlobalConfig globalConfig = configurer.getGlobalConfig();
        if (!globalConfig.isGenerateInsert()){
            this.insertDTO.adapter().disable();
        }
        if (!globalConfig.isGenerateUpdate()){
            this.updateDTO.adapter().disable();
        }
        if (!globalConfig.isGenerateQuery()){
            this.queryDTO.adapter().disable();
            if (!globalConfig.isEnhancer()){
                this.queryVO.adapter().disable();
            }
        }
    }
    
    public Adapter adapter() {
        return new Adapter(this);
    }

    public static class Adapter {
        private final OutputConfig config;

        public Adapter(OutputConfig config) {
            this.config = config;
        }

        /**
         * 文件输出目录
         *
         * @param outputDir 文件输出目录
         * @return this
         */
        public Adapter outputDir(String outputDir) {
            this.config.outputDir = outputDir;
            return this;
        }

        /**
         * 父包名
         *
         * @param parentPackage 父包名
         * @return this
         */
        public Adapter parentPackage(String parentPackage) {
            this.config.parentPackage = parentPackage;
            return this;
        }

        /**
         * 模块名
         *
         * @param moduleName 模块名
         * @return this
         */
        public Adapter moduleName(String moduleName) {
            this.config.moduleName = moduleName;
            return this;
        }

        /**
         * 启用全局文件覆盖(仅影响本配置提供的模板文件)
         *
         * @return this
         */
        public Adapter enableGlobalFileOverride() {
            this.config.globalFileOverride = true;
            return this;
        }

        /**
         * 禁用打开输出目录
         *
         * @return this
         */
        public Adapter disableOpenOutputDir() {
            this.config.open = false;
            return this;
        }

        /**
         * 启用打开输出目录
         *
         * @return this
         * @deprecated 默认值,无需设置
         */
        @Deprecated
        public Adapter enableOpenOutputDir() {
            this.config.open = false;
            return this;
        }
        

        /**
         * 实体类配置
         *
         * @param adapter 适配器
         */
        public Adapter entity(Function<TemplateFile.Adapter, TemplateFile.Adapter> adapter) {
            adapter.apply(this.config.entity.adapter());
            return this;
        }

        /**
         * mapper配置
         *
         * @param adapter 适配器
         */
        public Adapter mapper(Function<TemplateFile.Adapter, TemplateFile.Adapter> adapter) {
            adapter.apply(this.config.mapper.adapter());
            return this;
        }

        /**
         * mapperXml配置
         *
         * @param adapter 适配器
         */
        public Adapter mapperXml(Function<TemplateFile.Adapter, TemplateFile.Adapter> adapter) {
            adapter.apply(this.config.mapperXml.adapter());
            return this;
        }

        /**
         * service配置
         *
         * @param adapter 适配器
         */
        public Adapter service(Function<TemplateFile.Adapter, TemplateFile.Adapter> adapter) {
            adapter.apply(this.config.service.adapter());
            return this;
        }

        /**
         * serviceImpl配置
         *
         * @param adapter 适配器
         */
        public Adapter serviceImpl(Function<TemplateFile.Adapter, TemplateFile.Adapter> adapter) {
            adapter.apply(this.config.serviceImpl.adapter());
            return this;
        }

        /**
         * controller配置
         *
         * @param adapter 适配器
         */
        public Adapter controller(Function<TemplateFile.Adapter, TemplateFile.Adapter> adapter) {
            adapter.apply(this.config.controller.adapter());
            return this;
        }

        /**
         * insertDTO配置
         *
         * @param adapter 适配器
         */
        public Adapter insertDTO(Function<TemplateFile.Adapter, TemplateFile.Adapter> adapter) {
            adapter.apply(this.config.insertDTO.adapter());
            return this;
        }

        /**
         * updateDTO配置
         *
         * @param adapter 适配器
         */
        public Adapter updateDTO(Function<TemplateFile.Adapter, TemplateFile.Adapter> adapter) {
            adapter.apply(this.config.updateDTO.adapter());
            return this;
        }

        /**
         * queryDTO配置
         *
         * @param adapter 适配器
         */
        public Adapter queryDTO(Function<TemplateFile.Adapter, TemplateFile.Adapter> adapter) {
            adapter.apply(this.config.queryDTO.adapter());
            return this;
        }

        /**
         * vo配置
         *
         * @param adapter 适配器
         */
        public Adapter queryVO(Function<TemplateFile.Adapter, TemplateFile.Adapter> adapter) {
            adapter.apply(this.config.queryVO.adapter());
            return this;
        }
        
    }
}