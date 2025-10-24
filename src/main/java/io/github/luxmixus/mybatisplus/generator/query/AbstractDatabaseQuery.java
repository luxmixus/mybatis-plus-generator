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
package io.github.luxmixus.mybatisplus.generator.query;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import io.github.luxmixus.mybatisplus.generator.config.Configurer;
import io.github.luxmixus.mybatisplus.generator.config.support.DataSourceConfig;
import io.github.luxmixus.mybatisplus.generator.config.support.GlobalConfig;
import io.github.luxmixus.mybatisplus.generator.config.support.StrategyConfig;
import io.github.luxmixus.mybatisplus.generator.config.po.TableInfo;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据库查询抽象类
 *
 * @author nieqiurong
 * @since 3.5.3
 */
public abstract class AbstractDatabaseQuery implements IDatabaseQuery {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Getter
    protected final Configurer configAdapter;

    @Getter
    protected final DataSourceConfig dataSourceConfig;

    protected final StrategyConfig strategyConfig;

    protected final GlobalConfig globalConfig;


    public AbstractDatabaseQuery(Configurer configAdapter) {
        this.configAdapter = configAdapter;
        this.dataSourceConfig = configAdapter.getDataSourceConfig();
        this.strategyConfig = configAdapter.getStrategyConfig();
        this.globalConfig = configAdapter.getGlobalConfig();
    }

    protected void filter(List<TableInfo> tableList, List<TableInfo> includeTableList, List<TableInfo> excludeTableList) {
        boolean isInclude = !strategyConfig.getInclude().isEmpty();
        boolean isExclude = !strategyConfig.getExclude().isEmpty();
        if (isExclude || isInclude) {
            Map<String, String> notExistTables = new HashSet<>(isExclude ? strategyConfig.getExclude() : strategyConfig.getInclude())
                .stream()
                .filter(s -> !Configurer.matcherRegTable(s))
                .collect(Collectors.toMap(String::toLowerCase, s -> s, (o, n) -> n));
            // 将已经存在的表移除，获取配置中数据库不存在的表
            for (TableInfo tabInfo : tableList) {
                if (notExistTables.isEmpty()) {
                    break;
                }
                //解决可能大小写不敏感的情况导致无法移除掉
                notExistTables.remove(tabInfo.getName().toLowerCase());
            }
            if (!notExistTables.isEmpty()) {
                LOGGER.warn("表[{}]在数据库中不存在！！！", String.join(StringPool.COMMA, notExistTables.values()));
            }
            // 需要反向生成的表信息
            if (isExclude) {
                tableList.removeAll(excludeTableList);
            } else {
                tableList.clear();
                tableList.addAll(includeTableList);
            }
        }
    }
}
