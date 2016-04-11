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
import static org.forgerock.cuppa.Cuppa.*;
import static org.forgerock.cuppa.TestCuppaSupport.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.functions.TestBlockFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HookExceptionTests {

    private static final Map<String, BiConsumer<String, HookFunction>> TEST_HOOKS =
            new HashMap<String, BiConsumer<String, HookFunction>>() {
        {
            put("beforeEach", Cuppa::beforeEach);
            put("afterEach", Cuppa::afterEach);
        }
    };

    private static final Map<String, BiConsumer<String, HookFunction>> BLOCK_HOOKS =
            new HashMap<String, BiConsumer<String, HookFunction>>() {
        {
            put("before", Cuppa::before);
            put("after", Cuppa::after);
        }
    };

    private static final Map<String, BiConsumer<String, HookFunction>> ALL_HOOKS =
            new HashMap<String, BiConsumer<String, HookFunction>>() {
        {
            putAll(TEST_HOOKS);
            putAll(BLOCK_HOOKS);
        }
    };

    @DataProvider
    private Iterator<Object[]> testInTestHooks() {
        return TEST_HOOKS.values().stream()
                .map(f -> new Object[] {
                        (TestBlockFunction) () -> f.accept("hook", () -> it("", TestFunction.identity())),
                })
                .iterator();
    }

    @Test(dataProvider = "testInTestHooks")
    public void addingTestsInTestHookShouldThrowException(TestBlockFunction hookWithTest) {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("tests in hook", () -> {
                hookWithTest.apply();
                it("does not run the test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(reporter).testHookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class),
                eq(findTest(rootBlock, "does not run the test")), anyListOf(TestBlock.class), captor.capture());
        assertThat(captor.getValue())
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'it' may only be nested within a 'describe' or 'when' block");
    }

    @DataProvider
    private Iterator<Object[]> testInBlockHooks() {
        return BLOCK_HOOKS.values().stream()
                .map(f -> new Object[] {
                        (TestBlockFunction) () -> f.accept("hook", () -> it("", TestFunction.identity())),
                })
                .iterator();
    }

    @Test(dataProvider = "testInBlockHooks")
    public void addingTestsInBlockHookShouldThrowException(TestBlockFunction hookWithTest) {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("tests in hook", () -> {
                hookWithTest.apply();
                it("does not run the test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(reporter).blockHookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class), captor.capture());
        assertThat(captor.getValue())
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'it' may only be nested within a 'describe' or 'when' block");
    }

    @DataProvider
    private Iterator<Object[]> hooks() {
        return ALL_HOOKS.entrySet().stream()
                .map(e -> new Object[]{
                        e.getKey(),
                        (Runnable) () -> e.getValue().accept("hook", HookFunction.identity()),
                })
                .iterator();
    }

    @Test(dataProvider = "hooks")
    public void addingHookAtTopLevelShouldThrowException(String hookName, Runnable hook) {
        assertThatThrownBy(() -> defineTests(hook::run))
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'" + hookName + "' must be nested within a 'describe' or 'when' block");
    }

    @Test(dataProvider = "hooks")
    public void addingHookInTestShouldThrowException(String hookName, Runnable hook) {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("hook in test", () -> {
                it("will cause the test to throw an error", hook::run);
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(reporter).testFail(eq(findTest(rootBlock, "will cause the test to throw an error")),
                anyListOf(TestBlock.class), captor.capture());
        assertThat(captor.getValue())
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'" + hookName + "' may only be nested within a 'describe' or 'when' block");
    }

    @DataProvider
    private Iterator<Object[]> hooksInTestHooks() {
        return TEST_HOOKS.values().stream()
                .flatMap(f ->
                        ALL_HOOKS.entrySet().stream().map(e -> new Object[] {
                                e.getKey(),
                                (TestBlockFunction) () -> f.accept("hook",
                                        () -> e.getValue().accept("", HookFunction.identity())),
                        }))
                .iterator();
    }

    @Test(dataProvider = "hooksInTestHooks")
    public void addingNestedHookInTestHookShouldThrowException(String hookName, TestBlockFunction nestedHook) {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("nested hook in hook", () -> {
                nestedHook.apply();
                it("does not run the test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(reporter).testHookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class),
                eq(findTest(rootBlock, "does not run the test")), anyListOf(TestBlock.class), captor.capture());
        assertThat(captor.getValue())
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'" + hookName + "' may only be nested within a 'describe' or 'when' block");
    }

    @DataProvider
    private Iterator<Object[]> hooksInBlockHooks() {
        return BLOCK_HOOKS.values().stream()
                .flatMap(f ->
                        ALL_HOOKS.entrySet().stream().map(e -> new Object[] {
                                e.getKey(),
                                (TestBlockFunction) () -> f.accept("hook",
                                        () -> e.getValue().accept("", HookFunction.identity())),
                        }))
                .iterator();
    }

    @Test(dataProvider = "hooksInBlockHooks")
    public void addingNestedHookInBlockHookShouldThrowException(String hookName, TestBlockFunction nestedHook) {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("nested hook in hook", () -> {
                nestedHook.apply();
                it("does not run the test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(reporter).blockHookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class), captor.capture());
        assertThat(captor.getValue())
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'" + hookName + "' may only be nested within a 'describe' or 'when' block");
    }

    @DataProvider
    private Iterator<Object[]> testHooksThrowExceptions() {
        return TEST_HOOKS.values().stream()
                .map(f -> (TestBlockFunction) () -> f.accept("hook", () -> {
                    throw new Exception();
                }))
                .map(f -> new Object[]{f})
                .iterator();
    }

    @Test(dataProvider = "testHooksThrowExceptions")
    public void shouldAllowTestHookToThrowCheckedException(TestBlockFunction hook) throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("before blocks", () -> {
                hook.apply();
                it("a test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).testHookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class),
                eq(findTest(rootBlock, "a test")), anyListOf(TestBlock.class), isA(Exception.class));
    }

    @DataProvider
    private Iterator<Object[]> blockHooksThrowExceptions() {
        return BLOCK_HOOKS.values().stream()
                .map(f -> (TestBlockFunction) () -> f.accept("hook", () -> {
                    throw new Exception();
                }))
                .map(f -> new Object[]{f})
                .iterator();
    }

    @Test(dataProvider = "blockHooksThrowExceptions")
    public void shouldAllowBlockHookToThrowCheckedException(TestBlockFunction hook) throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("before blocks", () -> {
                hook.apply();
                it("a test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).blockHookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class),
                isA(Exception.class));
    }

    @DataProvider
    private Iterator<Object[]> testHooksThrowThrowable() {
        return TEST_HOOKS.values().stream()
                .map(f -> (TestBlockFunction) () -> f.accept("hook", () -> {
                    throw new AssertionError();
                }))
                .map(f -> new Object[]{f})
                .iterator();
    }

    @Test(dataProvider = "testHooksThrowThrowable")
    public void shouldAllowTestHookToThrowThrowable(TestBlockFunction hook) throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("before blocks", () -> {
                hook.apply();
                it("a test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).testHookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class),
                eq(findTest(rootBlock, "a test")), anyListOf(TestBlock.class), isA(AssertionError.class));
    }

    @DataProvider
    private Iterator<Object[]> blockHooksThrowThrowable() {
        return BLOCK_HOOKS.values().stream()
                .map(f -> (TestBlockFunction) () -> f.accept("hook", () -> {
                    throw new AssertionError();
                }))
                .map(f -> new Object[]{f})
                .iterator();
    }

    @Test(dataProvider = "blockHooksThrowThrowable")
    public void shouldAllowBlockHookToThrowThrowable(TestBlockFunction hook) throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("before blocks", () -> {
                hook.apply();
                it("a test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).blockHookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class),
                isA(AssertionError.class));
    }
}
