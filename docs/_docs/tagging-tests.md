---
title: Tagging Tests
---

{::options parse_block_html="true" /}

<div class="alert alert-info" role="alert">
#### Note

Tagging tests only works with Maven Surefire/Failsafe integration at the moment.
</div>

## Tagging a Single Test

Want to run just a sub-set of your tests? No problem. Simply decorate your tests with one or more tags:
 
```java
with(tags("smoke", "fast")).
it("returns -1", () -> {
    // ...
});
```

Tags can be specified when running Cuppa via Maven using the `tags` property.
For example, to run only the tests with the tag `smoke`:

```bash
mvn -Dtags=smoke test
```

Alternatively you can run all tests which __are not__ tagged with one or more specific tags.
For example, to run all tests except tests tagged with `slow`:

```bash
mvn -DexcludedTags=slow test
```

<div class="alert alert-info" role="alert">
#### Note

When running with a combination of TestNG or JUnit along side Cuppa, you can use the TestNG/JUnit way of 
specifying/excluding groups so that you can run a sub-set of tests across both TestNG/JUnit and Cuppa.

It's important to note that you cannot use both `-Dgroups=` and `-Dtags=` or `-DexcludedGroups=` and `-DexcludedTags=` 
at the same time.
</div>

## Tagging a Block of Tests

Similarly you can tag all tests within a `describe` or `when` block:

```java
with(tags("smoke")).
when("it is empty", () -> {
    it("returns -1", () -> {
        // ...
    });
});
``` 
