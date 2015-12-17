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
        Optional<StackTraceElement> first = Arrays.stream(stackTraceElements)
                .filter(s -> s.getClassName().startsWith(Cuppa.class.getPackage().getName()))
                .findFirst();
        if (first.isPresent()) {
            int index = Arrays.asList(stackTraceElements).indexOf(first.get());
            return Arrays.copyOf(stackTraceElements, index);
        }
        return stackTraceElements;
    }
}
