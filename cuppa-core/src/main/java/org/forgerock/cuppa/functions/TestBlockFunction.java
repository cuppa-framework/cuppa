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

import org.forgerock.cuppa.Cuppa;

/**
 * Implement this interface to define tests within a describe or when block.
 *
 * <p>This is a functional interface whose functional method is {@link #apply()}.
 */
@FunctionalInterface
public interface TestBlockFunction {

    /**
     * Defines a set of tests by calling {@link Cuppa#it(String, TestFunction)} and/or defines
     * nested blocks by calling {@link Cuppa#describe(String, TestBlockFunction)} or
     * {@link Cuppa#when(String, TestBlockFunction)}.
     */
    void apply();
}
