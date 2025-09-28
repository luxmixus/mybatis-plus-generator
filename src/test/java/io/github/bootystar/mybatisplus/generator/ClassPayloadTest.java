package io.github.bootystar.mybatisplus.generator;

import io.github.bootystar.mybatisplus.generator.config.po.ClassPayload;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * @author bootystar
 */
public class ClassPayloadTest {
    
    @Test
    void test1(){
        ClassPayload classPayload = new ClassPayload(Map.class);
        String clazz = classPayload.returnGenericTypeStr();
        System.out.println(clazz);
    }
    
    @Data
    public static class Person{
        private String name = "john";
        private Integer age = 3;
        private LocalDate birthday = LocalDate.now();
        private LocalDateTime createTime = LocalDateTime.now();
        private Date updateTime = new Date();
        private Timestamp deleteTime = new Timestamp(System.currentTimeMillis());
    
    }
}
