package com.acltabontabon.isalin.mock;

import com.acltabontabon.isalin.Language;
import com.acltabontabon.isalin.annotation.Translate;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

import static com.acltabontabon.isalin.mock.MockPayload.Body;

@Service
public class MockService {

    @Value("classpath:sample.pdf")
    private Resource sampleFile;

    @Translate(from = Language.ENGLISH, to = Language.FILIPINO)
    public String methodWithPlainResponse() {
        return "Hello world!";
    }

    @Translate(to = Language.FILIPINO)
    public String methodWithoutSourceLanguage() {
        return "This is an example of a translation without a source language!";
    }

    @Translate(value = "$.body.content", from = Language.ENGLISH, to = Language.FILIPINO)
    public MockPayload methodWithTextWithinCustomObject() {
        MockPayload msg = new MockPayload();
        msg.setSource("Tarzan");
        msg.setBody(new Body("Welcome to the jungle!", null));

        return msg;
    }

    @SneakyThrows
    @Translate(from = Language.ENGLISH, to = Language.FILIPINO)
    public File methodWithDocumentResponse() {
        return sampleFile.getFile();
    }

    @SneakyThrows
    @Translate(value = "$.body.fileContent", from = Language.ENGLISH, to = Language.FILIPINO)
    public MockPayload methodWithDocumentWithinCustomObject() {
        MockPayload msg = new MockPayload();
        msg.setBody(new Body("", sampleFile.getFile()));

        return msg;
    }
}
