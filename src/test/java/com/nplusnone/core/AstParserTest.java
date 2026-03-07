package com.nplusnone.core;

import com.nplusnone.model.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AstParserTest {

    private AstParser parser;

    @BeforeEach
    void setUp() {
        // Initializes the parser with the SymbolSolver pointing to the test folder
        parser = new AstParser(List.of("src/test/resources"));
    }

    @Test
    void shouldCorrectlyDifferentiateTrueAndFalsePositives() throws Exception {
        File testFile = new File("src/test/resources/dummy/ComprehensiveService.java");

        List<Violation> violations = parser.analyzeFile(testFile);

        // The service has 4 loops, but only 2 should be caught!
        assertEquals(2, violations.size(), "The analyzer should have found exactly 2 N+1 violations (ignoring DTOs and basic types).");

        // Checks the first violation: Collection return
        Violation collectionViolation = violations.get(0);
        assertEquals("ComprehensiveService.java", collectionViolation.fileName());
        assertEquals(9, collectionViolation.lineNumber(), "Should have caught line 9 (getLineItems)");
        assertTrue(collectionViolation.message().contains("getLineItems"), "The message must cite the getLineItems method");

        // Checks the second violation: Entity return
        Violation entityViolation = violations.get(1);
        assertEquals("ComprehensiveService.java", entityViolation.fileName());
        assertEquals(15, entityViolation.lineNumber(), "Should have caught line 15 (getCustomer)");
        assertTrue(entityViolation.message().contains("getCustomer"), "The message must cite the getCustomer method");
    }
}