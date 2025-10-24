package io.github.luxmixus.mybatisplus.generator.config.po;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;


/**
 * 类承载信息
 * @author luxmixus
 */
@Getter
@NoArgsConstructor
public class ClassPayload {
    protected Class<?> clazz;
    protected String classPackage;
    protected String classSimpleName;
    protected String classCanonicalName;
    protected int classGenericTypeCount;

    public ClassPayload(String classPackage, String classSimpleName) {
        this.classPackage = classPackage;
        this.classSimpleName = classSimpleName;
        this.classCanonicalName = classPackage+"."+classSimpleName;
    }

    public ClassPayload(Class<?> clazz) {
        this.clazz = clazz;
        this.classPackage = clazz.getPackage().getName();
        this.classSimpleName = clazz.getSimpleName();
        this.classCanonicalName = clazz.getName();
        this.classGenericTypeCount = clazz.getTypeParameters().length;
    }

    public boolean isClassReady() {
        if (classPackage == null || classSimpleName == null || classCanonicalName == null) {
            return false;
        }
        return true;
    }

    public String returnGenericTypeStr(String... genericTypeStr) {
        if (classSimpleName == null) {
            return genericTypeStr!=null && genericTypeStr.length == 1 ? genericTypeStr[0] : "Object";
        }
        if (classGenericTypeCount == 0) {
            return classSimpleName;
        }
        if (genericTypeStr == null || genericTypeStr.length == 0) {
            return String.format("%s<%s>", classSimpleName, String.join(", ", Collections.nCopies(classGenericTypeCount, "?")));
        }
        if (classGenericTypeCount == genericTypeStr.length) {
            return String.format("%s<%s>", classSimpleName, String.join(", ", genericTypeStr));
        }
        return String.format("%s<%s>", classSimpleName, String.join(", ", Collections.nCopies(classGenericTypeCount, "?")));
    }




}
