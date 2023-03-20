package com.aclt.isalin.instrumentation;

import com.aclt.isalin.Application;
import com.aclt.isalin.mock.MockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class)
@TestPropertySource(locations = "classpath:isalin-disabled.properties")
@EnabledIf(expression = "#{environment['run.itest'] == 'true'}", loadContext = true)
public class TranslateWithDisabledAspectIT {

    @Autowired
    private MockService mockService;

    @Test
    void testMethodWithPlainText() {
        String expected = "Hello world!";

        assertEquals(expected, mockService.methodWithPlainResponse());
    }
}