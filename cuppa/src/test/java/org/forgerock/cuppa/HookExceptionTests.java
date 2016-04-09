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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.it;
import static org.forgerock.cuppa.TestCuppaSupport.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.HookType;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HookExceptionTests {

    private static final Map<String, BiConsumer<String, HookFunction>> HOOKS =
            new HashMap<String, BiConsumer<String, HookFunction>>() {
        {
            put("beforeEach", Cuppa::beforeEach);
            put("afterEach", Cuppa::afterEach);
            put("before", Cuppa::before);
            put("after", Cuppa::after);
        }
    };

    private Reporter reporter;

    @BeforeMethod
    public void setupMocks() {
        reporter = mock(Reporter.class);
    }

    @Test(dataProvider = "hooks")
    public void addingTestsInHookShouldThrowException(String hookName) {

        //Given
        TestBlock rootBlock = defineTests(() -> {
            describe("tests in hook", () -> {
                addHook(hookName, "hook", () -> {
                    it("test", TestFunction.identity());
                });
                it("does not run the test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        Throwable cause = verifyHookFail(findHook(rootBlock, "hook"));
        assertThat(cause)
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'it' may only be nested within a 'describe' or 'when' block");
    }

    @Test(dataProvider = "hooks")
    public void addingHookAtTopLevelShouldThrowException(String hookName) {
        assertThatThrownBy(() -> defineTests(() -> {
            addHook(hookName, "hook", () -> {
            });
        }))
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'" + hookName + "' must be nested within a 'describe' or 'when' block");
    }

    @Test(dataProvider = "hooks")
    public void addingHookInTestShouldThrowException(String hookName) {

        //Given
        TestBlock rootBlock = defineTests(() -> {
            describe("hook in test", () -> {
                it("will cause the test to throw an error", () -> {
                    addHook(hookName, "hook", () -> {
                    });
                });
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(reporter).testFail(any(org.forgerock.cuppa.model.Test.class), anyListOf(TestBlock.class),
                captor.capture());
        assertThat(captor.getValue())
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'" + hookName + "' may only be nested within a 'describe' or 'when' block");
    }

    @Test(dataProvider = "hookPairs")
    public void addingNestedHookInHookShouldThrowException(String hookName, String otherHookName) {

        //Given
        TestBlock rootBlock = defineTests(() -> {
            describe("nested hook in hook", () -> {
                addHook(hookName, "hook", () -> {
                    addHook(otherHookName, "otherHook", () -> {

                    });
                });
                it("does not run the test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        Throwable cause = verifyHookFail(findHook(rootBlock, "hook"));
        assertThat(cause)
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'" + otherHookName + "' may only be nested within a 'describe' or 'when' block");
    }

    @Test(dataProvider = "hooks")
    public void shouldAllowHookToThrowCheckedException(String hookName) throws Exception {

        //Given
        Exception exception = new Exception();
        TestBlock rootBlock = defineTests(() -> {
            describe("before blocks", () -> {
                addHook(hookName, "hook", () -> {
                    throw exception;
                });
                it("a test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        Throwable cause = verifyHookFail(findHook(rootBlock, "hook"));
        assertThat(cause).isSameAs(exception);
    }

    @Test(dataProvider = "hooks")
    public void shouldAllowHookToThrowThrowable(String hookName) throws Exception {

        //Given
        AssertionError error = new AssertionError();
        TestBlock rootBlock = defineTests(() -> {
            describe("before blocks", () -> {
                addHook(hookName, "hook", () -> {
                    throw error;
                });
                it("a test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        Throwable cause = verifyHookFail(findHook(rootBlock, "hook"));
        assertThat(cause).isSameAs(error);
    }

    @DataProvider
    private Iterator<Object[]> hooks() {
        return HOOKS.keySet().stream().map(i -> new Object[]{i}).iterator();
    }

    @DataProvider
    private Iterator<Object[]> hookPairs() {
        return HOOKS.keySet().stream().flatMap(a -> HOOKS.keySet().stream().map(b -> new Object[]{a, b})).iterator();
    }

    private void addHook(String hookName, String description, HookFunction function) {
        HOOKS.get(hookName).accept(description, function);
    }

    private Throwable verifyHookFail(Hook hook) {
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        if (hook.type == HookType.BEFORE_EACH || hook.type == HookType.AFTER_EACH) {
            verify(reporter).testHookFail(eq(hook), anyListOf(TestBlock.class),
                    any(org.forgerock.cuppa.model.Test.class), anyListOf(TestBlock.class), captor.capture());
        } else {
            verify(reporter).blockHookFail(eq(hook), anyListOf(TestBlock.class), captor.capture());
        }
        return captor.getValue();
    }
}
