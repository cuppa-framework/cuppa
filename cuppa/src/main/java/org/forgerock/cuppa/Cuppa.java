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

import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.functions.TestBlockFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.internal.TestContainer;
import org.forgerock.cuppa.model.TestBlockBuilder;
import org.forgerock.cuppa.model.TestBuilder;

/**
 * Use the methods of this class to define your tests.
 *
 * <p>Annotate your class with {@link Test}.</p>
 *
 * <pre><code>
 * public class ListTest {
 *   {
 *     describe("List", () -&gt; {
 *       describe("#indexOf", () -&gt; {
 *         it("returns -1 when the value is not present", () -&gt; {
 *           List&lt;Integer&gt; list = Arrays.asList(1, 2, 3);
 *           assertThat(list.indexOf(5)).isEqualTo(-1);
 *         });
 *       });
 *     });
 *   }
 * }
 * </code></pre>
 */
public final class Cuppa {

    private Cuppa() {
    }

    /**
     * Registers a 'describe' block of tests.
     *
     * <p>Use 'describe' blocks to group together tests that describe the same thing. Blocks may be nested within other
     * blocks.</p>
     *
     * @param description The description of the 'describe' block. Used solely for reporting.
     * @param function A function that will define tests and/or test blocks. This function will be executed immediately.
     */
    public static void describe(String description, TestBlockFunction function) {
        TestContainer.INSTANCE.describe(description, function);
    }

    /**
     * Returns a builder for registering a described suite of tests to be run.
     *
     * @param description The description of the 'describe' block.
     * @return The builder for registering a described suite of tests.
     */
    public static TestBlockBuilder describe(String description) {
        return TestContainer.INSTANCE.describe(description);
    }

    /**
     * Registers a 'when' block of tests.
     *
     * <p>Use 'when' blocks to group together tests that share some context. Blocks may be nested within other
     * blocks.</p>
     *
     * @param description The description of the 'when' block. Used solely for reporting.
     * @param function A function that will define tests and/or test blocks. This function will be executed immediately.
     */
    public static void when(String description, TestBlockFunction function) {
        TestContainer.INSTANCE.when(description, function);
    }

    /**
     * Returns a builder for registering a 'when' block to be run.
     *
     * @param description The description of the 'when' block.
     * @return The builder for registering a 'when' block.
     */
    public static TestBlockBuilder when(String description) {
        return TestContainer.INSTANCE.when(description);
    }

    /**
     * Registers the given function to be executed once, before any tests in the current and nested blocks.
     *
     * @param function The 'before' block.
     */
    public static void before(HookFunction function) {
        TestContainer.INSTANCE.before(function);
    }

    /**
     * Registers the given function to be executed once, before any tests in the current and nested blocks.
     *
     * @param description A description of the function. Displayed when the function throws an exception.
     * @param function The function to execute.
     */
    public static void before(String description, HookFunction function) {
        TestContainer.INSTANCE.before(description, function);
    }

    /**
     * Registers the given function to be executed once, after all tests in the current and nested blocks.
     *
     * @param function The function to execute.
     */
    public static void after(HookFunction function) {
        TestContainer.INSTANCE.after(function);
    }

    /**
     * Registers the given function to be executed once, after all tests in the current block.
     *
     * @param description A description of the function. Displayed when the function throws an exception.
     * @param function The function to execute.
     */
    public static void after(String description, HookFunction function) {
        TestContainer.INSTANCE.after(description, function);
    }

    /**
     * Registers the given function to be executed before each test in the current and nested blocks.
     *
     * @param function The function to execute.
     */
    public static void beforeEach(HookFunction function) {
        TestContainer.INSTANCE.beforeEach(function);
    }

    /**
     * Registers the given function to be executed before each test in the current and nested blocks.
     *
     * @param description A description of the function. Displayed when the function throws an exception.
     * @param function The function to execute.
     */
    public static void beforeEach(String description, HookFunction function) {
        TestContainer.INSTANCE.beforeEach(description, function);
    }

    /**
     * Registers the given function to be executed after each test in the current and nested blocks.
     *
     * @param function The function to execute.
     */
    public static void afterEach(HookFunction function) {
        TestContainer.INSTANCE.afterEach(function);
    }

    /**
     * Registers the given function to be executed after each test in the current and nested blocks.
     *
     * @param description A description of the function. Displayed when the function throws an exception.
     * @param function The function to execute.
     */
    public static void afterEach(String description, HookFunction function) {
        TestContainer.INSTANCE.afterEach(description, function);
    }

    /**
     * Registers a test function to be run.
     *
     * @param description The description of the test.
     * @param function The test function.
     */
    public static void it(String description, TestFunction function) {
        TestContainer.INSTANCE.it(description, function);
    }

    /**
     * Returns a builder for registering a test function.
     *
     * <p>To register a pending test do not call the {@link TestBuilder#asserts(TestFunction)}.</p>
     *
     * @param description The description of the test function.
     * @return The builder for registering a test function.
     */
    public static TestBuilder it(String description) {
        return TestContainer.INSTANCE.it(description);
    }
}
