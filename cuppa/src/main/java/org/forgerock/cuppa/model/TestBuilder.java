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

package org.forgerock.cuppa.model;

import org.forgerock.cuppa.functions.TestFunction;

/**
 * A builder for registering a test function.
 *
 * <p>To register a pending test do not call the {@link #asserts(TestFunction)}.</p>
 */
public interface TestBuilder {

    /**
     * Specifies a set of tags to apply to the test function.
     *
     * @param tag The tag to apply.
     * @param tags Subsequent tags to apply.
     * @return This builder instance.
     */
    TestBuilder withTags(String tag, String... tags);

    /**
     * The test function that will be run to assert behaviour.
     *
     * @param function The test function.
     */
    void asserts(TestFunction function);
}
