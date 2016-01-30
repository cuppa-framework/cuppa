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

import org.forgerock.cuppa.functions.TestBlockFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.internal.TestContainer;

/**
 * Controls the behaviour of a test or collection of tests.
 */
public enum Behaviour {
    /**
     * Run the test(s).
     */
    NORMAL,
    /**
     * Do not run the test(s). The test(s) may still be included in test reports, but marked as skipped.
     */
    skip,
    /**
     * Run the test(s) and ignore all other tests not marked as ONLY.
     */
    only;

    /**
     * Registers a described suite of tests to be run.
     * <p>If {@link Behaviour#skip} then this test will be skipped.</p>
     *
     * @param description The description of the 'describe' block.
     * @param function The 'describe' block.
     */
    public void describe(String description, TestBlockFunction function) {
        TestContainer.INSTANCE.describe(this, description, function);
    }

    /**
     * Registers a 'when' block to be run.
     * <p>If {@link Behaviour#skip} then this test will be skipped.</p>
     *
     * @param description The description of the 'when' block.
     * @param function The 'when' block.
     */
    public void when(String description, TestBlockFunction function) {
        TestContainer.INSTANCE.when(this, description, function);
    }

    /**
     * Registers a test function to be run.
     * <p>If {@link Behaviour#skip} then this test will be skipped.</p>
     *
     * @param description The description of the test function.
     * @param function The test function.
     */
    public void it(String description, TestFunction function) {
        TestContainer.INSTANCE.it(this, description, function);
    }

    /**
     * Registers a pending test function that has yet to be implemented.
     *
     * @param description The description of the test function.
     */
    public void it(String description) {
        TestContainer.INSTANCE.it(description);
    }

    /**
     * Combine this behaviour with another behaviour.
     * @param behaviour The other behaviour
     * @return The combined behaviour
     */
    public Behaviour combine(Behaviour behaviour) {
        if (this == skip || behaviour == skip) {
            return skip;
        } else if (this == only || behaviour == only) {
            return only;
        }
        return NORMAL;
    }
}
