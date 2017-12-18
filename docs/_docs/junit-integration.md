---
title: JUnit
---

{::options parse_block_html="true" /}

Cuppa provides a JUnit runner, which is useful when you have existing JUnit tests and a build system that can run them.
If you're using Maven, then we recommend you use Cuppa's
[Maven integration]({{ site.baseurl }}/docs/maven-integration) instead.

To use the runner, your project will need to depend on `org.forgerock.cuppa:cuppa-junit`
([download it here]({{ site.github_url }}/releases/latest)).

This can be easily included with the following Maven dependency:
```xml
<dependency>
    <groupId>org.forgerock.cuppa</groupId>
    <artifactId>cuppa-junit</artifactId>
    <version>{{ site.cuppa_version }}</version>
    <scope>test</scope>
</dependency>
```

Simply annotate your class with JUnit's `@RunWith` annotation instead of Cuppa's `@Test` annotation:

```java
@RunWith(CuppaRunner.class)
public class MyCuppaTests {
    {
        describe("something", () -> {
            // ...
        });
    }
}
```

<div class="alert alert-info" role="alert">
#### Note

You cannot mix JUnit and Cuppa tests in a class.
</div>
