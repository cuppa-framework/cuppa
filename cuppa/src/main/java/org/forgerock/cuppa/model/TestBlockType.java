/*
 * Copyright 2016 ForgeRock AS.
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

/**
 * Models the type of a test block.
 *
 * @see TestBlock
 */
public enum TestBlockType {
    /**
     * Root test block, which contains all other test blocks and tests. Root test blocks have no description.
     */
    ROOT,

    /**
     * A 'describe' test block. These are typically created by calling
     * {@link org.forgerock.cuppa.Cuppa#describe(String, TestBlockFunction)}.
     */
    DESCRIBE,

    /**
     * A 'when' test block. These are typically created by calling
     * {@link org.forgerock.cuppa.Cuppa#when(String, TestBlockFunction)}.
     */
    WHEN
}
