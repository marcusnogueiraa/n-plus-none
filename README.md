# n-plus-none 🚀

**Zero-runtime N+1 query detector for Java/Spring applications.**

The N+1 query issue is a silent performance killer in ORM-based applications (like Hibernate/JPA). While most tools detect this at runtime, **n-plus-none** catches the mistake during compilation.

### 💡 The Problem

In Spring Boot, it's common to have Lazy-loaded relationships. When a developer calls a getter (e.g., `entity.getRelationship()`) inside a loop, Hibernate triggers a new database query for every iteration. This can turn a single request into hundreds of database calls, crashing production environments.

### 🛠️ The Solution

**n-plus-none** is a Maven plugin that statically analyzes your source code's Abstract Syntax Tree (AST). It identifies:

* Standard `for-each` loops and `while` loops.
* Lazy-load method calls (getters) triggered inside these loops.
* Fails the build automatically if violations are found, ensuring "clean" code reaches your CI/CD pipeline.

### 🚀 Usage

Add the following to your `pom.xml`:

```xml
<plugin>
    <groupId>com.nplusnone</groupId>
    <artifactId>n-plus-none</artifactId>
    <version>1.0-SNAPSHOT</version>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>

```

Then run:
`mvn nplusnone:check`

---