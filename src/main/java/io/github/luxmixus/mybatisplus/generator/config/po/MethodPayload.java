package io.github.luxmixus.mybatisplus.generator.config.po;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 方法承载信息
 * @author luxmixus
 */
@Getter
@NoArgsConstructor
public class MethodPayload extends ClassPayload {
    protected String methodName;
    protected int methodGenericTypeCount;
    protected boolean isStatic;
    protected boolean isConstructor;
    protected boolean isGenericMethod;

    public MethodPayload(Method method) {
        super(method.getDeclaringClass());
        this.methodName = method.getName();
        this.methodGenericTypeCount = method.getTypeParameters().length;
        this.isStatic = Modifier.isStatic(method.getModifiers());
        this.isConstructor = method.getName().startsWith("<init>");
    }
    
    public boolean isMethodReady(){
        return methodName != null;
    }

    public MethodPayload(Constructor<?> method) {
        super(method.getDeclaringClass());
        this.methodName = method.getName();
        this.methodGenericTypeCount = method.getTypeParameters().length;
        this.isStatic = Modifier.isStatic(method.getModifiers());
        this.isConstructor = true;
    }

    public String invokeMethodStr(String parametersStr) {
        if (methodName == null) {
            return parametersStr;
        }
        if (isStatic) {
            return String.format("%s.%s(%s)" , classSimpleName, methodName, parametersStr);
        }
        if (isConstructor) {
            return String.format("new %s%s(%s)" , classSimpleName, classGenericTypeCount == 1 ? "<>" : "" , parametersStr);
        }
        if (isGenericMethod) {
            return String.format("new %s<>().%s(%s)" , classSimpleName, methodName, parametersStr);
        }
//        if (classGenericTypeCount == 1) {
//            return String.format("new %s<%s>().%s(%s)" , classSimpleName, returnGenericTypeStr, methodName, parametersStr);
//        }
        return String.format("new %s().%s(%s)" , classSimpleName, methodName, parametersStr);
    }

}
