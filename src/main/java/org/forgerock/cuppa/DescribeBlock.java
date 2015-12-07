package org.forgerock.cuppa;

import static org.forgerock.cuppa.TestResults.EMPTY_RESULTS;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the 'describe' and 'when' function blocks and all nested 'describe', 'when' and
 * test ('it') function blocks.
 */
class DescribeBlock {

    private final String description;
    private final List<TestBlock> testBlocks = new ArrayList<>();
    private final List<DescribeBlock> describeBlocks = new ArrayList<>();

    DescribeBlock(String description) {
        this.description = description;
    }

    void runTests() {
        describeBlocks.forEach(DescribeBlock::runTests);
        testBlocks.forEach(this::runTest);
    }

    private void runTest(TestBlock testBlock) {
        testBlock.runTest();
    }

    void addDescribeBlock(DescribeBlock describeBlock) {
        describeBlocks.add(describeBlock);
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
