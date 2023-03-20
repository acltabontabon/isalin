package com.aclt.isalin;

import lombok.Getter;

public enum Language {

    AUTO_DETECT(""),
    AFRIKAANS("af"),
    ENGLISH("en"),
    FILIPINO("fil"),
    JAPANESE("ja");

    @Getter
    private final String code;

    Language(String code) {
        this.code = code;
    }
}
