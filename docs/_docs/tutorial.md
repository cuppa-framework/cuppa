---
title: Tutorial
---

## Our First Test

Cuppa tests are defined at runtime. Tests are often written in a class's initialiser block.

```java
@Test
public class ListTest {
    {
        it("returns -1 when the value is not present", () -> {
            List<Integer> list = Arrays.asList(1, 2, 3);
            assertThat(list.indexOf(5)).isEqualTo(-1);
        });
    }
}
```

We define a test by calling `it`, a static method from the `Cuppa` class. We pass it a description of the behaviour we
expect and a lambda expression that asserts the behaviour.

Cuppa makes extensive use of lambda expressions - a feature introduced in Java 8. They're just like anonymous classes
but their syntax is much more compact. If you've never used them before, don't worry, there's not much more you need
to know about them to use Cuppa.

### Why `it`?

The method is called `it` to remind us to describe the *behaviour* we are testing, helping to keep our test names
consistent and readable.

### Running the Test

```shell
$ mvn test
```

This is what Cuppa outputs:

```
  ✓ returns -1 when the value is not present


  1 passing
```

## Grouping Tests

We've explained that our tests describe behaviour, so it makes sense to document what it is that we are describing the
behaviour of.

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

We've wrapped our test in two nested "describe" blocks. The `describe` method allows us to group tests together
that describe the same thing, further aiding readability.

Running Cuppa we see that the test is now nicely nested under the thing it describes:

```
  List
    #indexOf
      ✓ returns -1 when the value is not present


  1 passing
```

## When

It is often the case when we want to group tests together because they share state. For example,

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

```
  List
    when it is empty
      #indexOf
        ✓ returns -1 for all inputs
      #isEmpty
        ✓ returns true


  1 passing
```

## Before and After

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
        });
    }
}
```

The lambda expression we pass to `beforeEach` is executed once before each test in the same `describe` or `when` block.
It is also executed for any test that is in a nested `describe` or `when` block.

## Well Done

We've explored the basic features of Cuppa. You now know enough to start writing tests with Cuppa. If you'd like
to learn more then check out some of the guides, starting with
[parameterised tests]({{ site.baseurl }}/docs/parameterised-tests.html).