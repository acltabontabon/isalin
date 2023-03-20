package com.aclt.isalin.instrumentation;

import com.aclt.isalin.IsalinTestBase;
import com.aclt.isalin.mock.MockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(locations = "classpath:isalin-disabled.properties")
public class TranslateWithDisabledAspectIT extends IsalinTestBase {

    @Autowired
    private MockService mockService;

    @Test
    void testMethodWithPlainText() {
        String expected = "Hello world!";

        assertEquals(expected, mockService.methodWithPlainResponse());
    }
}