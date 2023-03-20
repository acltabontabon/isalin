package com.aclt.isalin.aspect;

import com.aclt.isalin.Language;
import com.aclt.isalin.annotation.Translate;
import com.aclt.isalin.model.TranslateRequest;
import com.aclt.isalin.model.TranslateResponse;
import com.aclt.isalin.properties.IsalinProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;

@Aspect
@RequiredArgsConstructor
public class TranslateAspect {

    public static final String TARGET_REF_KEY = "$.";

    private final IsalinProperties isalinProperties;

    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @SneakyThrows
    @Around("@annotation(com.aclt.isalin.annotation.Translate)")
    public Object aroundTranslate(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Translate annotation = method.getAnnotation(Translate.class);

        Object joinPointResult = joinPoint.proceed();

        if (StringUtils.hasText(annotation.value())) {
            String fieldMapping = annotation.value().replace(TARGET_REF_KEY, "");

            EvaluationContext context = new StandardEvaluationContext(joinPointResult);
            String val = Objects.requireNonNull(expressionParser.parseExpression(fieldMapping).getValue(context)).toString();
            String translatedValue = callTranslationService(createPayload(val, annotation.from(), annotation.to()));

            assignTranslation(joinPointResult, fieldMapping, translatedValue);

            return joinPointResult;
        }

        return callTranslationService(createPayload(joinPointResult.toString(), annotation.from(), annotation.to()));
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

    @SneakyThrows
    private String callTranslationService(String payload) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(createUrl())
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Optional<TranslateResponse.Translation> translation = objectMapper
                .readValue(httpResponse.body(), TranslateResponse.class)
                .getData()
                .getTranslations()
                .stream()
                .findAny();

        if (translation.isPresent()) {
            return translation.get().getTranslatedText();
        }

        throw new RuntimeException("Failed to get translation!");
    }

    private String createPayload(String input, Language source, Language target) {
        return TranslateRequest.builder()
                .input(input)
                .source(source.getCode())
                .target(target.getCode())
                .build();
    }

    @SneakyThrows
    private URI createUrl() {
        return new URI(String.format("%s?key=%s", isalinProperties.getServiceUrl(), isalinProperties.getServiceKey()));
    }
}
