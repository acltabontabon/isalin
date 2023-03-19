package com.aclt.isalin;

import lombok.Getter;

public enum Language {

    AFRIKAANS("af"),
    ENGLISH("en"),
    FILIPINO("fil"),
    JAPANESE("ja");

    @Getter
    private String code;

    Language(String code) {
        this.code = code;
    }
}
