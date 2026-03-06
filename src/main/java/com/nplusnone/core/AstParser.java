package com.nplusnone.core;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.nplusnone.model.Violation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class AstParser {

    public List<Violation> analyzeFile(File file) throws FileNotFoundException {
        List<Violation> violations = new ArrayList<>();
        
        CompilationUnit compUnit = StaticJavaParser.parse(file);

        compUnit.findAll(ForEachStmt.class).forEach(forEachStmt -> {
            
            int line = forEachStmt.getRange().map(r -> r.begin.line).orElse(-1);
            
            violations.add(new Violation(file.getName(), line, "Loop 'for-each' detected by Tool. Consider refactoring to avoid potential performance issues with lazy loading."));
        });

        return violations;
    }
}