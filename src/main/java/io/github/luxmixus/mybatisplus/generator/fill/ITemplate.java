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
package io.github.luxmixus.mybatisplus.generator.fill;

import io.github.luxmixus.mybatisplus.generator.config.po.TableInfo;
import lombok.SneakyThrows;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 渲染模板接口
 *
 * @author nieqiurong 2020/11/9.
 * @since 3.5.0
 */

public interface ITemplate extends Serializable {

    @SneakyThrows
    default Map<String, Object> renderData(TableInfo tableInfo) {
        // 添加自定义配置字段信息
//        HashMap<String, Object> data = new HashMap<>();
//        Collection<Field> fields = ReflectUtil.fieldMap(getClass()).values();
//        for (Field field : fields) {
//            data.put(field.getName(), field.get(this));
//        }
//        return data;
        return new HashMap<>();
    }

}
