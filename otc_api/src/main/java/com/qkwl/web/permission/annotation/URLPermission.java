package com.qkwl.web.permission.annotation;

import com.qkwl.web.permission.Enum.URLTypeEnum;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface URLPermission {

    URLTypeEnum urlType() default URLTypeEnum.UNDEFINED_URL;
}
