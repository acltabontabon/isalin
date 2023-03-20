package com.aclt.isalin.mock;

import com.aclt.isalin.Language;
import com.aclt.isalin.annotation.Translate;
import org.springframework.stereotype.Service;

@Service
public class MockService {

    @Translate(from = Language.ENGLISH, to = Language.FILIPINO)
    public String methodWithPlainResponse() {
        return "Hello world!";
    }

    @Translate(to = Language.FILIPINO)
    public String methodWithoutSourceLanguage() {
        return "This is an example of a translation without a source language!";
    }

    @Translate(value = "$.body.content", to = Language.FILIPINO)
    public MockPayload methodWithJsonResponse() {
        MockPayload msg = new MockPayload();
        msg.setSource("Tarzan");
        msg.setBody(new MockPayload.Body("Welcome to the jungle!"));

        return msg;
    }
}
