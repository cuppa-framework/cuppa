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

import org.forgerock.cuppa.functions.TestBlockFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.model.Option;

/**
 * A builder for defining a test or block of tests.
 *
 * <p>This class helps you decorate a test or block of tests with additional options. Test builders are obtained from
 * various methods of {@link Cuppa}.</p>
 *
 * <pre><code>
 * with(tags("slow")).
 * it("takes a long time to start", () -&gt; {
 *   // ...
 * });
 * </code></pre>
 *
 * @see Cuppa
 */
public interface TestBuilder {

    /**
     * Decorate a test or block of tests with additional options. Options are constructed via factory methods. For
     * example, see {@link Cuppa#tags(String, String...)}.
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
     * @param option An option to apply to the test/block.
     * @param options Additional options to apply to the test/block.
     * @return This, for chaining.
     *
     * @see Cuppa#with(Option, Option...)
     */
    TestBuilder with(Option<?> option, Option<?>... options);

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
     * @return This, for chaining.
     *
     * @see Cuppa#skip()
     */
    TestBuilder skip();

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
     * @return This, for chaining.
     *
     * @see Cuppa#only()
     */
    TestBuilder only();

    /**
     * Registers a described suite of tests to be run.
     *
     * @param description The description of the 'describe' block.
     * @param function The 'describe' block.
     */
    void describe(String description, TestBlockFunction function);

    /**
     * Registers a 'when' block to be run.
     *
     * @param description The description of the 'when' block.
     * @param function The 'when' block.
     */
    void when(String description, TestBlockFunction function);

    /**
     * Registers a test function to be run.
     *
     * @param description The description of the test.
     * @param function The test function.
     */
    void it(String description, TestFunction function);

    /**
     * Registers a pending test.
     *
     * <p>A pending test has no implementation and acts as a reminder to the developer to write the implementation
     * later.</p>
     *
     * @param description The description of the test.
     */
    void it(String description);
}
