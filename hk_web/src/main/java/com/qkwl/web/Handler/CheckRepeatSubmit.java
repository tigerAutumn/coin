package com.qkwl.web.Handler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention (RetentionPolicy.RUNTIME)
@Documented
public @interface CheckRepeatSubmit {
   boolean save() default false;
   boolean remove() default false;
}
