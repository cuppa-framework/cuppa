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
    SKIP,
    /**
     * Run the test(s) and ignore all other tests not marked as ONLY.
     */
    ONLY;

    /**
     * Combine this behaviour with another behaviour.
     * @param behaviour The other behaviour
     * @return The combined behaviour
     */
    public Behaviour combine(Behaviour behaviour) {
        if (this == SKIP || behaviour == SKIP) {
            return SKIP;
        } else if (this == ONLY || behaviour == ONLY) {
            return ONLY;
        }
        return NORMAL;
    }
}
