package com.pp.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 指明实体中的字段非持久化
 * 
 * @author
 *
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface Transient {

}
