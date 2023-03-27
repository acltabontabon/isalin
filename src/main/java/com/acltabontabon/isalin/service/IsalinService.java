package com.acltabontabon.isalin.service;

import com.acltabontabon.isalin.Language;
import com.acltabontabon.isalin.properties.IsalinProperties;
import com.google.cloud.translate.v3.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

@Slf4j
@RequiredArgsConstructor
public class IsalinService {

    private final IsalinProperties isalinProperties;

    @SneakyThrows
    public String translateText(String input, Language source, Language target) {
        try (TranslationServiceClient translationServiceClient = TranslationServiceClient.create()) {
            TranslateTextRequest request = TranslateTextRequest.newBuilder()
                    .setParent(LocationName.of(isalinProperties.getProjectId(), "global").toString())
                    .setSourceLanguageCode(source.getCode())
                    .setTargetLanguageCode(target.getCode())
                    .addContents(input)
                    .build();

            TranslateTextResponse response = translationServiceClient.translateText(request);

            if (!response.getTranslationsList().isEmpty()) {
                return response.getTranslationsList().stream().findFirst().get().getTranslatedText();
            }
            return "";
        }
    }

    @SneakyThrows
    public File translateDocument(String input, Language source, Language target) {
        File inputFile = new File(input);
        File tmpFile = Files.createTempFile("", inputFile.getName()).toFile();
        tmpFile.deleteOnExit();

        try (TranslationServiceClient client = TranslationServiceClient.create();
             FileInputStream fis = new FileInputStream(inputFile)) {

            DocumentInputConfig documentInputConfig =
                    DocumentInputConfig.newBuilder()
                            .setContent(ByteString.readFrom(fis))
                            .setMimeType(getMimeType(inputFile.getName()))
                            .build();

            TranslateDocumentRequest request = TranslateDocumentRequest.newBuilder()
                    .setParent(LocationName.of(isalinProperties.getProjectId(), "global").toString())
                    .setSourceLanguageCode(source.getCode())
                    .setTargetLanguageCode(target.getCode())
                    .setDocumentInputConfig(documentInputConfig)
                    .build();

            TranslateDocumentResponse response = client.translateDocument(request);
            Files.write(tmpFile.toPath(), response.getDocumentTranslation().getByteStreamOutputs(0).toByteArray());
        }

        return tmpFile;
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
