package com.acltabontabon.isalin.service;

import com.acltabontabon.isalin.Language;
import com.acltabontabon.isalin.properties.IsalinProperties;
import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.TranslationServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class IsalinService {

    private final IsalinProperties isalinProperties;

    @SneakyThrows
    public String translate(String input, Language source, Language target) {
        try (TranslationServiceClient serviceClient = TranslationServiceClient.create()) {
            TranslateTextRequest request = TranslateTextRequest.newBuilder()
                    .setParent(LocationName.of(isalinProperties.getProjectId(), "global").toString())
                    .setSourceLanguageCode(source.getCode())
                    .setTargetLanguageCode(target.getCode())
                    .addContents(input)
                    .build();

            TranslateTextResponse response = serviceClient.translateText(request);

            if (!response.getTranslationsList().isEmpty()) {
                return response.getTranslationsList().stream().findFirst().get().getTranslatedText();
            }
        }

        return "";
    }
}
