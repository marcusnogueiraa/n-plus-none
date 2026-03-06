package com.nplusnone.core;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
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
            forEachStmt.getBody().findAll(MethodCallExpr.class).forEach(methodCall -> {
                String methodName = methodCall.getNameAsString();

                if (methodName.startsWith("get")) {
                    int line = methodCall.getRange().map(r -> r.begin.line).orElse(-1);
                    String message = String.format("Lazy loading detected in loop: %s", methodCall.toString());
                    violations.add(new Violation(file.getName(), line, message));
                }
            });
        });

        return violations;
    }
}