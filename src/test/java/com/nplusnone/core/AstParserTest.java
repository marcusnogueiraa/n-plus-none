package com.nplusnone.core;

import com.nplusnone.model.Violation;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AstParserTest {

    private static final String TEST_FILE_PATH = "src/test/resources/dummy/InvoiceService.java";
    private static final String EXPECTED_FILE_NAME = "InvoiceService.java";
    private static final int EXPECTED_LINE_NUMBER = 10;

    @Test
    void shouldDetectForEachLoopInServiceClass() throws Exception {
        File testFile = new File(TEST_FILE_PATH);
        
        AstParser parser = new AstParser(List.of("src/test/resources"));

        List<Violation> violations = parser.analyzeFile(testFile);

        assertFalse(violations.isEmpty(), "The tool should have found the loop.");
        assertEquals(1, violations.size(), "You should find exactly 1 violation.");
        
        Violation firstViolation = violations.get(0);
        assertEquals(EXPECTED_FILE_NAME, firstViolation.fileName());
        assertEquals(EXPECTED_LINE_NUMBER, firstViolation.lineNumber());
        assertTrue(firstViolation.message().contains("getLineItems"));
    }
}