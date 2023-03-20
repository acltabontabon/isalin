package com.aclt.isalin.aspect;

import com.aclt.isalin.annotation.Translate;
import com.aclt.isalin.service.IsalinService;
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
    @Around("@annotation(com.aclt.isalin.annotation.Translate)")
    public Object aroundTranslate(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object joinPointResult = joinPoint.proceed();

        if (method.getReturnType().equals(Void.TYPE)) {
            return joinPointResult;
        }

        return translate(joinPointResult, method.getAnnotation(Translate.class));
    }

    private Object translate(Object joinPointResult, Translate annotation) {
        try {
            if (StringUtils.hasText(annotation.value())) {
                String fieldMapping = annotation.value().replace(TARGET_REF_KEY, "");

                EvaluationContext context = new StandardEvaluationContext(joinPointResult);
                String rawValue = expressionParser.parseExpression(fieldMapping).getValue(context).toString();
                String translatedValue = isalinService.translate(rawValue, annotation.from(), annotation.to());

                assignTranslation(joinPointResult, fieldMapping, translatedValue);

                return joinPointResult;
            }

            return isalinService.translate(joinPointResult.toString(), annotation.from(), annotation.to());
        } catch (Exception ex) {
            return joinPointResult;
        }
    }

    /**
     * Assign translated value to the target field.
     * expression traversal:
     *      n+1:
     *          targetObj = joinPointResult
     *          expression = body.content
     *      n+2:
     *          targetObj = body
     *          expression = content
     *
     * @param targetObj - holds the object instance
     * @param fieldMapping - holds the mapping for the target field
     * @param valueToAssign - holds the translated value
     */
    @SneakyThrows
    private void assignTranslation(Object targetObj, String fieldMapping, String valueToAssign) {
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