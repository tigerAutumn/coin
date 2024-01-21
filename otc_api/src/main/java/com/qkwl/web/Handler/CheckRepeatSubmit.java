package com.qkwl.web.Handler;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention (RetentionPolicy.RUNTIME)
@Documented
public @interface CheckRepeatSubmit {
   boolean save() default false;
   boolean remove() default false;
}
