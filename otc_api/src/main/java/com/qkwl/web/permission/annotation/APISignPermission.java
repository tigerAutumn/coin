package com.qkwl.web.permission.annotation;

import com.qkwl.web.permission.Enum.URLTypeEnum;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface APISignPermission {

    URLTypeEnum urlType() default URLTypeEnum.APISIGN_URL;
}
