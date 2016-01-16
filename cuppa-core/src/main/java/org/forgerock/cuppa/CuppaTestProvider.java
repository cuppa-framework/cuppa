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

package org.forgerock.cuppa;

import java.util.Arrays;
import java.util.Optional;

import org.forgerock.cuppa.internal.TestContainer;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;

/**
 * This class allows integrations to control Cuppa and provides access to its model.
 */
public final class CuppaTestProvider {

    private CuppaTestProvider() {
    }

    /**
     * Runs all the tests that have been loaded into the test framework.
     *
     * @param reporter A reporter to apprise of test outcomes.
     */
    public static void runTests(Reporter reporter) {
        TestContainer.INSTANCE.runTests(reporter);
    }

    /**
     * Returns the test block that contains all user-defined tests and test blocks.
     *
     * @return The root test block.
     */
    public static TestBlock getRootTestBlock() {
        return TestContainer.INSTANCE.getRootTestBlock();
    }

    /**
     * Modify a {@link Throwable}'s stacktrace by removing any stack elements that are not relevant
     * to a test. If the {@link Throwable} has a cause, it will also be modified. The modification
     * is applied to all transitive causes.
     *
     * @param throwable a throwable to modify.
     */
    public static void filterStackTrace(Throwable throwable) {
        throwable.setStackTrace(filterStackTrace(throwable.getStackTrace()));
        if (throwable.getCause() != null) {
            filterStackTrace(throwable.getCause());
        }
    }

    private static StackTraceElement[] filterStackTrace(StackTraceElement[] stackTraceElements) {
        StackTraceElement[] newStackTraceElements = getStackTraceUpToCuppaElements(stackTraceElements);
        if (newStackTraceElements.length > 0
                && isStackTraceElementUseless(newStackTraceElements[newStackTraceElements.length - 1])) {
            newStackTraceElements = Arrays.copyOf(newStackTraceElements, newStackTraceElements.length - 1);
        }
        if (newStackTraceElements.length > 0
                && isStackTraceElementForLambda(newStackTraceElements[newStackTraceElements.length - 1])) {
            StackTraceElement oldElement = newStackTraceElements[newStackTraceElements.length - 1];
            newStackTraceElements[newStackTraceElements.length - 1] =
                    new StackTraceElement(oldElement.getClassName(), "<cuppa test>", oldElement.getFileName(),
                            oldElement.getLineNumber());
        }
        return newStackTraceElements;
    }

    private static StackTraceElement[] getStackTraceUpToCuppaElements(StackTraceElement[] stackTraceElements) {
        Optional<StackTraceElement> first = Arrays.stream(stackTraceElements)
                .filter(s -> s.getClassName().startsWith(Cuppa.class.getPackage().getName()))
                .findFirst();
        if (first.isPresent()) {
            int index = Arrays.asList(stackTraceElements).indexOf(first.get());
            return Arrays.copyOf(stackTraceElements, index);
        }
        return stackTraceElements;
    }

    private static boolean isStackTraceElementUseless(StackTraceElement element) {
        return element.getFileName() == null;
    }

    private static boolean isStackTraceElementForLambda(StackTraceElement element) {
        return element.getMethodName().startsWith("lambda$");
    }

    /**
     * Sets the class from which tests are being loaded.
     *
     * @param testClass The test class.
     */
    public static void setTestClass(Class testClass) {
        TestContainer.INSTANCE.setTestClass(testClass);
    }
}
