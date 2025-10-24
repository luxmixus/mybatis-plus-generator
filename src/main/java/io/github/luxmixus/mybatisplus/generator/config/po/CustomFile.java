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

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.Function;

/**
 * 自定义模板文件配置
 *
 * @author xusimin
 * @since 3.5.3
 */
@Getter
@Setter
@Accessors(chain = true)
public class CustomFile {
    /**
     * 模板路径
     */
    private String templatePath;
    /**
     * 输出文件所在文件夹
     */
    private String outputDir;
    /**
     * 文件名称格式化函数
     */
    private Function<TableInfo, String> formatNameFunction;
    /**
     * 输出文件后缀
     */
    private String outputFileSuffix;
    /**
     * 是否覆盖已有文件（默认 false）
     */
    private boolean fileOverride;
    
    public CustomFile validate() {
        if (templatePath == null) {
            throw new IllegalArgumentException("模板路径不能为空");
        }
        if (outputDir == null) {
            throw new IllegalArgumentException("文件输出文件夹不能为空");
        }
        if (formatNameFunction == null) {
            throw new IllegalArgumentException("文件名称格式化函数不能为空");
        }
        if (outputFileSuffix == null) {
            throw new IllegalArgumentException("文件名称后缀不能为空");
        }

        return this;
    }
    
}
