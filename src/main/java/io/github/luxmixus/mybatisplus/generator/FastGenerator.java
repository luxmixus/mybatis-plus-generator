package io.github.luxmixus.mybatisplus.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import io.github.luxmixus.mybatisplus.generator.config.Configurer;
import io.github.luxmixus.mybatisplus.generator.config.rules.DateType;
import io.github.luxmixus.mybatisplus.generator.config.rules.DbColumnType;
import io.github.luxmixus.mybatisplus.generator.config.support.*;
import io.github.luxmixus.mybatisplus.generator.engine.VelocityTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;

import java.io.File;
import java.util.function.Function;

/**
 * @author luxmixus
 */
@Slf4j
public class FastGenerator {
    private final Configurer configurer;

    private FastGenerator(String url, String username, String password) {
        this.configurer = new Configurer(url, username, password);
    }

    public static FastGenerator create(String url, String username, String password) {
        return new FastGenerator(url, username, password);
    }

    public void execute(String... tableNames) {
        if (tableNames.length > 0) {
            configurer.getStrategyConfig().adapter().addInclude(tableNames);
        }
        log.debug("==========================准备生成文件...==========================");
        VelocityTemplateEngine templateEngine = new VelocityTemplateEngine(this.configurer);
        // 模板引擎初始化执行文件输出
        templateEngine.batchOutput().open();
        log.debug("==========================文件生成完成！！！==========================");
        String banner = ".__                       .__                     \n" +
                "|  |  __ _____  ___ _____ |__| ________ __  ______\n" +
                "|  | |  |  \\  \\/  //     \\|  |/  ___/  |  \\/  ___/\n" +
                "|  |_|  |  />    <|  Y Y  \\  |\\___ \\|  |  /\\___ \\ \n" +
                "|____/____//__/\\_ \\__|_|  /__/____  >____//____  >\n" +
                "                 \\/     \\/        \\/           \\/ ";
        System.out.println(banner);
        System.out.println("execute success! check files in following folder:");
        String path = configurer.getOutputConfig().getOutputDir();
        System.out.println(new File(path).getAbsolutePath());
    }

    public FastGenerator initialize() {
        this
                .datasource(e -> e
                        .typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            if (JdbcType.TINYINT == metaInfo.getJdbcType()) {
                                return DbColumnType.INTEGER;
                            }
                            if (JdbcType.SMALLINT == metaInfo.getJdbcType()) {
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                .global(e -> e
                        .dateType(DateType.TIME_PACK)
                        .enableLombok()
                        .enableCommentLink()
//                        .enableCommentUUID()
                )
                .output(e -> e
                        .disableOpenOutputDir()
//                        .insertDTO(f -> f.formatPattern("%sIO"))
//                        .updateDTO(f -> f.formatPattern("%sUO"))
//                        .queryDTO(f -> f.formatPattern("%sQO"))
                        .queryVO(f -> f.formatPattern("%sVO"))
                )
                .strategy(e -> e
                                .extraFieldSuffix("In", "IN")
                                .extraFieldSuffix("Like", "LIKE")
                                .extraFieldSuffix("Le", "<=")
                                .extraFieldSuffix("Ge", ">=")
//                        .extraFieldSuffix("In", "IN")
//                        .extraFieldSuffix("NotIn", "NOT IN")
//                        .extraFieldSuffix("Like", "LIKE")
//                        .extraFieldSuffix("NotLike", "NOT LIKE")
//                        .extraFieldSuffix("IsNull", "IS NULL")
//                        .extraFieldSuffix("IsNotNull", "IS NOT NULL")
//                        .extraFieldSuffix("Ne", "!=")
//                        .extraFieldSuffix("Lt", "<")
//                        .extraFieldSuffix("Le", "<=")
//                        .extraFieldSuffix("Ge", ">=")
//                        .extraFieldSuffix("Gt", ">")
//                        .extraFieldSuffix("WithBit", "&>")
//                        .extraFieldSuffix("WithoutBit", "&=")
                )
                .injection(e -> e)
                .entity(e -> e
                        .idType(IdType.ASSIGN_ID)
                        .logicDeleteColumnName("deleted")
                        .versionColumnName("version")
//                        .addTableFills(
//                                new Column("create_time", FieldFill.INSERT),
//                                new Column("update_time", FieldFill.INSERT_UPDATE),
//                                new Column("created_time", FieldFill.INSERT),
//                                new Column("updated_time", FieldFill.INSERT_UPDATE),
//                                new Column("create_at", FieldFill.INSERT),
//                                new Column("update_at", FieldFill.INSERT_UPDATE),
//                                new Column("created_at", FieldFill.INSERT),
//                                new Column("updated_at", FieldFill.INSERT_UPDATE),
//                                new Column("creator_id", FieldFill.INSERT),
//                                new Column("updater_id", FieldFill.INSERT_UPDATE),
//                                new Column("create_by", FieldFill.INSERT),
//                                new Column("update_by", FieldFill.INSERT_UPDATE),
//                                new Column("created_by", FieldFill.INSERT),
//                                new Column("updated_by", FieldFill.INSERT_UPDATE)
//                        )
                )
                .model(e -> e
//                        .enableQueryDTOExtendsEntity()
//                        .enableQueryVOExtendsEntity()
                                .addEditExcludeColumns("create_time", "update_time")
                )
                .mapper(e -> e
                        .mapperAnnotation(org.apache.ibatis.annotations.Mapper.class)
                        .sortColumn("order", false)
                        .sortColumn("rank", false)
                        .sortColumn("sort", false)
                        .sortColumn("seq", false)
                        .sortColumn("sequence", false)
                        .sortColumn("create_time", true)
                        .sortColumn("id", true))
                .service(e -> e
                )
                .controller(e -> e
                );
        return this;
    }


    public FastGenerator datasource(Function<DataSourceConfig.Adapter, DataSourceConfig.Adapter> builder) {
        builder.apply(this.configurer.getDataSourceConfig().adapter());
        return this;
    }

    public FastGenerator global(Function<GlobalConfig.Adapter, GlobalConfig.Adapter> builder) {
        builder.apply(this.configurer.getGlobalConfig().adapter());
        return this;
    }

    public FastGenerator output(Function<OutputConfig.Adapter, OutputConfig.Adapter> builder) {
        builder.apply(this.configurer.getOutputConfig().adapter());
        return this;
    }

    public FastGenerator strategy(Function<StrategyConfig.Adapter, StrategyConfig.Adapter> builder) {
        builder.apply(this.configurer.getStrategyConfig().adapter());
        return this;
    }

    public FastGenerator injection(Function<InjectionConfig.Adapter, InjectionConfig.Adapter> builder) {
        builder.apply(this.configurer.getInjectionConfig().adapter());
        return this;
    }

    public FastGenerator entity(Function<EntityConfig.Adapter, EntityConfig.Adapter> builder) {
        builder.apply(this.configurer.getEntityConfig().adapter());
        return this;
    }

    public FastGenerator mapper(Function<MapperConfig.Adapter, MapperConfig.Adapter> builder) {
        builder.apply(this.configurer.getMapperConfig().adapter());
        return this;
    }

    public FastGenerator service(Function<ServiceConfig.Adapter, ServiceConfig.Adapter> builder) {
        builder.apply(this.configurer.getServiceConfig().adapter());
        return this;
    }

    public FastGenerator controller(Function<ControllerConfig.Adapter, ControllerConfig.Adapter> builder) {
        builder.apply(this.configurer.getControllerConfig().adapter());
        return this;
    }

    public FastGenerator model(Function<ModelConfig.Adapter, ModelConfig.Adapter> builder) {
        builder.apply(this.configurer.getModelConfig().adapter());
        return this;
    }


}