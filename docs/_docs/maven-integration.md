---
title: Maven
---

Add a test dependency for Cuppa in your project's POM:

```xml
<dependency>
    <groupId>org.forgerock.cuppa</groupId>
    <artifactId>cuppa</artifactId>
    <version>{{ site.cuppa_version }}</version>
    <scope>test</scope>
</dependency>
```

To get Surefire (the Maven plugin that runs unit tests) to run Cuppa tests, you'll need some additional configuration:

```xml
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>{{ site.surefire_version }}</version>
        <dependencies>
            <dependency>
                <groupId>org.forgerock.cuppa</groupId>
                <artifactId>cuppa-surefire</artifactId>
                <version>{{ site.cuppa_version }}</version>
            </dependency>
        </dependencies>
    </plugin>
</plugins>
```

If you want to use Cuppa to write integration tests, you'll need to do the same thing for Failsafe.
Add `cuppa-surefire` as a dependency of the `maven-failsafe-plugin` plugin.

## Existing Tests

If your project contains tests written in a different framework, then make sure you also add the dependencies for
the appropriate Surefire providers.
For example, if you have TestNG tests in your project you need to specify *both* the Cuppa provider and TestNG provider.

```xml
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>{{ site.surefire_version }}</version>
        <dependencies>
            <dependency>
                <groupId>org.forgerock.cuppa</groupId>
                <artifactId>cuppa-surefire</artifactId>
                <version>{{ site.cuppa_version }}</version>
            </dependency>
            <dependency>
                <groupId>org.apache.maven.surefire</groupId>
                <artifactId>surefire-testng</artifactId>
                <version>{{ site.surefire_version }}</version>
            </dependency>
        </dependencies>
    </plugin>
</plugins>
```