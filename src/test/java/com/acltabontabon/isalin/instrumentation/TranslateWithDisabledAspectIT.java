package com.acltabontabon.isalin.instrumentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.acltabontabon.isalin.IsalinTestBase;
import com.acltabontabon.isalin.mock.MockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

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