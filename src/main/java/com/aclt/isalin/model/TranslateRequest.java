package com.aclt.isalin.model;


import lombok.Builder;

@Builder
public class TranslateRequest {

    private String input;
    private String source;
    private String target;

    public static class TranslateRequestBuilder {
        public String build() {
            return String.format("{ \"q\": \"%s\", \"source\": \"%s\", \"target\": \"%s\" }",
                this.input, this.source, this.target);
        }
    }
}
