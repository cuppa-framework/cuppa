# [Cuppa](http://cuppa.forgerock.org/) [![Build Status](https://travis-ci.org/cuppa-framework/cuppa.svg?branch=master)](https://travis-ci.org/cuppa-framework/cuppa)

Cuppa is a test framework for Java 8. It makes writing tests productive and fun.

 * **Be descriptive:** Use strings – not identifiers – to clearly describe the behaviour you are testing. Test reports
 produced by Cuppa read like good documentation.
 * **Group tests together:** Create structure in your test files to reduce repetition and improve readability. Groups of
 tests can share setup and teardown steps.
 * **Define tests at runtime:** Cuppa makes it trivial to write parameterised tests. It's as simple as a <code>for</code> loop.
 Cuppa is also easy to extend - there's no need to write annotations.

## Example

```java
@Test
public class ListTest {
    {
        describe("List", () -> {
            describe("#indexOf", () -> {
                it("returns -1 when the value is not present", () -> {
                    List<Integer> list = Arrays.asList(1, 2, 3);
                    assertThat(list.indexOf(5)).isEqualTo(-1);
                });
            });
        });
    }
}
```

Check out [the website](http://cuppa.forgerock.org/) for more examples.

## Getting Started

For full details see [the getting started guide](http://cuppa.forgerock.org/docs/getting-started) on the
website.

#### Maven

Add a test dependency for Cuppa in your project's POM:

```xml
<dependency>
    <groupId>org.forgerock.cuppa</groupId>
    <artifactId>cuppa</artifactId>
    <version>1.0.0</version>
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
                <version>1.0.0</version>
            </dependency>
        </dependencies>
    </plugin>
</plugins>
```

If you want to use Cuppa to write integration tests, you'll need to do the same thing for Failsafe.
Add `cuppa-surefire` as a dependency of the `maven-failsafe-plugin` plugin.

#### Gradle

Add a test dependency for Cuppa in your project's build file:

```groovy
dependencies {
    testCompile 'org.forgerock.cuppa:cuppa:1.0.0'
}
```

### Plain Old Jar File

Alternatively, you can download binaries for the
[latest release](https://github.com/cuppa-framework/cuppa/releases/latest).

## Contribute

### Prerequisites

You'll need JDK 8 installed and available on the path.

### Building

Cuppa is built using [Gradle](https://gradle.org/):

```shell
$ cd cuppa
$ ./gradlew build
```

Run `./gradlew tasks` to see a list of all available tasks.

### Status

Cuppa is still in active development and hasn't reached a stable state yet.

### License

Cuppa is licensed under an [Apache 2.0 license](./LICENSE). The documentation is licensed under a
[Creative Commons license](./LICENSE-docs).

Cuppa is inspired by the wonderful <a href="https://mochajs.org">Mocha</a>, a testing framework for JavaScript.
