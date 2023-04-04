package com.acltabontabon.isalin.service;

import com.acltabontabon.isalin.Language;
import com.acltabontabon.isalin.properties.IsalinProperties;
import com.google.cloud.translate.v3.DocumentInputConfig;
import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateDocumentRequest;
import com.google.cloud.translate.v3.TranslateDocumentResponse;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class that encapsulates google translation service.
 */
@Slf4j
@RequiredArgsConstructor
public class IsalinService {

    private final IsalinProperties isalinProperties;

    /**
     * Translates a single string.
     *
     * @param input - raw text
     * @param source - source language
     * @param target - target language
     * @return translated text if successful, otherwise raw input
     */
    @SneakyThrows
    public String translateText(String input, Language source, Language target) {
        return translateTexts(List.of(input), source, target).stream().findFirst().orElse(input);
    }

    /**
     * Translates a list of string.
     *
     * @param input - list of raw texts
     * @param source - source language
     * @param target - target language
     * @return translated texts if successful, otherwise raw input
     */
    public List<String> translateTexts(List<String> input, Language source, Language target) {
        List<String> list = new ArrayList<>();

        try (TranslationServiceClient translationServiceClient = TranslationServiceClient.create()) {
            TranslateTextRequest request = TranslateTextRequest.newBuilder()
                    .setParent(LocationName.of(isalinProperties.getProjectId(), "global").toString())
                    .setSourceLanguageCode(source.getCode())
                    .setTargetLanguageCode(target.getCode())
                    .addAllContents(input)
                    .build();

            TranslateTextResponse response = translationServiceClient.translateText(request);

            if (!response.getTranslationsList().isEmpty()) {
                list = response.getTranslationsList().stream().map(Translation::getTranslatedText).collect(Collectors.toList());
            }

            return list;
        } catch (Exception e) {
            log.error("Text translation request failed. Reason: {}", e.getMessage(), e);
        }

        return input;
    }

    public File translateDocument(File input, Language source, Language target) {
        return translateDocuments(List.of(input), source, target).stream().findFirst().orElse(input);
    }

    public List<File> translateDocuments(List<File> input, Language source, Language target) {
        List<File> translatedDocs = new ArrayList<>();

        try (TranslationServiceClient client = TranslationServiceClient.create()) {
            for (File file: input) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    File tmpFile = Files.createTempFile("isalin-", file.getName()).toFile();
                    tmpFile.deleteOnExit();

                    DocumentInputConfig documentInputConfig = DocumentInputConfig.newBuilder()
                            .setContent(ByteString.readFrom(fis))
                            .setMimeType(getMimeType(file.getName()))
                            .build();

                    TranslateDocumentRequest request = TranslateDocumentRequest.newBuilder()
                            .setParent(LocationName.of(isalinProperties.getProjectId(), "global").toString())
                            .setSourceLanguageCode(source.getCode())
                            .setTargetLanguageCode(target.getCode())
                            .setDocumentInputConfig(documentInputConfig)
                            .build();

                    TranslateDocumentResponse response = client.translateDocument(request);
                    Files.write(tmpFile.toPath(), response.getDocumentTranslation().getByteStreamOutputs(0).toByteArray());

                    log.info("Document translation for {} is completed: {}", file.getName(), tmpFile.getAbsolutePath());
                    translatedDocs.add(tmpFile);
                }
            }
        } catch (IOException e) {
            log.error("Docs translation request failed. Reason: {}", e.getMessage(), e);
            return input;
        }

        return translatedDocs;
    }

    private String getMimeType(String filename) {
        String ext = filename.substring(filename.lastIndexOf(".") + 1);

        return switch (ext) {
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "pdf" -> "application/pdf";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default -> "application/octet-stream";
        };
    }
}
