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

package org.forgerock.cuppa.functions;

/**
 * Implement this interface to define a test hook. Hooks are used to setup and teardown state for tests.
 *
 * <p>This is a functional interface whose functional method is {@link #apply()}.
 */
@FunctionalInterface
public interface HookFunction {

    /**
     * Defines the behaviour of a test hook.
     *
     * @throws Exception To allow tests and hooks to throw checked exceptions.
     */
    void apply() throws Exception;

    /**
     * Returns a function that does nothing.
     *
     * @return a function that does nothing
     */
    static HookFunction identity() {
        return () -> {
        };
    }
}
