package org.forgerock.cuppa;

import static org.forgerock.cuppa.Reporter.Outcome.ERRORED;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Encapsulates the 'describe' and 'when' function blocks and all nested 'describe', 'when' and
 * test ('it') function blocks.
 */
class DescribeBlock {

    private final String description;
    private final List<DescribeBlock> describeBlocks = new ArrayList<>();
    private final List<Function> beforeFunctions = new ArrayList<>();
    private final List<Function> afterFunctions = new ArrayList<>();
    private final List<Function> beforeEachFunctions = new ArrayList<>();
    private final List<Function> afterEachFunctions = new ArrayList<>();
    private final List<TestBlock> testBlocks = new ArrayList<>();

    DescribeBlock(String description) {
        Objects.requireNonNull(description, "Block must have a description");
        this.description = description;
    }

    void runTests(Reporter reporter) {
        runTests(reporter, Function::apply);
    }

    private void runTests(Reporter reporter, TestWrapper outerTestWrapper) {
        TestWrapper testWrapper = createWrapper(outerTestWrapper, reporter);
        try {
            reporter.describeStart(description);
            try {
                beforeFunctions.forEach(Function::apply);
            } catch (Exception e) {
                reporter.testOutcome("before", ERRORED);
                return;
            }
            testBlocks.stream().forEach((t) -> testWrapper.apply(() -> t.runTest(reporter)));
            describeBlocks.stream().forEach((d) -> d.runTests(reporter, testWrapper));
        } catch (HookException e) {
            if (e.getDescribeBlock() != this) {
                throw e;
            }
        } finally {
            try {
                afterFunctions.forEach(Function::apply);
            } catch (Exception e) {
                reporter.testOutcome("after", ERRORED);
            }
            reporter.describeEnd(description);
        }
    }

    private TestWrapper createWrapper(TestWrapper outerTestRunner, Reporter reporter) {
        return outerTestRunner.compose((f) -> {
            try {
                try {
                    beforeEachFunctions.forEach(Function::apply);
                } catch (Exception e) {
                    reporter.testOutcome("beforeEach", ERRORED);
                    throw new HookException(this, e);
                }
                f.apply();
            } finally {
                try {
                    afterEachFunctions.forEach(Function::apply);
                } catch (Exception e) {
                    reporter.testOutcome("afterEach", ERRORED);
                    throw new HookException(this, e);
                }
            }
        });
    }

    void addDescribeBlock(DescribeBlock describeBlock) {
        describeBlocks.add(describeBlock);
    }

    void addBefore(Optional<String> description, Function function) {
        beforeFunctions.add(function);
    }

    void addAfter(Optional<String> description, Function function) {
        afterFunctions.add(function);
    }

    void addBeforeEach(Optional<String> description, Function function) {
        beforeEachFunctions.add(function);
    }

    void addAfterEach(Optional<String> description, Function function) {
        afterEachFunctions.add(function);
    }

    void addTest(TestBlock testBlock) {
        testBlocks.add(testBlock);
    }

    @FunctionalInterface
    private interface TestWrapper {
        void apply(Function testRunner);

        default TestWrapper compose(TestWrapper after) {
            return (f) -> apply(() -> after.apply(f));
        }
    }
}
