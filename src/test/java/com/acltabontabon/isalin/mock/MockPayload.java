package com.acltabontabon.isalin.mock;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class MockPayload {

    private String source;

    private Body body;

    private String content;

    @Data
    @AllArgsConstructor
    public static class Body {

        private String content;

    }
}
