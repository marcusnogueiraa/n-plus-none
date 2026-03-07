package com.nplusnone.core;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.nplusnone.model.Violation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AstParser {

    private static final List<String> SAFE_METHODS = Arrays.asList(
        "getId", "getClass", "getName", "getCreatedAt", "getUpdatedAt", "hashCode"
    );

    public AstParser(List<String> sourceRoots) {
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        
        for (String root : sourceRoots) {
            typeSolver.add(new JavaParserTypeSolver(new File(root)));
        }

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        
        ParserConfiguration config = new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17)
                .setSymbolResolver(symbolSolver);
                
        StaticJavaParser.setConfiguration(config);
    }

    public List<Violation> analyzeFile(File file) throws FileNotFoundException {
        List<Violation> violations = new ArrayList<>();
        CompilationUnit cu = StaticJavaParser.parse(file);

        cu.findAll(ForEachStmt.class).forEach(forEachStmt -> {
            forEachStmt.getBody().findAll(MethodCallExpr.class).forEach(methodCall -> {
                String methodName = methodCall.getNameAsString();
                
                if (methodName.startsWith("get") && !SAFE_METHODS.contains(methodName)) {
                    
                    methodCall.getScope().ifPresent(scope -> {
                        try {
                            ResolvedType resolvedType = scope.calculateResolvedType();
                            
                            if (resolvedType.isReferenceType()) {
                                resolvedType.asReferenceType().getTypeDeclaration().ifPresent(typeDecl -> {
                                    
                                    Optional<ClassOrInterfaceDeclaration> astClass = typeDecl.toAst(ClassOrInterfaceDeclaration.class);
                                    
                                    if (astClass.isPresent() && isEntity(astClass.get())) {
                                        int line = methodCall.getRange().map(r -> r.begin.line).orElse(-1);
                                        String message = String.format("N+1 detected on Entity! Call '%s' inside loop.", methodCall.toString());
                                        violations.add(new Violation(file.getName(), line, message));
                                    }
                                });
                            }
                        } catch (Exception e) {
                        }
                    });
                }
            });
        });

        return violations;
    }

    private boolean isEntity(ClassOrInterfaceDeclaration classDecl) {
        return classDecl.isAnnotationPresent("Entity") || 
               classDecl.isAnnotationPresent("Table") ||
               classDecl.isAnnotationPresent("jakarta.persistence.Entity") ||
               classDecl.isAnnotationPresent("javax.persistence.Entity");
    }
}