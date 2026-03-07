package com.nplusnone;

import com.nplusnone.core.AstParser;
import com.nplusnone.model.Violation;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY)
public class NPlusNoneCheckMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting n-plus-none static analysis...");

        List<Violation> allViolations = new ArrayList<>();
        AtomicInteger filesScanned = new AtomicInteger(0);

        List<String> sourceRoots = project.getCompileSourceRoots();
        AstParser parser = new AstParser(sourceRoots);

        for (String sourceRoot : sourceRoots) {
            Path rootPath = Paths.get(sourceRoot);
            
            // Avoids breaking the build if the source folder doesn't exist
            if (!Files.exists(rootPath)) {
                continue; 
            }

            try (Stream<Path> paths = Files.walk(rootPath)) {
                paths.filter(Files::isRegularFile)
                     .filter(path -> path.toString().endsWith(".java"))
                     .forEach(path -> {
                         try {
                             filesScanned.incrementAndGet();
                             File javaFile = path.toFile();
                             List<Violation> fileViolations = parser.analyzeFile(javaFile);
                             allViolations.addAll(fileViolations);
                         } catch (Exception e) {
                             // Changed to warn so a single unparseable file doesn't silently kill the whole process
                             getLog().warn("Failed to analyze file: " + path.getFileName(), e);
                         }
                     });
            } catch (Exception e) {
                throw new MojoExecutionException("Error scanning source directory: " + sourceRoot, e);
            }
        }

        getLog().info(String.format("Successfully scanned %d Java file(s).", filesScanned.get()));

        if (!allViolations.isEmpty()) {
            printViolationsReport(allViolations);
            // Fails the build cleanly
            throw new MojoFailureException(String.format("Build failed! Found %d possible N+1 query violation(s).", allViolations.size()));
        } else {
            getLog().info("No static N+1 violations detected. Your loops are clean!");
        }
    }

    private void printViolationsReport(List<Violation> violations) {
        getLog().error("");
        getLog().error("------------------------------------------------------------------------");
        getLog().error("N-PLUS-NONE: VIOLATIONS DETECTED");
        getLog().error("------------------------------------------------------------------------");
        
        for (Violation v : violations) {
            getLog().error(String.format(" -> %s:[Line %d] %s", v.fileName(), v.lineNumber(), v.message()));
        }
        
        getLog().error("------------------------------------------------------------------------");
        getLog().error("");
    }
}