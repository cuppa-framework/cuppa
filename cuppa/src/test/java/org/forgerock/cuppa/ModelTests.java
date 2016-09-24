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

package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.forgerock.cuppa.model.TestBlockType.ROOT;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.HookBuilder;
import org.forgerock.cuppa.model.HookType;
import org.forgerock.cuppa.model.Option;
import org.forgerock.cuppa.model.Options;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.model.TestBlockBuilder;
import org.forgerock.cuppa.model.TestBuilder;
import org.testng.annotations.Test;

public class ModelTests {
    @Test
    public void testBlockShouldBeImmutable() {
        TestBlock testBlock = new TestBlockBuilder()
                .setType(ROOT)
                .setTestClass(ModelTests.class)
                .setDescription("")
                .build();

        assertThatThrownBy(() -> testBlock.testBlocks.add(testBlock))
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        org.forgerock.cuppa.model.Test test = new TestBuilder()
                .setTestClass(ModelTests.class)
                .setDescription("")
                .setFunction(Optional.empty())
                .build();
        assertThatThrownBy(() -> testBlock.tests.add(test))
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        Hook hook = new HookBuilder()
                .setType(HookType.BEFORE)
                .setTestClass(ModelTests.class)
                .setDescription(Optional.empty())
                .setFunction(HookFunction.identity())
                .build();
        assertThatThrownBy(() -> testBlock.hooks.add(hook))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testBlockShouldTakeDefensiveCopiesOfMutableObjects() {
        List<TestBlock> originalTestBlocks = new ArrayList<>();
        List<org.forgerock.cuppa.model.Test> originalTests = new ArrayList<>();
        List<Hook> originalHooks = new ArrayList<>();
        Options originalOptions = Options.EMPTY;

        TestBlock testBlock = new TestBlockBuilder()
                .setType(ROOT)
                .setTestClass(ModelTests.class)
                .setDescription("")
                .setTestBlocks(originalTestBlocks)
                .setHooks(originalHooks)
                .setTests(originalTests)
                .setOptions(originalOptions)
                .build();

        originalTestBlocks.add(testBlock);

        org.forgerock.cuppa.model.Test test = new TestBuilder()
                .setTestClass(ModelTests.class)
                .setDescription("")
                .setFunction(Optional.empty())
                .build();
        originalTests.add(test);
        Hook hook = new HookBuilder()
                .setType(HookType.BEFORE)
                .setTestClass(ModelTests.class)
                .setDescription(Optional.empty())
                .setFunction(HookFunction.identity())
                .build();
        originalHooks.add(hook);
        originalOptions.set(new TestOption("a"));

        assertThat(testBlock.testBlocks).hasSize(0);
        assertThat(testBlock.tests).hasSize(0);
        assertThat(testBlock.hooks).hasSize(0);
        assertThat(testBlock.options.get(TestOption.class)).isEmpty();
    }

    private static final class TestOption extends Option<String> {
        private TestOption(String value) {
            super(value);
        }
    }
}
