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
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.with;
import static org.forgerock.cuppa.TestCuppaSupport.defineTests;
import static org.forgerock.cuppa.TestCuppaSupport.findTest;
import static org.forgerock.cuppa.model.Behaviour.NORMAL;
import static org.forgerock.cuppa.model.TestBlockType.ROOT;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.HookType;
import org.forgerock.cuppa.model.Option;
import org.forgerock.cuppa.model.Options;
import org.forgerock.cuppa.model.TestBlock;
import org.testng.annotations.Test;

public class ModelTests {
    @Test
    public void testBlockShouldBeImmutable() {
        TestBlock testBlock = new TestBlock(ROOT, NORMAL, ModelTests.class, "", new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new Options());

        assertThatThrownBy(() -> testBlock.testBlocks.add(testBlock))
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        assertThatThrownBy(() -> testBlock.tests.add(new org.forgerock.cuppa.model.Test(NORMAL,
                ModelTests.class, "", Optional.empty(), new Options())))
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        assertThatThrownBy(() -> testBlock.hooks.add(new Hook(HookType.BEFORE, ModelTests.class, Optional.empty(),
                HookFunction.identity())))
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        assertThatThrownBy(() -> testBlock.options.set(new TestOption("a")))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testBlockShouldTakeDefensiveCopiesOfMutableObjects() {
        List<TestBlock> originalTestBlocks = new ArrayList<>();
        List<org.forgerock.cuppa.model.Test> originalTests = new ArrayList<>();
        List<Hook> originalHooks = new ArrayList<>();
        Options originalOptions = new Options();

        TestBlock testBlock = new TestBlock(ROOT, NORMAL, ModelTests.class, "", originalTestBlocks,
                originalHooks, originalTests, originalOptions);

        originalTestBlocks.add(testBlock);
        originalTests.add(new org.forgerock.cuppa.model.Test(NORMAL,
                ModelTests.class, "", Optional.empty(), new Options()));
        originalHooks.add(new Hook(HookType.BEFORE, ModelTests.class, Optional.empty(), HookFunction.identity()));
        originalOptions.set(new TestOption("a"));

        assertThat(testBlock.testBlocks).hasSize(0);
        assertThat(testBlock.tests).hasSize(0);
        assertThat(testBlock.hooks).hasSize(0);
        assertThat(testBlock.options.get(TestOption.class)).isEmpty();
    }

    @Test
    public void testShouldBeImmutable() {
        org.forgerock.cuppa.model.Test test = new org.forgerock.cuppa.model.Test(NORMAL, ModelTests.class, "",
                Optional.empty(), new Options());

        assertThatThrownBy(() -> test.options.set(new TestOption("a")))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testShouldTakeDefensiveCopiesOfMutableObjects() {
        Options originalOptions = new Options();

        org.forgerock.cuppa.model.Test test = new org.forgerock.cuppa.model.Test(NORMAL, ModelTests.class, "",
                Optional.empty(), originalOptions);

        originalOptions.set(new TestOption("a"));

        assertThat(test.options.get(TestOption.class)).isEmpty();
    }


    @Test
    public void shouldMergeMultipleOptionsOfSameType() throws Exception {

        //When
        TestBlock rootBlock = defineTests(() -> {
            describe("tagged tests", () -> {
                with(testOptions("A"), testOptions("B")).
                it("has both options", TestFunction.identity());
            });
        });

        //Then
        assertThat(findTest(rootBlock, "has both options").options.get(TestOption.class).get()).isEqualTo("AB");
    }

    private static TestOption testOptions(String value) {
        return new TestOption(value);
    }

    private static final class TestOption extends Option<String> {
        private TestOption(String value) {
            super(value);
        }

        @Override
        protected Option<String> merge(String value) {
            return new TestOption(get() + value);
        }
    }
}
