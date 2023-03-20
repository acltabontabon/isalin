package com.aclt.isalin.instrumentation;

import com.aclt.isalin.IsalinTestBase;
import com.aclt.isalin.mock.MockPayload;
import com.aclt.isalin.mock.MockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testMethodWithJson() {
        String expected = "Maligayang pagdating sa kagubatan!";
        MockPayload message = new MockPayload();
        message.setBody(new MockPayload.Body("Welcome to the jungle!"));

        assertEquals(expected, mockService.methodWithJsonResponse().getBody().getContent());
    }
}