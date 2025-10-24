# mybatis-plus-generator

[![Maven Central](https://img.shields.io/maven-central/v/io.github.luxmixus/mybatis-plus-generator)](https://mvnrepository.com/artifact/io.github.luxmixus/mybatis-plus-generator)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)


MyBatis-Plus 代码生成器，提供了比官方代码生成器更丰富的功能和可配置项，旨在提升开发效率，减少重复代码编写。

## 功能特性

- **基础代码生成**：生成实体类、Mapper、Service、Controller 等基础代码
- **领域模型生成**：支持生成新增 DTO、修改 DTO、查询 DTO、查询 VO 等领域模型
- **选择性方法生成**：支持选择性生成增删查改、导入导出等方法
- **动态 SQL 支持**：支持动态 SQL 入参映射查询
- **字段后缀映射**：支持字段自定义后缀映射查询
- **多种数据库支持**：支持 MySQL、PostgreSQL、Oracle 等主流数据库
- **模板引擎支持**：使用 Velocity 模板引擎，支持自定义模板
- **配置灵活**：提供链式调用的配置方式，配置简单易用

## 仓库地址

- GitHub: https://github.com/luxmixus/mybatis-plus-generator
- Maven Central: https://central.sonatype.com/artifact/io.github.luxmixus/mybatis-plus-generator

## Maven依赖

当前最新版本为:  
[![Maven Central](https://img.shields.io/maven-central/v/io.github.luxmixus/mybatis-plus-generator)](https://mvnrepository.com/artifact/io.github.luxmixus/mybatis-plus-generator)

```xml
<dependency>
    <groupId>io.github.luxmixus</groupId>
    <artifactId>mybatis-plus-generator</artifactId>
    <version>latest</version>
</dependency>
```

## 快速开始

使用 [FastGenerator](src/main/java/io/github/luxmixus/mybatisplus/generator/FastGenerator.java) 可以快速生成代码：

```java
public static void main(String[] args) {
    FastGenerator.create("jdbc:mysql://localhost:3306/your_database", "username", "password")
            .initialize() // 一键初始化常用配置项
            .execute("user","role") // 指定要生成的表名
    ;
}
```

## 生成文件说明

生成器会生成以下类型的文件：

1. **Entity(实体类)** - 数据库表对应的实体类
2. **Mapper** - MyBatis Mapper接口
3. **Mapper XML** - MyBatis XML映射文件
4. **Service** - 服务接口
5. **ServiceImpl** - 服务实现类
6. **Controller** - 控制器类
7. **InsertDTO** - 插入数据传输对象
8. **UpdateDTO** - 更新数据传输对象
9. **QueryDTO** - 查询数据传输对象
10. **VO** - 视图对象

## 自定义配置示例

```java
public static void main(String[] args) {
    FastGenerator.create("jdbc:mysql://localhost:3306/your_database", "username", "password")
            // 数据源配置
            .datasource(e -> e
                    .schema("your_schema")
            )
            // 全局配置
            .global(e -> e
                    .author("your_name")
                    .enableLombok()
                    .enableSwagger()
            )
            // 输出配置
            .output(e -> e
                    .outputDir("D:/project/src/main/java") // 指定文件输出目录
                    .parentPackage("com.example.project") // 父包名
                    .moduleName("system") // 模块名
                    .insertDTO(f -> f
                            .formatPattern("%sInsertDTO") // 格式化名称
                            .subPackage("dto") // 指定文件所在的子包 
                            .templatePath("/templates/insertDTO.java") // 指定模板路径
                            .outputDir("D:/project/src/main/java") // 单独指定文件输出目录
                            .disable() // 禁用生成
                    )
                    .updateDTO(f -> f.formatPattern("%sUpdateDTO"))
                    .queryDTO(f -> f.formatPattern("%sQueryDTO"))
                    .queryVO(f -> f.formatPattern("%sVO"))
            )
            // 策略配置
            .strategy(e -> e
                    .enableCapitalMode()
                    .enableSkipView()
                    .disableSqlFilter()
                    .addInclude("table1", "table2") // 指定需要生成的表名
                    .addExclude("test") // 排除生成的表名
                    .addTablePrefix("t_", "c_") // 添加表前缀
                    .addFieldPrefix("is_", "has_") // 添加字段前缀
            )
            // 实体配置
            .entity(e -> e
                    .enableTableFieldAnnotation()
                    .idType(IdType.AUTO)
                    .naming(NamingStrategy.underline_to_camel)
                    .columnNaming(NamingStrategy.underline_to_camel)
                    .logicDeleteColumnName("deleted")
                    .versionColumnName("version")
            )
            // Mapper配置
            .mapper(e -> e
                    .enableBaseResultMap()
                    .enableBaseColumnList()
                    .sortColumn("create_time", true)
                    .sortColumn("id", true)
            )
            // Service配置
            .service(e -> e
                    .superServiceClass("com.baomidou.mybatisplus.extension.service.IService")
                    .superServiceImplClass("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl")
            )
            // Controller配置
            .controller(e -> e
                    .enableCrossOrigin()
            )
            // 模型配置
            .model(e -> e
                    .enableQueryDTOExtendsEntity()
            )
            // 指定需要生成的表名       
            .execute("user","role");
    ;
}
```

## 配置说明

### 全局配置 (GlobalConfig)

| 配置方法 | 参数类型 | 详细说明                           |
|---------|---------|--------------------------------|
| author | String | 设置代码作者名称                       |
| dateType | DateType | 设置时间类型策略                       |
| commentDate | String | 指定注释日期格式化模式                    |
| enableLombok | - | 开启lombok模型                     |
| enableChainModel | - | 开启链式getter和setter              |
| enableCommentLink | - | 文档注释添加相关类链接                    |
| enableCommentUUID | - | 启用类注释随机UUID                    |
| disableValidated | - | 禁用新增和修改的入参校验                   |
| enableSwagger | - | 使用swagger文档                    |
| enableSpringdoc | - | 使用springdoc文档                  |
| enableJavaxApi | - | 使用javax包作为javaEE api           |
| enableEasyExcel | - | 使用EasyExcel(默认为FastExcel)      |
| enableMybatisPlusEnhancer | - | 使用mybatis-plus-enhancer(需自行引入依赖) |
| disableQuery | - | 不生成查询方法                        |
| disableInsert | - | 不生成新增方法                        |
| disableUpdate | - | 不生成更新方法                        |
| disableDelete | - | 不生成删除方法                        |
| disableImport | - | 不生成导入方法                        |
| disableExport | - | 不生成导出方法                        |

### 输出配置 (OutputConfig)

| 配置方法 | 参数类型 | 详细说明 |
|---------|---------|---------|
| outputDir | String | 设置文件输出目录 |
| parentPackage | String | 设置父包名 |
| moduleName | String | 设置模块名 |
| enableGlobalFileOverride | - | 启用全局文件覆盖 |
| disableOpenOutputDir | - | 禁用打开输出目录 |
| entity | Function<TemplateFile.Adapter, TemplateFile.Adapter> | 实体类配置 |
| mapper | Function<TemplateFile.Adapter, TemplateFile.Adapter> | mapper配置 |
| mapperXml | Function<TemplateFile.Adapter, TemplateFile.Adapter> | mapperXml配置 |
| service | Function<TemplateFile.Adapter, TemplateFile.Adapter> | service配置 |
| serviceImpl | Function<TemplateFile.Adapter, TemplateFile.Adapter> | serviceImpl配置 |
| controller | Function<TemplateFile.Adapter, TemplateFile.Adapter> | controller配置 |
| insertDTO | Function<TemplateFile.Adapter, TemplateFile.Adapter> | insertDTO配置 |
| updateDTO | Function<TemplateFile.Adapter, TemplateFile.Adapter> | updateDTO配置 |
| queryDTO | Function<TemplateFile.Adapter, TemplateFile.Adapter> | queryDTO配置 |
| queryVO | Function<TemplateFile.Adapter, TemplateFile.Adapter> | vo配置 |

### 策略配置 (StrategyConfig)

| 配置方法 | 参数类型 | 详细说明 |
|---------|---------|---------|
| addTablePrefix | String... | 增加过滤表前缀 |
| addTableSuffix | String... | 增加过滤表后缀 |
| addFieldPrefix | String... | 增加过滤字段前缀 |
| addFieldSuffix | String... | 增加过滤字段后缀 |
| addInclude | String... | 增加包含的表名 |
| addExclude | String... | 增加排除表 |
| likeTable | LikeTable | 包含表名 |
| notLikeTable | LikeTable | 不包含表名 |
| extraFieldSuffix | String, String | 额外字段后缀 |
| clearExtraFieldSuffix | - | 清除额外字段后缀 |
| extraFieldStrategy | BiFunction<String, TableField, Boolean> | 额外字段策略 |
| enableCapitalMode | - | 开启大写命名 |
| enableSkipView | - | 开启跳过视图 |
| enableSchema | - | 启用 schema |
| disableSqlFilter | - | 禁用sql过滤 |

### 实体配置 (EntityConfig)

| 配置方法 | 参数类型 | 详细说明 |
|---------|---------|---------|
| superClass | Class<?> 或 String | 自定义继承的Entity类 |
| versionColumnName | String | 设置乐观锁数据库表字段名称 |
| versionPropertyName | String | 设置乐观锁实体属性字段名称 |
| logicDeleteColumnName | String | 逻辑删除数据库字段名称 |
| logicDeletePropertyName | String | 逻辑删除实体属性名称 |
| naming | NamingStrategy | 数据库表映射到实体的命名策略 |
| columnNaming | NamingStrategy | 数据库表字段映射到实体的命名策略 |
| addSuperEntityColumns | String... | 添加父类公共字段 |
| addIgnoreColumns | String... | 添加忽略字段 |
| addTableFills | IFill... | 添加表字段填充 |
| idType | IdType | 指定生成的主键的ID类型 |
| enableSerialAnnotation | - | 启用生成 @Serial |
| enableColumnConstant | - | 开启生成字段常量 |
| enableRemoveIsPrefix | - | 开启Boolean类型字段移除is前缀 |
| enableTableFieldAnnotation | - | 开启生成实体时生成字段注解 |
| enableActiveRecord | - | 开启 ActiveRecord 模式 |
| disableSerialVersionUID | - | 禁用生成serialVersionUID |

### Mapper配置 (MapperConfig)

| 配置方法 | 参数类型 | 详细说明 |
|---------|---------|---------|
| superClass | String 或 Class<?> | 父类Mapper |
| mapperAnnotation | Class<? extends Annotation> | 标记 MapperConfig 注解 |
| enableBaseResultMap | - | 开启baseResultMap |
| enableBaseColumnList | - | 开启baseColumnList |
| cache | Class<? extends Cache> | 设置缓存实现类 |
| clearSortColumnMap | - | 清空排序字段 |
| sortColumn | String, boolean | 添加排序字段 |

### Service配置 (ServiceConfig)

| 配置方法 | 参数类型 | 详细说明 |
|---------|---------|---------|
| superServiceClass | Class<?> 或 String | Service接口父类 |
| superServiceImplClass | Class<?> 或 String | Service实现类父类 |

### Controller配置 (ControllerConfig)

| 配置方法 | 参数类型 | 详细说明 |
|---------|---------|---------|
| superClass | Class<?> 或 String | 父类控制器 |
| disableRestController | - | 关闭@RestController控制器 |
| disableHyphenStyle | - | 关闭驼峰转连字符 |
| baseUrl | String | controller请求前缀 |
| enableCrossOrigin | - | 跨域注解 |
| disableBatchQueryPost | - | 禁止批量数据查询使用post请求 |
| enableRestful | - | 增删查改使用restful风格 |
| disablePathVariable | - | 禁用路径变量 |
| disableRequestBody | - | 禁用消息体接收数据 |
| returnMethod | SFunction<Object, R> | 指定controller的返回结果包装类及方法 |
| pageMethod | SFunction<IPage<O>, R> | 指定controller返回的分页包装类及方法 |
| queryParam | Class<?> | 指定用于查询的类 |

### 模型配置 (ModelConfig)

| 配置方法 | 参数类型 | 详细说明 |
|---------|---------|---------|
| enableQueryDTOExtendsEntity | - | 查询dto继承实体类 |
| enableQueryVOExtendsEntity | - | 查询vo继承实体类 |





## 注意事项

1. 开启文件覆盖后, 生成的代码会覆盖同名文件，请注意备份重要文件
2. 需要确保数据库连接信息正确
3. 根据实际需要调整配置参数
4. 可以通过自定义模板来满足特殊需求

## 版本依赖
建议依赖版本如下:

| 生成器版本         | mybatis-plus官方生成器 | mybatis-plus-enhancer | 说明                        |
|---------------|-------------------|-----------------------|---------------------------|
| 0.0.3 - 0.0.9 | 3.5.3.2           | -                     | 早期实验版本                    |
| 1.0.0 - 1.0.1 | 3.5.3.2 - 3.5.5   | -                     | 初版                        |
| 1.2.0 - 1.2.7 | 3.5.7 - 3.5.11    | 1.2.0 - 1.2.4         | 聚合mybatis-plus-enhancer功能 |
| 2.0.0         | -                 | 2.0.0及以上(可选)          | 重构细分配置, 移除非必要依赖, 提升兼容性    |


