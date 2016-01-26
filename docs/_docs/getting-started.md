---
title: Getting Started
---

{::options parse_block_html="true" /}

## Installation

### Maven

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
        <version>2.19.1</version>
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

### Gradle

Add a test dependency for Cuppa in your project's build file:

```groovy
dependencies {
    testCompile 'org.forgerock.cuppa:cuppa:{{ site.cuppa_version }}'
}
```

### Plain Old Jar File

Alternatively, you can download binaries for the [latest release]({{ site.github_url }}/releases/latest).

<div class="alert alert-info" role="alert">
#### Already got tests?

If your project already contains tests that were written with another testing framework, there are
[several methods]({{ site.baseurl }}/docs/integrating-with-existing-tests) to integrate Cuppa.
</div>

## Next Steps

Check out [the tutorial]({{ site.baseurl }}/docs/tutorial.html) to learn how to write tests.