package com.aclt.isalin.model;

import lombok.Data;

import java.util.List;

@Data
public class TranslateResponse {

    private Body data;

    @Data
    public static class Body {

        private List<Translation> translations;

    }

    @Data
    public static class Translation {

        private String translatedText;
        private String detectedSourceLanguage;

    }
}
