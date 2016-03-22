---
title: Tutorial
---

{::options parse_block_html="true" /}

Start by cloning the [example Maven project](https://github.com/cuppa-framework/cuppa-maven-example).

## Our First Test

Cuppa tests are defined at runtime, often in a class's initialiser block.

```java
@Test
public class ListTest {
    {
        describe("List#indexOf", () -> {
            it("returns -1 when the value is not present", () -> {
                List<Integer> list = Arrays.asList(1, 2, 3);
                assertThat(list.indexOf(5)).isEqualTo(-1);
            });
        });
    }
}
```

We use the `@Test` annotation so Cuppa can find our class when running the tests.

We start by calling `describe`, a static method of the `Cuppa` class, to tell Cuppa what we're testing.
Cuppa doesn't care what string you pass to `describe` so you should use something that makes sense to you.
The string is only used when generating reports.

We also pass a lambda-expression, where all the tests related to `List#indexOf` are defined.

<div class="alert alert-info" role="alert">
#### Lambda Expressions

Cuppa makes extensive use of lambda expressions - a feature introduced in Java 8. They're just like anonymous classes
but their syntax is much more compact. If you've never used them before, don't worry, there's not much more you need
to know in order to use Cuppa.
</div>

Finally, we define a test by calling another static method `it`.
We pass it a description of the behaviour we want to test and a lambda expression that asserts that behaviour.

<div class="alert alert-info" role="alert">
#### Why `it`?

The method is called `it` to remind us to describe the *behaviour* we are testing, helping to keep our test names
consistent and readable.
</div>

### Running it

Use Maven to run the test:

```shell
$ mvn test
```

Cuppa will print out a report:

```
  List#indexOf
    ✓ returns -1 when the value is not present


  1 passing
```

## The Next Test

Now we can add another test right beside the first:

```java
@Test
public class ListTest {
    {
        describe("List#indexOf", () -> {
            it("returns -1 when the value is not present", () -> {
                List<Integer> list = Arrays.asList(1, 2, 3);
                assertThat(list.indexOf(5)).isEqualTo(-1);
            });
            it("returns 0-based index of value when the value is present", () -> {
                List<Integer> list = Arrays.asList(1, 2, 3);
                assertThat(list.indexOf(2)).isEqualTo(1);
            });
        });
    }
}
```

Running the tests prints:

```
  List#indexOf
    ✓ returns -1 when the value is not present
    ✓ returns 0-based index of value when the value is present


  2 passing
```

## Further Grouping of Tests

So far we've only written tests for a single method, `indexOf`. If we want to test more methods, it makes sense to nest a second
level of `describe` blocks.

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
                it("returns 0-based index of value when the value is present", () -> {
                    List<Integer> list = Arrays.asList(1, 2, 3);
                    assertThat(list.indexOf(2)).isEqualTo(1);
                });
            });
            describe("#isEmpty", () -> {
                it("returns true when the list is empty", () -> {
                    List<Integer> list = Arrays.asList();
                    assertThat(list.isEmpty()).isTrue();
                });
                it("returns false when the list has a single element", () -> {
                    List<Integer> list = Arrays.asList(1);
                    assertThat(list.isEmpty()).isFalse();
                });
            });
        });
    }
}
```

Running the tests prints:

```
  List
    #indexOf
      ✓ returns -1 when the value is not present
      ✓ returns 0-based index of value when the value is present
    #isEmpty
      ✓ returns true when the list is empty
      ✓ returns false when the list has a single element


  4 passing
```

Structuring the tests like this makes it easy to see which tests relate to which method. Cuppa imposes no constraints
on how you use `describe` blocks to add structure to your tests.

## When

It is often the case when we want to group tests together because they share the same setup. For example,

```java
@Test
public class ListTest {
    {
        describe("List", () -> {
            when("it is empty", () -> {
                describe("#indexOf", () -> {
                    it("returns -1 for all inputs", () -> {
                        List<Integer> list = Arrays.asList();
                        assertThat(list.indexOf(5)).isEqualTo(-1);
                    });
                });
                describe("#isEmpty", () -> {
                    it("returns true", () -> {
                        List<Integer> list = Arrays.asList();
                        assertThat(list.isEmpty()).isTrue();
                    });
                });
            });
        });
    }
}
```

In this example, we have grouped together tests for an empty list.

`when` blocks are very similar to `describe` blocks and can be used interchangeably. The only difference is that Cuppa
adds the word "when" in reports:

```
  List
    when it is empty
      #indexOf
        ✓ returns -1 for all inputs
      #isEmpty
        ✓ returns true


  1 passing
```

Using `when` helps us better convey the structure of the behaviour we're testing.

## Hooks

In the previous example, we created the empty list twice. Let's remove this repetition by using the `beforeEach` method.


```java
@Test
public class ListTest {
    List<Integer> list;

    {
        describe("List", () -> {
            when("it is empty", () -> {
                beforeEach(() -> {
                    list = Arrays.asList();
                });
                describe("#indexOf", () -> {
                    it("returns -1 for all inputs", () -> {
                        assertThat(list.indexOf(5)).isEqualTo(-1);
                    });
                });
                describe("#isEmpty", () -> {
                    it("returns true", () -> {
                        assertThat(list.isEmpty()).isTrue();
                    });
                });
            });
            when("it is not empty", () -> {
                beforeEach(() -> {
                    list = Arrays.asList(1, 2, 3);
                });
                describe("#isEmpty", () -> {
                    it("returns false", () -> {
                        assertThat(list.isEmpty()).isFalse();
                    });
                });
            });
        });
    }
}
```

The lambda expression we pass to `beforeEach` is executed once before each test in the same `describe` or `when` block.
It is also executed for any test that is in a nested `describe` or `when` block.

The available hooks are:

* `before` - Runs once before any tests in the same or nested blocks.
* `beforeEach` - Runs for each test in the same or nested blocks.
* `after` - Runs once after all tests in the same or nested blocks.
* `afterEach` - Runs after each test in the same or nested blocks.

## Well Done

You now know enough to start writing tests. We've explored the basic features of Cuppa, but if you'd like to learn more
then check out some of the guides, starting with [parameterised tests]({{ site.baseurl }}/docs/parameterised-tests.html).
