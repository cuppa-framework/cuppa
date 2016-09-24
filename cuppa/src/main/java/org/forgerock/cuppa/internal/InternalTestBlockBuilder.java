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

import static org.forgerock.cuppa.model.HookType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.model.Behaviour;
import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.HookBuilder;
import org.forgerock.cuppa.model.Options;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.model.TestBlockBuilder;
import org.forgerock.cuppa.model.TestBlockType;

final class InternalTestBlockBuilder {

    private final TestBlockBuilder builder = new TestBlockBuilder();
    private final Class<?> testClass;
    private final List<TestBlock> testBlocks = new ArrayList<>();
    private final List<Hook> hooks = new ArrayList<>();
    private final List<Test> tests = new ArrayList<>();

    InternalTestBlockBuilder(TestBlockType type, Behaviour behaviour, Class<?> testClass, String description,
            Options options) {
        this.testClass = testClass;
        builder.setType(type)
                .setBehaviour(behaviour)
                .setTestClass(testClass)
                .setDescription(description)
                .setOptions(options)
                .setTestBlocks(testBlocks)
                .setTests(tests)
                .setHooks(hooks);
    }

    InternalTestBlockBuilder addTestBlock(TestBlock testBlock) {
        testBlocks.add(testBlock);
        return this;
    }

    InternalTestBlockBuilder addBefore(Optional<String> description, HookFunction function) {
        hooks.add(getHookBuilder(description, function).setType(BEFORE).build());
        return this;
    }

    InternalTestBlockBuilder addAfter(Optional<String> description, HookFunction function) {
        hooks.add(getHookBuilder(description, function).setType(AFTER).build());
        return this;
    }

    InternalTestBlockBuilder addBeforeEach(Optional<String> description, HookFunction function) {
        hooks.add(getHookBuilder(description, function).setType(BEFORE_EACH).build());
        return this;
    }

    InternalTestBlockBuilder addAfterEach(Optional<String> description, HookFunction function) {
        hooks.add(getHookBuilder(description, function).setType(AFTER_EACH).build());
        return this;
    }

    InternalTestBlockBuilder addTest(Test test) {
        tests.add(test);
        return this;
    }

    private HookBuilder getHookBuilder(Optional<String> description, HookFunction function) {
        return new HookBuilder().setTestClass(testClass).setDescription(description).setFunction(function);
    }

    TestBlock build() {
        return builder.build();
    }
}
