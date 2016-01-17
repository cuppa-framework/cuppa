/*
 * Copyright 2015-2016 ForgeRock AS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.forgerock.cuppa.junit;

import static org.junit.runner.Description.createSuiteDescription;
import static org.junit.runner.Description.createTestDescription;

import org.forgerock.cuppa.CuppaTestProvider;
import org.forgerock.cuppa.model.TestBlock;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * A {@code Runner} for running Cuppa tests and notifying the JUnit framework of test results.
 */
public final class CuppaRunner extends Runner {

    private final Class<?> testClass;

    /**
     * Constructs a new {@code CuppaRunner} that will run tests in the {@code annotatedClass}.
     *
     * @param annotatedClass The class containing the test definitions.
     */
    public CuppaRunner(Class<?> annotatedClass) {
        this.testClass = annotatedClass;
        try {
            testClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Description getDescription() {
        Description description = createSuiteDescription(testClass);
        CuppaTestProvider.getRootTestBlock().testBlocks
                .forEach(b -> description.addChild(getDescriptionOfDescribeBlock(b)));
        return description;
    }

    private Description getDescriptionOfDescribeBlock(TestBlock testBlock) {
        Description description = createSuiteDescription(testBlock.description);
        testBlock.testBlocks
                .forEach(b -> description.addChild(getDescriptionOfDescribeBlock(b)));
        testBlock.tests.forEach(b -> description.addChild(createTestDescription(testClass, b.description)));
        return description;
    }

    @Override
    public void run(RunNotifier notifier) {
        CuppaTestProvider.runTests(new ReportJUnitAdapter(testClass, notifier));
    }
}
