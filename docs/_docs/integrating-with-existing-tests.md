---
title: Integrating with Existing Tests
---

{::options parse_block_html="true" /}

## Maven

If you're using Maven, then Surefire/Failsafe will handle running Cuppa along side other test frameworks and aggregating
the results automatically.

## JUnit

If you're not using Maven and are using JUnit, then Cuppa provides a JUnit runner that allows Cuppa tests to be included
when running JUnit.

To use the runner, your project will need to depend on `org.forgerock.cuppa:cuppa-junit`
([download it here]({{ site.github_url }}/releases/latest)).
Simply annotate your class with JUnit's `@RunWith` annotation instead of Cuppa's `@Test` annotation:

```java
@RunWith(CuppaTestRunner.class)
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

## TestNG

Currently, Cuppa offers no way to integrate with TestNG without Maven. If you want to work on this, let us know
your thoughts on [the issue]({{ site.github_url }}/issues/30).