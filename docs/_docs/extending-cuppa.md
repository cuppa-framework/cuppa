---
title: Extending Cuppa
---

Cuppa can be configured by implementing the `ConfigurationProvider` interface:

```java
package com.example;

import org.forgerock.cuppa.ConfigurationProvider;

public final class MyConfigurationProvider implements ConfigurationProvider {
    @Override
    public void configure(Configuration configuration) {
        // call methods on configuration
    }
}
```

Cuppa uses a [ServiceLoader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) to find the
configuration provider.
Hence, you need to provide a configuration file called `META-INF/services/org.forgerock.cuppa.ConfigurationProvider`
that contains the fully-qualified class name of your class:

```
com.example.MyConfigurationProvider
```

Ensure that this file is on the classpath when running Cuppa.

The configuration object allows you control:

* How test classes are instantiated.
* Transform the tests before they are run.

### Test Class Instantiation

By default, Cuppa will simply create an instance of the test class using the default no-arg constructor.
If you would like to use an inversion of control container, you will need to use the injector's mechanism for
instantiating classes.
See the [Guice integration guide]({{ site.baseurl }}/docs/guice-integration) as an example. 

### Test Transformation

Cuppa runs in two phases:

* Instantiates the test classes to execute all test definitions, resulting in a `TestBlock` that represents the root
  node in a tree containing all the tests.
* Recursively iterates over the test tree executing the tests.

Test transformation happen between the first and second phase, offering a very general mechanism to control
what tests will be run.
Your test transformation function will be passed the root test block and returns a new root test block.
The returned test block will be used as input for the second phase.
Your transform can remove or alter existing tests or add new tests.

Note that the test tree is immutable so your transform will need to create new instances of the
[model objects]({{ site.baseurl }}/javadoc/cuppa/index.html?org/forgerock/cuppa/model/package-summary.html) if it
wants to modify them.

Cuppa itself uses this mechanism to implement filtering tests that have tags.

### Test Metadata

Cuppa's test model was designed with extensibility in mind.
Each [`Test`]({{ site.baseurl }}/javadoc/cuppa/index.html?org/forgerock/cuppa/model/Test.html) and
[`TestBlock`]({{ site.baseurl }}/javadoc/cuppa/index.html?org/forgerock/cuppa/model/TestBlock.html) has a
[`Options`]({{ site.baseurl }}/javadoc/cuppa/index.html?org/forgerock/cuppa/model/Options.html) object, which is a
type-safe store for metadata.
If you would like to decorate tests or test blocks with additional metadata, create a subclass of
[`Option`]({{ site.baseurl }}/javadoc/cuppa/index.html?org/forgerock/cuppa/model/Option.html) to hold your metadata and
a static factory method.

### Example

Let's look at how Cuppa implements tags as an example. There is an `Option` subclass,
[`TagsOption`]({{ site.baseurl }}/javadoc/cuppa/index.html?org/forgerock/cuppa/model/TagsOption.html), which stores a
set of tags.
There is a static factory method, `tags`, which creates instances of `TagsOption`.

There is a test transform,
[`TagTestBlockFilter`]({{ site.baseurl }}/javadoc/cuppa/index.html?org/forgerock/cuppa/internal/TagTestBlockFilter.html),
which filters out any tests that do not have a matching `TagsOption`.