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

package org.forgerock.cuppa.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.model.Behaviour;
import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;

final class InternalTestBlockBuilder {

    private final Behaviour behaviour;
    private final String description;
    private final List<TestBlock> testBlocks = new ArrayList<>();
    private final List<Hook> beforeHooks = new ArrayList<>();
    private final List<Hook> afterAfter = new ArrayList<>();
    private final List<Hook> beforeEachHooks = new ArrayList<>();
    private final List<Hook> afterEachHooks = new ArrayList<>();
    private final List<TestBuilderImpl> testBuilders = new ArrayList<>();
    private final Set<String> tags = new HashSet<>();

    InternalTestBlockBuilder(Behaviour behaviour, String description) {
        this.behaviour = behaviour;
        this.description = description;
    }

    InternalTestBlockBuilder addTestBlock(TestBlock testBlock) {
        testBlocks.add(testBlock);
        return this;
    }

    InternalTestBlockBuilder addBefore(Optional<String> description, HookFunction function) {
        beforeHooks.add(new Hook(description, function));
        return this;
    }

    InternalTestBlockBuilder addAfter(Optional<String> description, HookFunction function) {
        afterAfter.add(new Hook(description, function));
        return this;
    }

    InternalTestBlockBuilder addBeforeEach(Optional<String> description, HookFunction function) {
        beforeEachHooks.add(new Hook(description, function));
        return this;
    }

    InternalTestBlockBuilder addAfterEach(Optional<String> description, HookFunction function) {
        afterEachHooks.add(new Hook(description, function));
        return this;
    }

    InternalTestBlockBuilder addTest(TestBuilderImpl test) {
        testBuilders.add(test);
        return this;
    }

    InternalTestBlockBuilder eachWithTags(String tag, String... tags) {
        this.tags.addAll(Arrays.asList(tags));
        this.tags.add(tag);
        return this;
    }

    TestBlock build() {
        List<Test> tests = testBuilders.stream().map(TestBuilderImpl::build).collect(Collectors.toList());
        return new TestBlock(behaviour, description, testBlocks, beforeHooks, afterAfter, beforeEachHooks,
                afterEachHooks, tests, tags);
    }
}
