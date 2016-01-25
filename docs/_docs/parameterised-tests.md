---
title: Parameterised Tests
---

Parameterised tests are easy in Cuppa. In fact, if you've ever used a `for` loop before, then you already know how!

```java
for (int x : inputs) {
    it("returns -1 for " + x, () -> {
        // ...
    });
}
```

You can even turn tests on and off based on conditions.

```java
if (inProductionEnvironment()) {
    it("works in production", () -> {
        // ...
    });
}
```

Magic!