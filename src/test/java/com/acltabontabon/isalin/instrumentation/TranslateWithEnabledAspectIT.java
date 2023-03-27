package com.acltabontabon.isalin.instrumentation;

import com.acltabontabon.isalin.IsalinTestBase;
import com.acltabontabon.isalin.mock.MockPayload;
import com.acltabontabon.isalin.mock.MockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(locations = "classpath:isalin-enabled.properties")
public class TranslateWithEnabledAspectIT extends IsalinTestBase {

    @Autowired
    private MockService mockService;

    @Test
    void testMethodWithPlainText() {
        String expected = "Hello mundo!";

        assertEquals(expected, mockService.methodWithPlainResponse());
    }

    @Test
    void testMethodWithoutSourceLanguage() {
        String expected = "Ito ay isang halimbawa ng pagsasalin na walang pinagmulang wika!";

        assertEquals(expected, mockService.methodWithoutSourceLanguage());
    }

    @Test
    void testCustomObjectWithPlainText() {
        String expected = "Maligayang pagdating sa kagubatan!";
        MockPayload message = new MockPayload();
        message.setBody(new MockPayload.Body("Welcome to the jungle!", null));

        assertEquals(expected, mockService.methodWithTextWithinCustomObject().getBody().getContent());
    }

    @Test
    void testMethodWithDocument() {
        File translatedFile = mockService.methodWithDocumentResponse();

        assertTrue(translatedFile.getName().startsWith("isalin"));
        assertTrue(translatedFile.exists());
    }

    @Test
    void testCustomObjectWithDocument() {
        MockPayload msg = mockService.methodWithDocumentWithinCustomObject();

        assertTrue(msg.getBody().getFileContent().getName().startsWith("isalin"));
        assertTrue(msg.getBody().getFileContent().exists());
    }
}