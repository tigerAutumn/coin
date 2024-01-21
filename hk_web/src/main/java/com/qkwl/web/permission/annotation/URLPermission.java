package com.qkwl.web.permission.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.qkwl.web.permission.Enum.URLTypeEnum;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface URLPermission {

    URLTypeEnum urlType() default URLTypeEnum.UNDEFINED_URL;
}
