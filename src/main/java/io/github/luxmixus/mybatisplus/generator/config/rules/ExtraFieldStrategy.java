package io.github.luxmixus.mybatisplus.generator.config.rules;


import io.github.luxmixus.mybatisplus.generator.config.po.TableField;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 额外字段配置
 *
 * @author luxmixus
 */
public class ExtraFieldStrategy implements BiFunction<String, TableField, Boolean> {
    private static final List<String> ALLOW_COMPARE = Arrays.asList(
            "Byte"
            ,
            "Short"
            ,
            "Integer"
            ,
            "Long"
            ,
            "Float"
            ,
            "Double"
            ,
            "BigInteger"
            ,
            "BigDecimal"
            ,
            "Date"
            ,
            "Time"
            ,
            "Timestamp"
            ,
            "LocalDate"
            ,
            "LocalTime"
            ,
            "LocalDateTime"
    );
    private static final List<String> ALLOW_MULTI = Arrays.asList(
            "Byte"
            ,
            "Short"
            ,
            "Integer"
            ,
            "Long"
//            ,
//            "Float"
//            ,
//            "Double"
//            ,
//            "BigInteger"
//            ,
//            "BigDecimal"
//            ,
//            "Date"
//            ,
//            "Time"
//            ,
//            "Timestamp"
            ,
            "LocalDate"
//            ,
//            "LocalTime"
//            ,
//            "LocalDateTime"
    );

    @Override
    public Boolean apply(String sqlOperator, TableField tableField) {
        if (sqlOperator == null || sqlOperator.isEmpty() || tableField == null) {
            return false;
        }
        sqlOperator = sqlOperator.toUpperCase();
        String propertyType = tableField.getPropertyType();
        boolean isKeyFlag = tableField.isKeyFlag();
        boolean isNullable = tableField.getMetaInfo().isNullable();
        boolean isString = "String".equals(propertyType);
//        int length = tableField.getMetaInfo().getLength();
//        boolean isShortString = isString && length > 0 && length <= 32;
        boolean isIdColumn = tableField.getColumnName().endsWith("id");

        // 大小比较
        if (">".equalsIgnoreCase(sqlOperator)
            || "<".equalsIgnoreCase(sqlOperator)
            || ">=".equalsIgnoreCase(sqlOperator)
            || "<=".equalsIgnoreCase(sqlOperator)
        ) {
            return ALLOW_COMPARE.contains(propertyType)
                    && !isKeyFlag
                    && !isIdColumn
                    ;
        }

        // 模糊查询
        if ("LIKE".equalsIgnoreCase(sqlOperator) 
                || "NOT LIKE".equalsIgnoreCase(sqlOperator)
        ) {
            return isString;
        }

        // in查询
        if ("IN".equalsIgnoreCase(sqlOperator) 
                || "NOT IN".equalsIgnoreCase(sqlOperator)
        ) {
            return ALLOW_MULTI.contains(propertyType)
//                    || isShortString
                    || isKeyFlag
                    || isIdColumn
                    ;
        }

        // 是否为空
        if ("IS NULL".equalsIgnoreCase(sqlOperator) 
                || "IS NOT NULL".equalsIgnoreCase(sqlOperator)
        ) {
            return isNullable
                    && !isKeyFlag
                    ;
        }
        
        // 比特位
        if (sqlOperator.contains("&")){
            return "Byte".equals(propertyType)
                    || "Short".equals(propertyType)
                    || "Integer".equals(propertyType)
                    || "Long".equals(propertyType)
                    ;
        }
        return true;
    }

}
