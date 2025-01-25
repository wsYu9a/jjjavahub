package com.wsyu9a.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreventDuplicateSubmit {
    /**
     * 防重复时间间隔，单位毫秒
     */
    long interval() default 1000;
    
    /**
     * 提示消息
     */
    String message() default "提交太频繁，请稍后再试";
} 