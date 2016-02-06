---
title: Using Guice with Cuppa
---

Cuppa provides a hook to allow you to customise how your test classes are instantiated.
This keeps Cuppa agnostic about which inversion of control container you happen to be using.

To do this, you need to write a class that implements `ConfigurationProvider` and sets the class instantiator:

```java
package com.example;

import com.google.inject.Guice;
import org.forgerock.cuppa.ConfigurationProvider;

public final class MyConfigurationProvider implements ConfigurationProvider {
    @Override
    public void configure(Configuration configuration) {
        // Setup Guice as needed...
        Injector injector = Guice.createInjector(new MyModule());
        configuration.setClassInstantiator(injector::getInstance);
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