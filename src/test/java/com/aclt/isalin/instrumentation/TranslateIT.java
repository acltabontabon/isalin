package com.aclt.isalin.instrumentation;

import com.aclt.isalin.Application;
import com.aclt.isalin.mock.MockPayload;
import com.aclt.isalin.mock.MockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@EnabledIf(expression = "#{environment['run.itest'] == 'true'}", loadContext = true)
public class TranslateIT {

    @Autowired
    private MockService mockService;

    @Test
    void testMethodWithPlainText() {
        String expected = "Hello mundo!";

        assertEquals(expected, mockService.methodWithPlainResponse());
    }

    @Test
    void testMethodWithoutSourceLanguage() {
        String expected = "Hello mundo!";

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