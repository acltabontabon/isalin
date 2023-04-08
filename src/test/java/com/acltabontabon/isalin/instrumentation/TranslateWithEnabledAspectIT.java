package com.acltabontabon.isalin.instrumentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acltabontabon.isalin.IsalinTestBase;
import com.acltabontabon.isalin.mock.MockPayload;
import com.acltabontabon.isalin.mock.MockService;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = "classpath:isalin-enabled.properties")
public class TranslateWithEnabledAspectIT extends IsalinTestBase {

    @Autowired
    private MockService mockService;

    @Test
    void testMethodWithNullResponse() {
        assertNull(mockService.methodWithNullResponse());
    }

    @Test
    void testMethodWithBrokenFileRefResponse() {
        assertFalse(mockService.methodWithNonExistentFileResponse().exists());
    }

    @Test
    void testMethodWithPlainText() {
        String expected = "Hello mundo!";

        assertEquals(expected, mockService.methodWithPlainResponse());
    }

    @Test
    void testMethodWithListOfPlainText() {
        List<String> expected = List.of("Hello mundo!", "Kamusta ka?");

        assertEquals(expected, mockService.methodWithListOfTextResponse());
    }

    @Test
    void testCustomObjectWithListOfPlainText() {
        List<String> expected = List.of("Hello mundo!", "Kamusta ka?");

        assertEquals(expected, mockService.methodWithListOfTextWithinCustomObject().getContents());
    }

    @Test
    void testMethodWithoutSourceLanguage() {
        String expected = "Ito ay isang halimbawa ng pagsasalin na walang pinagmulang wika!";

        assertEquals(expected, mockService.methodWithoutSourceLanguage());
    }

    @Test
    void testCustomObjectWithPlainText() {
        String expected = "Maligayang pagdating sa kagubatan!";

        assertEquals(expected, mockService.methodWithTextWithinCustomObject().getBody().getContent());
    }

    @Test
    void testMethodWithDocument() {
        File translatedFile = mockService.methodWithDocumentResponse();

        assertTrue(translatedFile.getName().startsWith("isalin"));
        assertTrue(translatedFile.exists());
    }

    @Test
    void testMethodWithListOfDocuments() {
        List<File> translatedFiles = mockService.methodWithListOfDocumentResponse();

        assertEquals(2, translatedFiles.size());
        for (File translatedFile: translatedFiles) {
            assertTrue(translatedFile.getName().startsWith("isalin"));
            assertTrue(translatedFile.exists());
        }
    }

    @Test
    void testCustomObjectWithDocument() {
        MockPayload msg = mockService.methodWithDocumentWithinCustomObject();

        assertTrue(msg.getBody().getFileContent().getName().startsWith("isalin"));
        assertTrue(msg.getBody().getFileContent().exists());
    }

    @Test
    void testCustomObjectWithListOfDocuments() {
        MockPayload msg = mockService.methodWithListOfDocumentsWithinCustomObject();

        assertEquals(2, msg.getFileContents().size());
        for (File translatedFile: msg.getFileContents()) {
            assertTrue(translatedFile.getName().startsWith("isalin"));
            assertTrue(translatedFile.exists());
        }
    }
}