# Cuppa [![Build Status](https://travis-ci.org/phillcunnington/cuppa.svg?branch=master)](https://travis-ci.org/phillcunnington/cuppa)

Cuppa is a test framework for Java 8. It makes writing tests productive and fun.

 * **Describe behaviour with strings.** Use the full flexibility of strings to describe the behaviour you are testing.
 * **Group tests together to aid readability.** Create structure in your test files that better reflects the behaviour
 you are testing.
 * **Dynamically define tests at runtime.** In Cuppa, tests are defined at runtime, so it's easy to define tests
 dynamically.

### Example

```java
@Test
public class ListTest {
    List<Integer> list = Arrays.asList(1, 2, 3);

    {
        describe("List#indexOf", () -> {
            when("the value is not present", () -> {
                it("returns -1", () -> {
                    assertThat(list.indexOf(5)).isEqualTo(-1);
                    assertThat(list.indexOf(0)).isEqualTo(-1);
                });
            });
        });
    }
}
```

### Getting Started

Cuppa provides several ways to get started writing tests.

#### Maven

Add the dependency:

```xml
<dependency>
    <groupId>org.forgerock.cuppa</groupId>
    <artifactId>cuppa</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

#### Gradle

Add the dependency:

```groovy
dependencies {
    testCompile 'org.forgerock.cuppa:cuppa:1.0.0'
}
```

#### JUnit

If you already have a suite of JUnit tests, add
`cuppa-junit` to the classpath and then annotate your test class with `@RunWith(CuppaRunner.class)`. Cuppa tests cannot
be mixed with JUnit tests within a single class.

### Status

Cuppa is still in active development and hasn't reached a stable state yet.

### License

Cuppa is licensed under an [Apache 2.0 license](./LICENSE). The documentation is licensed under a
[Creative Commons license](./LICENSE-docs).

Cuppa is inspired by the wonderful <a href="https://mochajs.org">Mocha</a>, a testing framework for JavaScript.
