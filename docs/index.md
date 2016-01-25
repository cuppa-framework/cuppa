---
layout: default
title: A test framework for Java 8
---

<div class="jumbotron text-center">
    <div class="container">
        <h1>Cuppa</h1>
        <p>A test framework for Java 8</p>
        <a href="{{ site.baseurl }}/docs/getting-started" class="btn btn-lg btn-primary">Get Started</a>
    </div>
</div>
<div class="container">
    <div class="row">
        <div class="col-md-4">
            <h3>Be descriptive</h3>
            <p>Use strings – not identifiers – to clearly describe the behaviour you are testing. Test reports
            produced by Cuppa read like good documentation.</p>
        </div>
        <div class="col-md-4">
            <h3>Group tests together</h3>
            <p>Create structure in your test files to reduce repetition and improve readability. Groups of tests can
            share setup and teardown steps.</p>
        </div>
        <div class="col-md-4">
            <h3>Define tests at runtime</h3>
            <p>Cuppa makes it trivial to write parameterised tests. It's as simple as a <code>for</code> loop.
            Cuppa is also easy to extend - there's no need to write annotations.</p>
        </div>
    </div>
</div>
<hr class="mini-hr">
{::options parse_block_html="true" /}
<div class="container">

### A simple test

The syntax for writing tests in Cuppa can seem surprising at first, especially if you're used to writing tests in JUnit
or TestNG.

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

</div>
{::options parse_block_html="false" /}
<hr class="mini-hr">
<div class="text-center">
    <a href="{{ site.baseurl }}/docs/getting-started" class="btn btn-primary">Get Started</a>
</div>