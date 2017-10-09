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

import org.forgerock.cuppa.functions.Condition;
import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.functions.TestBlockFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.internal.TestContainer;
import org.forgerock.cuppa.model.Option;

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
     * @param description A description of the behaviour that the function will assert.
     * @param function The function to execute.
     */
    public static void it(String description, TestFunction function) {
        TestContainer.INSTANCE.it(description, function);
    }

    /**
     * Registers a pending test.
     *
     * <p>A pending test has no implementation and acts as a reminder to the developer to write the implementation
     * later.</p>
     *
     * @param description A description of the behaviour that this test will assert.
     */
    public static void it(String description) {
        TestContainer.INSTANCE.it(description);
    }

    /**
     * Decorate a test or block of tests with additional options. Options are constructed via factory methods. For
     * example, see {@link Cuppa#tags(String...)}.
     *
     * <p>Multiple options can be either passed as additional arguments or chained using the returned builder.</p>
     *
     * <pre><code>
     * with(tags("slow")).
     * it("takes a long time to start", () -&gt; {
     *   // ...
     * });
     * </code></pre>
     *
     * @param options Options to apply to the test/block.
     * @return An object for building a test or test block with the given options.
     *
     * @see Cuppa#tags(String...)
     */
    public static TestBuilder with(Option<?>... options) {
        return TestContainer.INSTANCE.with(options);
    }

    /**
     * Mark a test or block of tests to be skipped.
     *
     * <p>The test(s) may still be included in test reports, but marked as skipped.</p>
     *
     * <pre><code>
     * skip().it("does something", () -&gt; {
     *   // Will not be run.
     * });
     * </code></pre>
     *
     * @return An object for building the test or test block that will be skipped.
     */
    public static TestBuilder skip() {
        return TestContainer.INSTANCE.skip();
    }

    /**
     * Mark a test or block of tests to be skipped only if the provided {@link Condition} applies.
     *
     * <p>The test(s) may still be included in test reports, but marked as skipped.</p>
     *
     * <pre><code>
     * skip(() -&gt; /* ... *&#47;).it("does something", () -&gt; {
     *   // Will not run if condition evaluates to true.
     * });
     * </code></pre>
     *
     * @param condition The {@link Condition} that needs to apply in order to skip the test or block of tests.
     * @return An object for building the test or test block that will be skipped.
     */
    public static TestBuilder skip(Condition condition) {
        if (condition.applies()) {
            return skip();
        } else {
            return TestContainer.INSTANCE.with();
        }
    }

    /**
     * Mark a test or block of tests as the only tests that should be run.
     *
     * <p>If at least one test is marked as {@code only} then all tests not marked as {@code only} will not be run.</p>
     *
     * <pre><code>
     * only().it("does something", () -&gt; {
     *   // ...
     * });
     * </code></pre>
     *
     * @return An object for building the test or test block that will be run.
     */
    public static TestBuilder only() {
        return TestContainer.INSTANCE.only();
    }

    /**
     * Decorates tests with a set of tags. Tags can be used to group tests together to be included or excluded from a
     * test run.
     *
     * <p>Apply to a test or block of tests by passing the result of this method to
     * {@link Cuppa#with(Option...)}.</p>
     *
     * <pre><code>
     * with(tags("slow")).
     * it("takes a long time to start", () -&gt; {
     *   // ...
     * });
     * </code></pre>
     *
     * @param tags String identifiers that can be used when running Cuppa to include or exclude a test/block.
     * @return An option, which can be passed to {@link Cuppa#with(Option...)}.
     *
     * @see Cuppa#with(Option...)
     */
    public static Option tags(String... tags) {
        return TestContainer.INSTANCE.tags(tags);
    }
}
