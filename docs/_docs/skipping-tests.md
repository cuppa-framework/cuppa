---
title: Skipping Tests
---

## Skipping a Single Test

During development, you may want to temporarily turn off a test. Rather than commenting it out, we recommend using the 
following syntax to mark the test as skipped.

```java
skip.it("returns -1", () -> {
    // ...
);
```

Skipped tests will be reported to remind you to restore the test before committing the code.

You can also skip entire `describe` or `when` blocks:

```java
skip.when("it is empty", () -> {
    it("returns -1", () -> {
        // ...
    });
});
```

## Running a Single Test

If you'd like to run a single test to debug a problem, just use the following syntax.

```java
only.it("returns -1", () -> {
    // ...
);
```

Likewise, this can be applied to `describe` and `when` blocks.

## Writing a Pending Test

If you'd like to remind yourself to come back and write a test later, just omit the lambda expression to define a
pending test.

```java
it("returns -1");
```

Pending tests will be reported to remind you to come back and fill in the implementations.
