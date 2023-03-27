package com.acltabontabon.isalin.aspect;

import com.acltabontabon.isalin.annotation.Translate;
import com.acltabontabon.isalin.service.IsalinService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class TranslateAspect {

    private static final String TARGET_REF_KEY = "$.";

    private final IsalinService isalinService;

    private final ExpressionParser expressionParser = new SpelExpressionParser();


    @SneakyThrows
    @Around("@annotation(com.acltabontabon.isalin.annotation.Translate)")
    public Object aroundTranslate(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Translate translate = method.getAnnotation(Translate.class);
        Object joinPointResult = joinPoint.proceed();

        return switch (method.getReturnType().getSimpleName().toLowerCase()) {
            case "void" -> joinPointResult;
            case "string" -> isalinService.translateText(joinPointResult.toString(), translate.from(), translate.to());
            case "file" -> isalinService.translateDocument(((File) joinPointResult).getAbsolutePath(), translate.from(), translate.to());
            default -> processCustomObject(joinPointResult, translate) ;
        };
    }

    private Object processCustomObject(Object joinPointResult, Translate translate) {
        if (StringUtils.hasText(translate.value())) {
            String fieldMapping = translate.value().replace(TARGET_REF_KEY, "");

            EvaluationContext context = new StandardEvaluationContext(joinPointResult);
            Object rawValue = expressionParser.parseExpression(fieldMapping).getValue(context);

            if (rawValue instanceof String s) {
                String result = isalinService.translateText(s, translate.from(), translate.to());

                assignTranslation(joinPointResult, fieldMapping, result);
            } else if (rawValue instanceof File f) {
                File result = isalinService.translateDocument(f.getAbsolutePath(), translate.from(), translate.to());

                assignTranslation(joinPointResult, fieldMapping, result);
            } else {
                log.error("Unsupported target object '{}'", joinPointResult.getClass());
            }
        }

        return joinPointResult;
    }

    /**
     * Assign translated value to the target field.
     * expression traversal:
     *      n+1:
     *          targetObj = joinPointResult
     *          fieldMapping = body.content
     *      n+2:
     *          targetObj = body
     *          fieldMapping = content
     *
     * @param targetObj - holds the object instance
     * @param fieldMapping - holds the mapping for the target field
     * @param valueToAssign - holds the translated value
     */
    @SneakyThrows
    private void assignTranslation(Object targetObj, String fieldMapping, Object valueToAssign) {
        if (!fieldMapping.contains(".")) {
            Field targetField = targetObj.getClass().getDeclaredField(fieldMapping);
            targetField.setAccessible(true);
            targetField.set(targetObj, valueToAssign);

            return;
        }

        String parent = fieldMapping.substring(0, fieldMapping.indexOf("."));
        String child = fieldMapping.substring(fieldMapping.indexOf(".") + 1);
        Field targetField = targetObj.getClass().getDeclaredField(parent);
        targetField.setAccessible(true);

        assignTranslation(targetField.get(targetObj), child, valueToAssign);
    }
}