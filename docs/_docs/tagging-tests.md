---
title: Tagging Tests
---

{::options parse_block_html="true" /}

<div class="alert alert-info" role="alert">
#### Note

Tagging tests only works with Maven Surefire/Failsafe integration at the moment.
</div>

## Tagging a Single Test

Want to run just a sub-set of your tests? No problem. Simply tag your tests with one or more tags.
 
```java
it("returns -1")
        .withTags("smoke")
        .asserts(() -> {
            // ...
        });
```

Running the following command will only run the tests with the matching tag `smoke` and all the remaining tests will be 
ignored.

```bash
mvn -Dtags=smoke test
```

Alternatively you can run all tests which __are not__ tagged with one or more specific tags.

Running the following command will __not__ run the tests with the matching tag `slow` and all the remaining tests will 
be run.

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

## Tagging Pending Tests

Want to add a tag to a pending test? No worries. Simply omit the `.asserts` method call and your done!

```java
it("returns -1")
        .withTags("smoke");
```

## Tagging a Set of Tests

Similarly you can also tag all tests within a `describe` or `when` block:

```java
when("it is empty")
        .eachWithTags("smoke")
        .then(() -> {
            it("returns -1", () -> {
                // ...
            });
        });
``` 
