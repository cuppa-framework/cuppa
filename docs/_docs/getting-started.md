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

See the [Maven integration guide]({{ site.baseurl }}/docs/maven-integration) for details on how to get Maven to
run the tests automatically.

### Gradle

We don't yet support Gradle. If you'd like to work on this, let us know on
[the issue](https://github.com/cuppa-framework/cuppa/issues/32).

### Plain Old Jar File

Alternatively, you can download binaries for the [latest release]({{ site.github_url }}/releases/latest).

To run your Cuppa tests, use the Cuppa API:

```java
public class EntryPoint {
    public static void main(String[] args) {
        Runner runner = new Runner();
        TestBlock rootBlock = runner.defineTests(Collections.singletonList(MyTestClass.class));
        runner.run(rootBlock, new DefaultReporter());
    }
}
```

## Next Steps

Check out [the tutorial]({{ site.baseurl }}/docs/tutorial.html) to learn how to write tests.