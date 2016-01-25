---
title: Getting Started
---

## Installation

### Maven

Add a test dependency for Cuppa in your project's POM:

```xml
<dependency>
    <groupId>org.forgerock.cuppa</groupId>
    <artifactId>cuppa</artifactId>
    <version>{{ site.cuppa_version }}</version>
    <scope>test</scope>
</dependency>
```

### Gradle

Add a test dependency for Cuppa in your project's build file:

```groovy
dependencies {
    testCompile 'org.forgerock.cuppa:cuppa:{{ site.cuppa_version }}'
}
```

### Plain Old Jar File

Alternatively, you can download binaries for the [latest release]({{ site.github_url }}/releases/latest).

{::options parse_block_html="true" /}
<div class="alert alert-info" role="alert">
#### Already got tests?

If your project already contains tests that were written with another testing framework, there are
[several methods]({{ site.baseurl }}/docs/integrating-with-existing-tests) to integrate Cuppa.
</div>

## Next Steps

Check out [the tutorial]({{ site.baseurl }}/docs/tutorial.html) to learn how to write tests.