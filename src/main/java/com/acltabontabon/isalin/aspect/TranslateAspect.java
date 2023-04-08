package com.acltabontabon.isalin.aspect;

import com.acltabontabon.isalin.annotation.Translate;
import com.acltabontabon.isalin.service.IsalinService;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
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

/**
 * The aspect used to intercept the methods annotated by {@link Translate}.
 */
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

        if (joinPointResult == null) {
            return joinPointResult;
        }

        return switch (method.getReturnType().getSimpleName().toLowerCase()) {
            case "void" -> joinPointResult;
            case "string" -> isalinService.translateText(joinPointResult.toString(), translate.from(), translate.to());
            case "file" -> isalinService.translateDocument(((File) joinPointResult), translate.from(), translate.to());
            case "list" -> processList((List<?>) joinPointResult, translate);
            default -> processCustomObject(joinPointResult, translate) ;
        };
    }

    private boolean containsText(List<?> list) {
        if (list.isEmpty()) {
            return false;
        }

        return list.get(0) instanceof String;
    }

    private boolean containsFile(List<?> list) {
        if (list.isEmpty()) {
            return false;
        }

        return list.get(0) instanceof File;
    }

    @SuppressWarnings("unchecked")
    private Object processList(List<?> joinPointResult, Translate translate) {
        if (containsText(joinPointResult)) {
            return isalinService.translateTexts((List<String>) joinPointResult, translate.from(), translate.to());
        }

        if (containsFile(joinPointResult)) {
            return isalinService.translateDocuments((List<File>) joinPointResult, translate.from(), translate.to());
        }

        return joinPointResult;
    }

    @SuppressWarnings("unchecked")
    private Object processCustomObject(Object joinPointResult, Translate translate) {
        if (!StringUtils.hasText(translate.value())) {
            return joinPointResult;
        }

        String fieldMapping = translate.value().replace(TARGET_REF_KEY, "");
        EvaluationContext context = new StandardEvaluationContext(joinPointResult);
        Object rawValue = expressionParser.parseExpression(fieldMapping).getValue(context);
        Object translation = null;

        if (rawValue instanceof String s) {
            translation = isalinService.translateText(s, translate.from(), translate.to());
        } else if (rawValue instanceof List<?> l) {
            if (containsText(l)) {
                translation = isalinService.translateTexts((List<String>) l, translate.from(), translate.to());
            } else if (containsFile(l)) {
                translation = isalinService.translateDocuments((List<File>) l, translate.from(), translate.to());
            }
        } else if (rawValue instanceof File f) {
            translation = isalinService.translateDocument(f, translate.from(), translate.to());
        } else {
            log.error("Unsupported target object '{}'", joinPointResult.getClass());
        }

        if (translation != null) {
            updateCustomObject(joinPointResult, fieldMapping, translation);
        }

        return joinPointResult;
    }

    /**
     * Assign translated value to the target field within a custom object.
     * fieldMapping traversal:
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
    private void updateCustomObject(Object targetObj, String fieldMapping, Object valueToAssign) {
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

        updateCustomObject(targetField.get(targetObj), child, valueToAssign);
    }
}