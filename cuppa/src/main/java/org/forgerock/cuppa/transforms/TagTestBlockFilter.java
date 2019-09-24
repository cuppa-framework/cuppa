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

package org.forgerock.cuppa.transforms;

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
 * Filters the test tree to only include tests that have tags that match the given run tags, excluding any tests
 * that have the given excluded run tags.
 */
public final class TagTestBlockFilter implements Function<TestBlock, TestBlock> {
    private final Tags runTags;

    /**
     * Creates a new filter.
     *
     * @param runTags The tags to include/exclude.
     */
    public TagTestBlockFilter(Tags runTags) {
        this.runTags = runTags;
    }

    @Override
    public TestBlock apply(TestBlock testBlock) {
        if (runTags.tags.isEmpty() && runTags.excludedTags.isEmpty()) {
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
                .filter(t -> shouldRun(union(getTags(t.options), blockTags)))
                .collect(Collectors.toList());
        return testBlock.toBuilder()
                .setTestBlocks(testBlocks)
                .setTests(tests)
                .build();
    }

    private boolean shouldInclude(Set<String> testTags) {
        return runTags.tags.isEmpty() || !intersection(testTags, runTags.tags).isEmpty();
    }

    private boolean shouldExclude(Set<String> testTags) {
        return !intersection(testTags, runTags.excludedTags).isEmpty();
    }

    private boolean shouldRun(Set<String> testTags) {
        return shouldInclude(testTags) && !shouldExclude(testTags);
    }

    private Set<String> getTags(Options options) {
        return options.get(TagsOption.class).orElse(Collections.emptySet());
    }

    private <T> Set<T> union(Set<T> a, Set<T> b) {
        Set<T> union = new HashSet<>(a);
        union.addAll(b);
        return union;
    }

    private <T> Set<T> intersection(Set<T> a, Set<T> b) {
        Set<T> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        return intersection;
    }
}
