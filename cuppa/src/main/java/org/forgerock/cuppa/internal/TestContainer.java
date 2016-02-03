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

package org.forgerock.cuppa.internal;

import static org.forgerock.cuppa.model.Behaviour.NORMAL;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.forgerock.cuppa.CuppaException;
import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.functions.TestBlockFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.model.Behaviour;
import org.forgerock.cuppa.model.Tags;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.model.TestBlockBuilder;
import org.forgerock.cuppa.model.TestBuilder;
import org.forgerock.cuppa.reporters.Reporter;

/**
 * Singleton container for user-defined tests.
 */
public enum TestContainer {
    /**
     * The singleton.
     */
    INSTANCE;

    private InternalTestBlockBuilder rootBuilder;
    private Deque<InternalTestBlockBuilder> stack;
    private boolean runningTests;
    private Class<?> testClass;

    TestContainer() {
        reset();
    }

    /**
     * Registers a described suite of tests to be run.
     *
     * @param description The description of the 'describe' block.
     * @param function The 'describe' block.
     */
    public void describe(String description, TestBlockFunction function) {
        describe(NORMAL, description).then(function);
    }

    /**
     * Returns a builder for registering a described suite of tests to be run.
     *
     * @param description The description of the 'describe' block.
     * @return The builder for registering a described suite of tests.
     */
    public TestBlockBuilder describe(String description) {
        return describe(NORMAL, description);
    }

    /**
     * Returns a builder for registering a described suite of tests to be run.
     *
     * @param behaviour If {@link Behaviour#skip} then this test will be skipped.
     * @param description The description of the 'describe' block.
     * @return The builder for registering a described suite of tests.
     */
    public TestBlockBuilder describe(Behaviour behaviour, String description) {

        assertNotRunningTests("describe");
        InternalTestBlockBuilder testBlockBuilder = new InternalTestBlockBuilder(behaviour, description);
        stack.addLast(testBlockBuilder);
        return new TestBlockBuilderImpl(testBlockBuilder);
    }

    /**
     * Registers a 'when' block to be run.
     *
     * @param description The description of the 'when' block.
     * @param function The 'when' block.
     */
    public void when(String description, TestBlockFunction function) {
        when(NORMAL, description).then(function);
    }

    /**
     * Returns a builder for registering a 'when' block to be run.
     *
     * @param description The description of the 'when' block.
     * @return The builder for registering a 'when' block.
     */
    public TestBlockBuilder when(String description) {
        return when(NORMAL, description);
    }

    /**
     * Returns a builder for registering a 'when' block to be run.
     *
     * @param behaviour If {@link Behaviour#skip} then this test will be skipped.
     * @param description The description of the 'when' block.
     * @return The builder for registering a 'when' block.
     */
    public TestBlockBuilder when(Behaviour behaviour, String description) {
        assertNotRunningTests("when");
        assertNotRootDescribeBlock("when", "when", "describe");
        return describe(behaviour, description);
    }

    /**
     * Registers a 'before' block to be run.
     *
     * @param function The 'before' block.
     */
    public void before(HookFunction function) {
        before(null, function);
    }

    /**
     * Registers a 'before' block to be run.
     *
     * @param description The description of the 'before' block.
     * @param function The 'before' block.
     */
    public void before(String description, HookFunction function) {
        assertNotRunningTests("before");
        assertNotRootDescribeBlock("when", "describe");
        getCurrentDescribeBlock().addBefore(Optional.ofNullable(description), function);
    }

    /**
     * Registers a 'after' block to be run.
     *
     * @param function The 'after' block.
     */
    public void after(HookFunction function) {
        after(null, function);
    }

    /**
     * Registers a 'after' block to be run.
     *
     * @param description The description of the 'after' block.
     * @param function The 'after' block.
     */
    public void after(String description, HookFunction function) {
        assertNotRunningTests("after");
        assertNotRootDescribeBlock("when", "describe");
        getCurrentDescribeBlock().addAfter(Optional.ofNullable(description), function);
    }

    /**
     * Registers a 'beforeEach' block to be run.
     *
     * @param function The 'beforeEach' block.
     */
    public void beforeEach(HookFunction function) {
        beforeEach(null, function);
    }

    /**
     * Registers a 'beforeEach' block to be run.
     *
     * @param description The description of the 'beforeEach' block.
     * @param function The 'beforeEach' block.
     */
    public void beforeEach(String description, HookFunction function) {
        assertNotRunningTests("beforeEach");
        assertNotRootDescribeBlock("when", "describe");
        getCurrentDescribeBlock().addBeforeEach(Optional.ofNullable(description), function);
    }

    /**
     * Registers a 'afterEach' block to be run.
     *
     * @param function The 'afterEach' block.
     */
    public void afterEach(HookFunction function) {
        afterEach(null, function);
    }

    /**
     * Registers a 'afterEach' block to be run.
     *
     * @param description The description of the 'afterEach' block.
     * @param function The 'afterEach' block.
     */
    public void afterEach(String description, HookFunction function) {
        assertNotRunningTests("afterEach");
        assertNotRootDescribeBlock("when", "describe");
        getCurrentDescribeBlock().addAfterEach(Optional.ofNullable(description), function);
    }

    /**
     * Registers a test function to be run.
     *
     * @param description The description of the test function.
     * @param function The test function.
     */
    public void it(String description, TestFunction function) {
        it(NORMAL, description).asserts(function);
    }

    /**
     * Returns a builder for registering a test function to be run.
     *
     * <p>To register a pending test do not call the {@link TestBuilder#asserts(TestFunction)}.</p>
     *
     * @param description The description of the test function.
     * @return The builder for registering a test function.
     */
    public TestBuilder it(String description) {
        return it(NORMAL, description);
    }

    /**
     * Returns a builder for registering a test function to be run.
     *
     * <p>To register a pending test do not call the {@link TestBuilder#asserts(TestFunction)}.</p>
     *
     * @param behaviour If {@link Behaviour#skip} then this test will be skipped.
     * @param description The description of the test function.
     * @return The builder for registering a test function.
     */
    public TestBuilder it(Behaviour behaviour, String description) {
        assertNotRunningTests("it");
        assertNotRootDescribeBlock("it", "when", "describe");
        TestBuilderImpl testBuilder = new TestBuilderImpl(behaviour, description, testClass);
        getCurrentDescribeBlock().addTest(testBuilder);
        return testBuilder;
    }

    /**
     * Sets the class from which tests are being loaded.
     *
     * @param testClass The test class.
     */
    @SuppressFBWarnings(value = "ME_ENUM_FIELD_SETTER",
            justification = "Need to be able to set the class in which the tests are defined when they are registered.")
    public void setTestClass(Class<?> testClass) {
        this.testClass = testClass;
    }

    /**
     * Runs all the tests that have been loaded into the test framework.
     *
     * @param reporter A reporter to apprise of test outcomes.
     * @param tags The set of tags to filter the tests to run by.
     */
    public void runTests(Reporter reporter, Tags tags) {
        if (stack.size() != 1) {
            throw new IllegalStateException("runTests cannot be run from within a 'describe' or 'when'");
        }
        runningTests = true;
        TestBlock rootBlock = rootBuilder.build();
        reporter.start();
        new TestRunner().runTests(rootBlock, rootBlock.hasOnlyTests(), reporter, tags);
        reporter.end();
    }

    /**
     * Returns the test block that contains all user-defined tests and test blocks.
     *
     * @return The root test block.
     */
    public TestBlock getRootTestBlock() {
        return rootBuilder.build();
    }

    /**
     * For test use only.
     *
     * <p>Resets the test framework state.</p>
     */
    public void reset() {
        runningTests = false;
        rootBuilder = new InternalTestBlockBuilder(NORMAL, "");
        stack = new ArrayDeque<>();
        stack.addLast(rootBuilder);
        testClass = null;
    }

    private void assertNotRunningTests(String blockType) {
        if (runningTests) {
            throw new CuppaException(new IllegalStateException("Cannot declare new '" + blockType
                    + "' block whilst running tests"));
        }
    }

    private void assertNotRootDescribeBlock(String blockType, String... allowedBlockTypes) {
        if (getCurrentDescribeBlock().equals(rootBuilder)) {
            throw new CuppaException(new IllegalStateException("A '" + blockType + "' must be nested within a "
                    + String.join(", ", allowedBlockTypes) + " function"));
        }
    }

    private InternalTestBlockBuilder getCurrentDescribeBlock() {
        return stack.getLast();
    }

    private final class TestBlockBuilderImpl implements TestBlockBuilder {

        private final InternalTestBlockBuilder testBlockBuilder;

        private TestBlockBuilderImpl(InternalTestBlockBuilder testBlockBuilder) {
            this.testBlockBuilder = testBlockBuilder;
        }

        @Override
        public TestBlockBuilder eachWithTags(String tag, String... tags) {
            testBlockBuilder.eachWithTags(tag, tags);
            return this;
        }

        @Override
        public void then(TestBlockFunction function) {
            try {
                function.apply();
            } finally {
                stack.removeLast();
                getCurrentDescribeBlock().addTestBlock(testBlockBuilder.build());
            }
        }
    }

}
