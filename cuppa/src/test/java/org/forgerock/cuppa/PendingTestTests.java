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

package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Cuppa.*;
import static org.forgerock.cuppa.TestCuppaSupport.*;
import static org.mockito.Mockito.*;

import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;
import org.testng.annotations.Test;

public class PendingTestTests {
    @Test
    public void supportPendingTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("support pending tests", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the first test, which passes", () -> {
                        assertThat(true).isTrue();
                    });
                    it("marks the second test as pending");
                    it("runs the third test, which passes", () -> {
                        assertThat(true).isTrue();
                    });
                });
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter, times(2)).testPass(any());
        verify(reporter).testPending(findTest(rootBlock, "marks the second test as pending"));
    }

    @Test
    public void shouldNotRunEachHooksForPendingTests() throws Exception {

        //Given
        HookFunction hookFunction = mock(HookFunction.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("support pending tests", () -> {
                when("the 'when' block is run", () -> {
                    beforeEach(hookFunction);
                    afterEach(hookFunction);
                    it("skips the pending test");
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(hookFunction, never()).apply();
    }

    @Test
    public void shouldRunOneTimeHooksForPendingTests() throws Exception {

        //Given
        HookFunction hookFunction = mock(HookFunction.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("support pending tests", () -> {
                when("the 'when' block is run", () -> {
                    before(hookFunction);
                    after(hookFunction);
                    it("skips the pending test");
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(hookFunction, times(2)).apply();
    }
}
