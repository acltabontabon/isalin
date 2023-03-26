package com.acltabontabon.isalin.annotation;

import com.acltabontabon.isalin.Language;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Translate {

    Language from() default Language.ANY;

    Language to();

    String value() default "";

}
