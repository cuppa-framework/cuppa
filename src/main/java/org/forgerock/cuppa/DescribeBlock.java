package org.forgerock.cuppa;

import static org.forgerock.cuppa.TestResults.EMPTY_RESULTS;
import static org.forgerock.cuppa.TestResults.ERROR_RESULT;

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

    TestResults runTests() {
        return runTests(TestRunner::apply);
    }

    private TestResults runTests(TestWrapper outerTestWrapper) {
        try {
            TestWrapper testWrapper = createWrapper(outerTestWrapper);
            beforeFunctions.forEach(Function::apply);
            TestResults testResults = testBlocks.stream()
                    .map((t) -> testWrapper.apply(t::runTest))
                    .reduce(EMPTY_RESULTS, TestResults::combine);
            return describeBlocks.stream()
                    .map((d) -> d.runTests(testWrapper))
                    .reduce(testResults, TestResults::combine);
        } catch (BeforeEachException e) {
            if (e.getDescribeBlock() == this) {
                return ERROR_RESULT;
            } else {
                throw e;
            }
        } catch (Exception e) {
            return ERROR_RESULT;
        } finally {
            try {
                afterFunctions.forEach(Function::apply);
            } catch (Exception e) {
                return ERROR_RESULT;
            }
        }
    }

    private TestWrapper createWrapper(TestWrapper outerTestRunner) {
        return outerTestRunner.compose((f) -> {
            try {
                beforeEachFunctions.forEach(Function::apply);
                return f.apply();
            } catch (BeforeEachException e) {
                throw e;
            } catch (Exception e) {
                throw new BeforeEachException(this, e);
            } finally {
                try {
                    afterEachFunctions.forEach(Function::apply);
                } catch (Exception e) {
                    throw new BeforeEachException(this, e);
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
        TestResults apply(TestRunner testRunner);

        default TestWrapper compose(TestWrapper after) {
            return (f) -> apply(() -> after.apply(f));
        }
    }

    @FunctionalInterface
    private interface TestRunner {
        TestResults apply();
    }
}
