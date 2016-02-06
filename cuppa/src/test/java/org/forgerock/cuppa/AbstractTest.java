package org.forgerock.cuppa;

import org.forgerock.cuppa.internal.TestContainer;
import org.forgerock.cuppa.model.Tags;
import org.forgerock.cuppa.reporters.Reporter;
import org.testng.annotations.BeforeMethod;

public class AbstractTest {
    @BeforeMethod
    public void setup() {
        TestContainer.INSTANCE.reset();
        TestContainer.INSTANCE.setTestClass(this.getClass());
    }

    public void runTests(Reporter reporter) {
        runTests(reporter, Tags.EMPTY_TAGS);
    }

    public void runTests(Reporter reporter, Tags tags) {
        new Runner(tags).run(TestContainer.INSTANCE.getRootTestBlock(), reporter, new Configuration());
    }
}
