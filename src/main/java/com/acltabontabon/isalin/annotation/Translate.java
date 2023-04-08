package com.acltabontabon.isalin.annotation;

import com.acltabontabon.isalin.Language;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to instrument a qualified method.
 *
 * <p>Supported method return types are:</p>
 * <ul>
 *     <li>{@code String}</li>
 *     <li>{@code File}</li>
 *     <li>{@code String}/{@code File} within a custom object mapped by `value` attribute</li>
 * </ul>
 *
 * <p>Example of using a custom object:</p>
 * <pre>{@code
 *     @Translate (value = "$.field1.nestedField1", to = Language.FILIPINO)
 *     public CustomObject getMessage() { }
 * }</pre>
 *
 * @see com.acltabontabon.isalin.aspect.TranslateAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Translate {

    /**
     * Holds the source language.
     */
    Language from() default Language.ANY;

    /**
     * Holds the target language.
     */
    Language to();

    /**
     * Holds the field mapping in case the response type of a method is a custom object.
     */
    String value() default "";

}