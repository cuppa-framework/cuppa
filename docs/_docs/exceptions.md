---
title: Handling Exceptions
---
## Throwing Checked Exceptions

Cuppa makes it easy to test code that may throw checked exceptions (i.e. exceptions that are not subtypes of
`RuntimeException`). Tests may throw checked exceptions without needing to declare anything - it just works.

```java
it("throws a checked exception", () -> {
    throw new Exception();
});
```

## Expected Exceptions

Cuppa doesn't include built-in support for expected exceptions (like JUnit's `@Test(expected=Exception.class)`). You
should use the features of your chosen assertion library instead. For example, AssertJ provides the `assertThatThrownBy`
method:

```java
it("throws an exception", () - {
    assertThatThrownBy(() -> {
        throw new Exception("boom!");
    }).isInstanceOf(Exception.class).hasMessageContaining("boom");
});
```