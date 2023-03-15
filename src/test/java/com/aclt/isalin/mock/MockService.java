package com.aclt.isalin.mock;

import com.aclt.isalin.annotation.Translate;
import org.springframework.stereotype.Service;

@Service
public class MockService {

    @Translate
    public String methodWithPlainResponse() {
        return "Hello world!";
    }

    @Translate("$.body.content")
    public MockPayload methodWithJsonResponse() {
        MockPayload msg = new MockPayload();
        msg.setSource("Tarzan");
        msg.setBody(new MockPayload.Body("Welcome to the jungle!"));

        return msg;
    }
}
