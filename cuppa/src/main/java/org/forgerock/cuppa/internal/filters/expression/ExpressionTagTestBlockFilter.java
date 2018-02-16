/*
 * Copyright 2018 ForgeRock AS.
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

package org.forgerock.cuppa.internal.filters.expression;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.forgerock.cuppa.model.Options;
import org.forgerock.cuppa.model.Tags;
import org.forgerock.cuppa.model.TagsOption;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;


/**
 * Filter the tests according to the expression tag.
 * It uses a set of conditions to decide if it should run a test.
 *
 * @see Condition
 */
public final class ExpressionTagTestBlockFilter implements Function<TestBlock, TestBlock> {
    private final Tags runTags;
    private final Condition condition;

    /**
     * Creates a new filter.
     *
     * @param runTags runTags
     */
    public ExpressionTagTestBlockFilter(Tags runTags) {
        this.runTags = runTags;
        this.condition = ExpressionParser.parse(runTags.expressionTags);
    }

    @Override
    public TestBlock apply(TestBlock testBlock) {
        if (runTags.expressionTags.isEmpty()) {
            return testBlock;
        }

        return filterTests(testBlock, Collections.emptySet());
    }

    private TestBlock filterTests(TestBlock testBlock, Set<String> parentBlockTags) {
        Set<String> blockTags = union(getTags(testBlock.options), parentBlockTags);
        List<TestBlock> testBlocks = testBlock.testBlocks.stream()
                .map(b -> filterTests(b, blockTags))
                .collect(Collectors.toList());
        List<Test> tests = testBlock.tests.stream()
                .filter(t -> condition.shouldRun(union(getTags(t.options), blockTags)))
                .collect(Collectors.toList());
        return testBlock.toBuilder()
                .setTestBlocks(testBlocks)
                .setTests(tests)
                .build();
    }

    private Set<String> getTags(Options options) {
        return options.get(TagsOption.class).orElse(Collections.emptySet());
    }

    private <T> Set<T> union(Set<T> a, Set<T> b) {
        Set<T> union = new HashSet<>(a);
        union.addAll(b);
        return union;
    }
}
