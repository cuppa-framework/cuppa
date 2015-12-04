package org.forgerock.cuppa;

import static org.forgerock.cuppa.TestResults.EMPTY_RESULTS;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.Iterables;

/**
 * Encapsulates the 'describe' and 'when' function blocks and all nested 'describe', 'when' and
 * test ('it') function blocks.
 */
class DescribeBlock {

    private final String description;
    private final Optional<DescribeBlock> parentDescribeBlock;
    private final List<DescribeBlock> describeBlocks = new ArrayList<>();
    private final List<Function> beforeFunctions = new ArrayList<>();
    private final List<Function> afterFunctions = new ArrayList<>();
    private final List<Function> beforeEachFunctions = new ArrayList<>();
    private final List<Function> afterEachFunctions = new ArrayList<>();
    private final List<TestBlock> testBlocks = new ArrayList<>();

    DescribeBlock(String description, Optional<DescribeBlock> parentDescribeBlock) {
        Objects.requireNonNull(description, "Block must have a description");
        Objects.requireNonNull(parentDescribeBlock, "Block must have an optional parent block");
        this.description = description;
        this.parentDescribeBlock = parentDescribeBlock;
    }

    void runTests() {
        describeBlocks.forEach(DescribeBlock::runTests);
        beforeFunctions.forEach(Function::apply);
        testBlocks.forEach(this::runTest);
        afterFunctions.forEach(Function::apply);
    }

    private void runTest(TestBlock testBlock) {
        getBeforeEachFunctionsForTest().forEach(Function::apply);
        testBlock.runTest();
        getAfterEachFunctionsForTest().forEach(Function::apply);
    }

    private Iterable<Function> getBeforeEachFunctionsForTest() {
        if (parentDescribeBlock.isPresent()) {
            return Iterables.concat(parentDescribeBlock.get().getBeforeEachFunctionsForTest(), beforeEachFunctions);
        } else {
            return beforeEachFunctions;
        }
    }

    private Iterable<Function> getAfterEachFunctionsForTest() {
        if (parentDescribeBlock.isPresent()) {
            return Iterables.concat(afterEachFunctions, parentDescribeBlock.get().getAfterEachFunctionsForTest());
        } else {
            return afterEachFunctions;
        }
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

    TestResults getTestResults() {
        TestResults testResults = describeBlocks.stream()
                .map(DescribeBlock::getTestResults)
                .reduce(EMPTY_RESULTS, TestResults::combine);
        return testBlocks.stream()
                .map(TestBlock::getTestResults)
                .reduce(testResults, TestResults::combine);
    }
}
