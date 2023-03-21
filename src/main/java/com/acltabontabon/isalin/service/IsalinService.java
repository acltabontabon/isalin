package com.acltabontabon.isalin.service;

import com.acltabontabon.isalin.Language;
import com.acltabontabon.isalin.model.TranslateRequest;
import com.acltabontabon.isalin.model.TranslateResponse;
import com.acltabontabon.isalin.properties.IsalinProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class IsalinService {

    private final IsalinProperties isalinProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String translate(String input, Language source, Language target) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(createUrl())
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(createPayload(input, source, target))
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

            log.debug("No translation found - result: {}", httpResponse.body());
            throw new RuntimeException("No translation found!");
        } catch (Exception ex) {
            log.error("Failed to translate '{}' using the service url '{}'", input, isalinProperties.getServiceUrl(), ex);
            throw new RuntimeException(ex);
        }
    }

    private HttpRequest.BodyPublisher createPayload(String input, Language source, Language target) {
        return HttpRequest.BodyPublishers.ofString(
                TranslateRequest.builder()
                        .input(input)
                        .source(source.getCode())
                        .target(target.getCode())
                        .build()
        );
    }

    @SneakyThrows
    private URI createUrl() {
        return new URI(String.format("%s?key=%s", isalinProperties.getServiceUrl(), isalinProperties.getServiceKey()));
    }
}
