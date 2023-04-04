package com.acltabontabon.isalin.mock;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class MockPayload {

    private String source;

    private Body body;

    private String content;

    private List<String> contents;

    private List<File> fileContents;

    @Data
    @AllArgsConstructor
    public static class Body {

        private String content;

        private File fileContent;

    }
}
