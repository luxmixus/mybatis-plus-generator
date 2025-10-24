package io.github.luxmixus.mybatisplus.generator.config.po;

import lombok.Getter;

/**
 * @author luxmixus
 */
@Getter
public class TemplateFile {
    /**
     * 用于标识文件的key,重复时覆盖
     */
    protected String key;
    /**
     * 格式化文件名称
     */
    protected String formatPattern;
    /**
     * 子包名(可空)
     */
    protected String subPackage;
    /**
     * 模板路径
     */
    protected String templatePath;
    /**
     * 输出文件路径
     */
    protected String outputDir;
    /**
     * 输出文件后缀
     */
    protected String outputFileSuffix;
    /**
     * 文件覆盖
     */
    protected boolean fileOverride;
    /**
     * 是否生成
     */
    protected boolean generate = true;

    public TemplateFile(String key, String formatPattern,String subPackage, String templatePath, String outputFileSuffix) {
        this.key = key;
        this.formatPattern = formatPattern;
        this.subPackage = subPackage;
        this.templatePath = templatePath;
        this.outputFileSuffix = outputFileSuffix;
    }

    /**
     * 根据表信息转化输出文件名称
     *
     * @param tableInfo 表信息
     */
    public String convertFormatName(TableInfo tableInfo) {
        return String.format(formatPattern, tableInfo.getEntityName());
    }

    /**
     * 获取适配器
     */
    public Adapter adapter() {
        return new Adapter(this);
    }
    public static class Adapter {
        protected final TemplateFile templateFile;
        public Adapter(TemplateFile templateFile) {
            this.templateFile = templateFile;
        }

        public Adapter formatPattern(String formatPattern) {
            this.templateFile.formatPattern = formatPattern;
            return this;
        }

        /**
         * 子包名
         *
         * @param subPackage 子包
         */
        public Adapter subPackage(String subPackage) {
            this.templateFile.subPackage = subPackage;
            return this;
        }

        /**
         * 模板路径
         *
         * @param templatePath 模板路径
         */
        public Adapter templatePath(String templatePath) {
            this.templateFile.templatePath = templatePath;
            return this;
        }

        /**
         * 输出方向
         *
         * @param outputDir 输出方向
         */
        public Adapter outputDir(String outputDir) {
            this.templateFile.outputDir = outputDir;
            return this;
        }

        /**
         * 禁用
         */
        public Adapter disable() {
            this.templateFile.generate = false;
            return this;
        }
    }
}
