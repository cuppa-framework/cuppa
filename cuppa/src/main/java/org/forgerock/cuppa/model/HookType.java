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
 * Hook type.
 *
 * @see Hook
 */
public enum HookType {
    /**
     * Run once before all tests in the current and nested blocks.
     */
    BEFORE("before"),

    /**
     * Run before each test in the current and nested blocks.
     */
    BEFORE_EACH("beforeEach"),

    /**
     * Run after each test in the current and nested blocks.
     */
    AFTER_EACH("afterEach"),

    /**
     * Run once after all tests in the current and nested blocks.
     */
    AFTER("after");

    /**
     * The method name of the hook.
     */
    public final String description;

    HookType(String description) {
        this.description = description;
    }
}
