package io.github.bootystar.mybatisplus.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.example.P;
import com.example.R;
import io.github.bootystar.mybatisplus.enhancer.query.helper.SqlHelper;
import io.github.bootystar.mybatisplus.generator.config.rules.DateType;
import io.github.bootystar.mybatisplus.generator.fill.Column;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author booty
 */
public class Generator4Mysql {


    @Test
    void generator() {
        String url = "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8";
        String username = "root";
        String password = "root";
        FastGenerator
                .create(url, username, password)
                .initialize()
                .global(e -> e

                                .author("bootystar")
                                .dateType(DateType.TIME_PACK)
//                                .commentDate("yyyy-MM-dd HH:mm:ss")
                                .commentDate("yyyy-MM-dd")
                                .enableLombok()
//                                .enableChainModel()
//                                .enableCommentLink()
//                                .enableCommentUUID()
//                                .enableSwagger()
//                                .enableSpringdoc()
                                .enableJavaxApi()
                                .enableMybatisPlusEnhancer()
//                                .enableEasyExcel()
//                                .enableMybatisPlusEnhancer()
//                                .disableQuery()
//                                .disableInsert()
//                                .disableUpdate()
//                                .disableDelete()
//                                .disableImport()
//                                .disableExport()
//                                .disableValidated()

                )

                .model(e -> e
//                                .enableQueryDTOExtendsEntity()
//                        .enableQueryVOExtendsEntity()

                )
                .strategy(e -> e
//                        .extraFieldSuffix("In", "IN")
//                        .extraFieldSuffix("NotIn", "NOT IN")
                        .extraFieldSuffix("Like", "LIKE")
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
                .entity(e -> e
//                        .disable()
                                .logicDeleteColumnName("deleted")
                                .versionColumnName("version")
                                .enableActiveRecord()
                                .idType(IdType.ASSIGN_ID)
                                .logicDeleteColumnName("deleted")
                                .versionColumnName("version")
                                .addTableFills(
                                        new Column("create_time", FieldFill.INSERT),
                                        new Column("update_time", FieldFill.INSERT_UPDATE)
                                )
                )
                .mapper(e -> e
                        .enableBaseResultMap()

                )
                .service(e -> e
                )
                .controller(e -> e
//                        .disableRestController()
//                        .disableHyphenStyle()
                        .baseUrl("/api")
                        .enableCrossOrigin()
                        .disableBatchQueryPost()
                        .enableRestful()
//                        .disablePathVariable()
//                        .disableRequestBody()
                        .returnMethod(R::of)
                        .pageMethod(P::of)
//                        .queryParam(Map.class)
//                        .queryParam(SqlHelper.class)
                )
                .output(e -> e
                                .outputDir(System.getProperty("user.dir") + "/src/test/java")
                                .enableGlobalFileOverride()
                                .disableOpenOutputDir()
                                .parentPackage("com.example.test")
//                                .disableOpenOutputDir()
                                .entity(f -> f

//                                                .disable()
//                                        .formatPattern("%sPOJO")
                                )
                                .mapper(f -> f
//                                                .subPackage("mapper")
//                                .disable()
                                )
                                .mapperXml(f -> f
//                                                .subPackage("mapper")
//                                .disable()
                                )
                                .service(f -> f
//                                                .subPackage("service")
//                                                .disable()
//                                        .disable()
                                )
                                .serviceImpl(f -> f
//                                        .subPackage("service.impl")
//                                                .subPackage("impl")
//                                        .disable()
                                )
                                .controller(f -> f
//                                                .subPackage("controller")
//                                        .disable()
                                )
                                .insertDTO(f -> f
//                                                .subPackage("dto")
//                                .disable()
                                )
                                .updateDTO(f -> f
//                                                .subPackage("dto")
//                                .disable()
                                )
                                .queryDTO(f -> f
//                                                .subPackage("dto")
//                                .disable()
                                )
                                .queryVO(f -> f
//                                                .subPackage("vo")
//                                .disable()
                                )
                )

                .execute("sys_user")
        ;
    }


}