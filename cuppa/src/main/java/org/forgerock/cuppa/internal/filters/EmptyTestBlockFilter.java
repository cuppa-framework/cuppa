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

package org.forgerock.cuppa.internal.filters;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.forgerock.cuppa.model.TestBlock;

/**
 * Filter out test blocks that do not contain any tests.
 */
public final class EmptyTestBlockFilter implements Function<TestBlock, TestBlock> {
    @Override
    public TestBlock apply(TestBlock testBlock) {
        List<TestBlock> testBlocks = testBlock.testBlocks.stream()
                .map(this)
                .filter(b -> !isEmpty(b))
                .collect(Collectors.toList());
        return new TestBlock(testBlock.type, testBlock.behaviour, testBlock.testClass, testBlock.description,
                testBlocks, testBlock.hooks, testBlock.tests, testBlock.options);
    }

    private boolean isEmpty(TestBlock testBlock) {
        return testBlock.testBlocks.isEmpty() && testBlock.tests.isEmpty();
    }
}
