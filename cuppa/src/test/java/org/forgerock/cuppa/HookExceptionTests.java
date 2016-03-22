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

    private static final Map<String, BiConsumer<String, HookFunction>> ALL_HOOKS =
            new HashMap<String, BiConsumer<String, HookFunction>>() {
        {
            put("before", Cuppa::before);
            put("after", Cuppa::after);
            put("beforeEach", Cuppa::beforeEach);
            put("afterEach", Cuppa::afterEach);
        }
    };

    @Test
    public void shouldReturnSingleErrorResultIfBeforeHookThrowsException() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        HookFunction beforeFunction = mock(HookFunction.class, "beforeFunction");
        RuntimeException exception = new RuntimeException("Before failed");
        doThrow(exception).when(beforeFunction).apply();
        TestBlock rootBlock = defineTests(() -> {
            describe("before blocks", () -> {
                before("hook", beforeFunction);
                it("a test", () -> {
                });
                it("a second test", () -> {
                });
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).hookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class), eq(exception));
    }

    @Test
    public void shouldRunAfterHookIfBeforeHookThrowsException() throws Exception {

        //Given
        HookFunction beforeFunction = mock(HookFunction.class, "beforeFunction");
        HookFunction afterFunction = mock(HookFunction.class, "afterFunction");

        doThrow(new RuntimeException("Before failed")).when(beforeFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("before blocks", () -> {
                before(beforeFunction);
                after(afterFunction);
                it("a test", () -> {
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(afterFunction).apply();
    }

    @Test
    public void shouldSkipBeforeEachHookIfBeforeHookThrowsException() throws Exception {

        //Given
        HookFunction beforeFunction = mock(HookFunction.class, "beforeFunction");
        HookFunction beforeEachFunction = mock(HookFunction.class, "beforeEachFunction");

        doThrow(new RuntimeException("Before failed")).when(beforeFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("before blocks", () -> {
                before(beforeFunction);
                beforeEach(beforeEachFunction);
                it("a test", () -> {
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(beforeEachFunction, never()).apply();
    }

    @Test
    public void shouldSkipAfterEachHookIfBeforeHookThrowsException() throws Exception {

        //Given
        HookFunction beforeFunction = mock(HookFunction.class, "beforeFunction");
        HookFunction afterEachFunction = mock(HookFunction.class, "afterEachFunction");

        doThrow(new RuntimeException("Before failed")).when(beforeFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("before blocks", () -> {
                before(beforeFunction);
                afterEach(afterEachFunction);
                it("a test", () -> {
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(afterEachFunction, never()).apply();
    }

    @Test
    public void shouldSkipTestsIfBeforeHookThrowsException() throws Exception {

        //Given
        HookFunction beforeFunction = mock(HookFunction.class, "beforeFunction");
        TestFunction testFunction = mock(TestFunction.class, "testFunction");

        doThrow(new RuntimeException("Before failed")).when(beforeFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("before blocks", () -> {
                before(beforeFunction);
                it("a test", testFunction);
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(testFunction, never()).apply();
    }

    @Test
    public void shouldSkipNestedBlocksIfBeforeThrowsException() throws Exception {

        //Given
        HookFunction topLevelBeforeFunction = mock(HookFunction.class, "topLevelBeforeFunction");
        HookFunction nestedBeforeFunction = mock(HookFunction.class, "nestedBeforeFunction");
        HookFunction nestedBeforeEachFunction = mock(HookFunction.class, "nestedBeforeEachFunction");
        HookFunction nestedAfterEachFunction = mock(HookFunction.class, "nestedAfterEachFunction");
        HookFunction nestedAfterFunction = mock(HookFunction.class, "nestedAfterFunction");
        TestFunction nestedTestFunction = mock(TestFunction.class, "nestedTestFunction");

        doThrow(new RuntimeException("Before failed")).when(topLevelBeforeFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("before blocks", () -> {
                before(topLevelBeforeFunction);
                when("the first 'before' block throws an exception", () -> {
                    before(nestedBeforeFunction);
                    beforeEach(nestedBeforeEachFunction);
                    afterEach(nestedAfterEachFunction);
                    after(nestedAfterFunction);
                    it("doesn't run the test nested", nestedTestFunction);
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(nestedBeforeFunction, never()).apply();
        verify(nestedBeforeEachFunction, never()).apply();
        verify(nestedAfterEachFunction, never()).apply();
        verify(nestedAfterFunction, never()).apply();
        verify(nestedTestFunction, never()).apply();
    }

    @Test
    public void shouldReturnSingleErrorResultIfBeforeEachHookThrowsException() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        HookFunction beforeEachFunction = mock(HookFunction.class, "beforeEachFunction");
        RuntimeException exception = new RuntimeException("beforeEach failed");
        doThrow(exception).when(beforeEachFunction).apply();
        TestBlock rootBlock = defineTests(() -> {
            describe("beforeEach blocks", () -> {
                beforeEach("hook", beforeEachFunction);
                it("a test", () -> {
                });
                it("a second test", () -> {
                });
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).hookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class), eq(exception));
    }

    @Test
    public void shouldRunAfterHookIfBeforeEachHookThrowsException() throws Exception {

        //Given
        HookFunction beforeEachFunction = mock(HookFunction.class, "beforeEachFunction");
        HookFunction afterFunction = mock(HookFunction.class, "afterFunction");

        doThrow(new RuntimeException("Before each failed")).when(beforeEachFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("beforeEach blocks", () -> {
                beforeEach(beforeEachFunction);
                after(afterFunction);
                it("a test", () -> {
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(afterFunction).apply();
    }

    @Test
    public void shouldSkipAfterEachRunIfBeforeEachHookThrowsException() throws Exception {

        //Given
        HookFunction beforeEachFunction = mock(HookFunction.class, "beforeEachFunction");
        HookFunction afterEachFunction = mock(HookFunction.class, "afterEachFunction");

        doThrow(new RuntimeException("Before each failed")).when(beforeEachFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("beforeEach blocks", () -> {
                beforeEach(beforeEachFunction);
                afterEach(afterEachFunction);
                it("a test", () -> {
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(afterEachFunction).apply();
    }

    @Test
    public void shouldSkipTestsIfBeforeEachHookThrowsException() throws Exception {

        //Given
        HookFunction beforeEachFunction = mock(HookFunction.class, "beforeEachFunction");
        TestFunction testFunction1 = mock(TestFunction.class, "testFunction");
        TestFunction testFunction2 = mock(TestFunction.class, "testFunction");

        doThrow(new RuntimeException("Before each failed")).when(beforeEachFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("beforeEach blocks", () -> {
                beforeEach(beforeEachFunction);
                it("a test", testFunction1);
                it("a second test", testFunction2);
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(testFunction1, never()).apply();
        verify(testFunction2, never()).apply();
    }

    @Test
    public void shouldSkipNestedBeforeEachAfterEachAndTestsIfBeforeEachThrowsException() throws Exception {

        //Given
        HookFunction topLevelBeforeEachFunction = mock(HookFunction.class, "topLevelBeforeEachFunction");
        HookFunction nestedBeforeEachFunction = mock(HookFunction.class, "nestedBeforeEachFunction");
        HookFunction nestedAfterEachFunction = mock(HookFunction.class, "nestedAfterEachFunction");
        TestFunction nestedTestFunction = mock(TestFunction.class, "nestedTestFunction");

        doThrow(new RuntimeException("Before each failed")).when(topLevelBeforeEachFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("beforeEach blocks", () -> {
                beforeEach(topLevelBeforeEachFunction);
                when("nested block", () -> {
                    beforeEach(nestedBeforeEachFunction);
                    afterEach(nestedAfterEachFunction);
                    it("doesn't run the test nested", nestedTestFunction);
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(nestedBeforeEachFunction, never()).apply();
        verify(nestedAfterEachFunction, never()).apply();
        verify(nestedTestFunction, never()).apply();
    }

    @Test
    public void shouldRunNestedBeforeAndAfterHooksIfBeforeEachThrowsException() throws Exception {

        //Given
        HookFunction topLevelBeforeEachFunction = mock(HookFunction.class, "topLevelBeforeEachFunction");
        HookFunction nestedBeforeFunction = mock(HookFunction.class, "nestedBeforeFunction");
        HookFunction nestedAfterFunction = mock(HookFunction.class, "nestedAfterFunction");

        doThrow(new RuntimeException("Before each failed")).when(topLevelBeforeEachFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("beforeEach blocks", () -> {
                beforeEach(topLevelBeforeEachFunction);
                when("nested block", () -> {
                    before(nestedBeforeFunction);
                    after(nestedAfterFunction);
                    it("doesn't run the test nested", () -> {
                    });
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(nestedBeforeFunction).apply();
        verify(nestedAfterFunction).apply();
    }

    @Test
    public void shouldSkipAllNestedBlocksIfTopLevelBeforeEachThrowsException() throws Exception {

        //Given
        HookFunction topLevelBeforeEachFunction = mock(HookFunction.class, "topLevelBeforeEachFunction");
        HookFunction nestedBeforeFunction = mock(HookFunction.class, "nestedBeforeFunction");

        doThrow(new RuntimeException("Before each failed")).when(topLevelBeforeEachFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("beforeEach blocks", () -> {
                beforeEach(topLevelBeforeEachFunction);
                when("nested block", () -> {
                    it("doesn't run the test nested", () -> {
                    });
                });
                when("nested block", () -> {
                    before(nestedBeforeFunction);
                    it("doesn't run the test nested", () -> {
                    });
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(nestedBeforeFunction, never()).apply();
    }

    @Test
    public void shouldHandleDoubleNestedBlocksIfTopLevelBeforeEachThrowsException() throws Exception {

        //Given
        HookFunction topLevelBeforeEachFunction = mock(HookFunction.class, "topLevelBeforeEachFunction");
        HookFunction nestedBeforeFunction = mock(HookFunction.class, "nestedBeforeFunction");

        doThrow(new RuntimeException("Before each failed")).when(topLevelBeforeEachFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("beforeEach blocks", () -> {
                when("nested block", () -> {
                    beforeEach(topLevelBeforeEachFunction);
                    when("double nested block", () -> {
                        it("doesn't run the test nested", () -> {
                        });
                    });
                });
                when("nested block", () -> {
                    before(nestedBeforeFunction);
                    it("doesn't run the test nested", () -> {
                    });
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(nestedBeforeFunction).apply();
    }

    @Test
    public void shouldReturnErrorResultInPlaceOfNestedBlocksIfBeforeHookThrowsException() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        HookFunction beforeFunction = mock(HookFunction.class, "beforeFunction");
        RuntimeException exception = new RuntimeException("before failed");
        doThrow(exception).when(beforeFunction).apply();
        TestBlock rootBlock = defineTests(() -> {
            describe("before blocks", () -> {
                before("hook", beforeFunction);
                it("does not run the first test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).hookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class), eq(exception));
        verify(reporter, never()).testFail(any(), anyListOf(TestBlock.class), any());
        verify(reporter, never()).testPass(any(), anyListOf(TestBlock.class));
    }

    @Test
    public void shouldReturnAdditionalErrorResultIfAfterHookThrowsException() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        HookFunction afterFunction = mock(HookFunction.class, "afterFunction");
        RuntimeException exception = new RuntimeException("after failed");
        doThrow(exception).when(afterFunction).apply();
        TestBlock rootBlock = defineTests(() -> {
            describe("after blocks", () -> {
                after("hook", afterFunction);
                it("runs the first test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).hookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class), eq(exception));
    }

    @Test
    public void shouldReturnErrorResultInPlaceOfTestsIfBeforeEachHookThrowsException() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        HookFunction beforeEachFunction = mock(HookFunction.class, "beforeEachFunction");
        RuntimeException exception = new RuntimeException("beforeEach failed");
        doThrow(exception).when(beforeEachFunction).apply();
        TestBlock rootBlock = defineTests(() -> {
            describe("beforeEach blocks", () -> {
                beforeEach("hook", beforeEachFunction);
                it("does not run the test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).hookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class), eq(exception));
        verify(reporter, never()).testFail(any(), anyListOf(TestBlock.class), any());
        verify(reporter, never()).testPass(any(), anyListOf(TestBlock.class));
    }

    @Test
    public void shouldReturnAdditionalErrorResultIfAfterEachHookThrowsException() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        HookFunction afterEachFunction = mock(HookFunction.class, "afterEachFunction");
        RuntimeException exception = new RuntimeException("afterEach failed");
        doThrow(exception).when(afterEachFunction).apply();
        TestBlock rootBlock = defineTests(() -> {
            describe("afterEach blocks", () -> {
                afterEach("hook", afterEachFunction);
                it("runs the first test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).hookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class), eq(exception));
    }

    @Test
    public void shouldReturnErrorResultsIfBeforeEachAndAfterEachHookThrowsException() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        HookFunction beforeEachFunction = mock(HookFunction.class, "beforeEachFunction");
        RuntimeException beforeEachException = new RuntimeException("beforeEach failed");
        doThrow(beforeEachException).when(beforeEachFunction).apply();
        HookFunction afterEachFunction = mock(HookFunction.class, "afterEachFunction");
        RuntimeException afterEachException = new RuntimeException("afterEach failed");
        doThrow(afterEachException).when(afterEachFunction).apply();
        TestBlock rootBlock = defineTests(() -> {
            describe("afterEach blocks", () -> {
                beforeEach("beforeEach", beforeEachFunction);
                afterEach("afterEach", afterEachFunction);
                it("runs the first test", TestFunction.identity());
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).hookFail(eq(findHook(rootBlock, "beforeEach")), anyListOf(TestBlock.class),
                eq(beforeEachException));
        verify(reporter).hookFail(eq(findHook(rootBlock, "afterEach")), anyListOf(TestBlock.class),
                eq(afterEachException));
        verify(reporter, times(2)).hookFail(any(), anyListOf(TestBlock.class), any());
        verify(reporter, never()).testFail(any(), anyListOf(TestBlock.class), any());
        verify(reporter, never()).testPass(any(), anyListOf(TestBlock.class));
    }

    @Test
    public void shouldSkipRemainingTestsIfAfterEachThrowsException() throws Exception {

        // Given
        HookFunction afterEachFunction = mock(HookFunction.class, "afterEachFunction");
        TestFunction testFunction1 = mock(TestFunction.class, "testFunction1");
        TestFunction testFunction2 = mock(TestFunction.class, "testFunction2");

        doThrow(new RuntimeException("After each failed")).when(afterEachFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("afterEach blocks", () -> {
                afterEach(afterEachFunction);
                it("runs the first test", testFunction1);
                it("doesn't run the second test", testFunction2);
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(testFunction2, never()).apply();
    }

    @Test
    public void shouldSkipRemainingBlocksIfAfterEachThrowsException() throws Exception {

        // Given
        HookFunction afterEachFunction = mock(HookFunction.class, "afterEachFunction");
        TestFunction testFunction1 = mock(TestFunction.class, "testFunction1");
        TestFunction testFunction2 = mock(TestFunction.class, "testFunction2");

        doThrow(new RuntimeException("After each failed")).when(afterEachFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("afterEach blocks", () -> {
                afterEach(afterEachFunction);
                when("nested when", () -> {
                    it("runs the first test", testFunction1);
                });
                when("nested when", () -> {
                    it("doesn't run the second test", testFunction2);
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(testFunction2, never()).apply();
    }

    @Test
    public void shouldRunRemainingBlocksInOuterScopeIfNestedAfterEachThrowsException() throws Exception {

        // Given
        HookFunction afterEachFunction = mock(HookFunction.class, "afterEachFunction");
        TestFunction testFunction1 = mock(TestFunction.class, "testFunction1");
        TestFunction testFunction2 = mock(TestFunction.class, "testFunction2");

        doThrow(new RuntimeException("After each failed")).when(afterEachFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("afterEach blocks", () -> {
                when("nested when", () -> {
                    afterEach(afterEachFunction);
                    it("runs the first test", testFunction1);
                });
                when("nested when", () -> {
                    it("runs the second test", testFunction2);
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(testFunction2).apply();
    }

    @Test
    public void shouldRunRemainingBlocksInOuterScopeIfNestedAfterThrowsException() throws Exception {

        // Given
        HookFunction afterFunction = mock(HookFunction.class, "afterFunction");
        TestFunction testFunction1 = mock(TestFunction.class, "testFunction1");
        TestFunction testFunction2 = mock(TestFunction.class, "testFunction2");

        doThrow(new RuntimeException("After failed")).when(afterFunction).apply();

        TestBlock rootBlock = defineTests(() -> {
            describe("after blocks", () -> {
                when("nested when", () -> {
                    after(afterFunction);
                    it("runs the first test", testFunction1);
                });
                when("nested when", () -> {
                    it("runs the second test", testFunction2);
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(testFunction2).apply();
    }

    @DataProvider
    private Iterator<Object[]> testInHooks() {
        return ALL_HOOKS.values().stream()
                .map(f -> new Object[] {
                        (TestBlockFunction) () -> f.accept("hook", () -> it("", TestFunction.identity())),
                })
                .iterator();
    }

    @Test(dataProvider = "testInHooks")
    public void addingTestsInHookShouldThrowException(TestBlockFunction hookWithTest) {

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
        verify(reporter).hookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class), captor.capture());
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
    private Iterator<Object[]> hooksInHooks() {
        return ALL_HOOKS.values().stream()
                .flatMap(f ->
                        ALL_HOOKS.entrySet().stream().map(e -> new Object[] {
                                e.getKey(),
                                (TestBlockFunction) () -> f.accept("hook",
                                        () -> e.getValue().accept("", HookFunction.identity())),
                        }))
                .iterator();
    }

    @Test(dataProvider = "hooksInHooks")
    public void addingNestedHookInHookShouldThrowException(String hookName, TestBlockFunction nestedHook) {

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
        verify(reporter).hookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class), captor.capture());
        assertThat(captor.getValue())
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'" + hookName + "' may only be nested within a 'describe' or 'when' block");
    }

    @DataProvider
    private Iterator<Object[]> hooksThrowExceptions() {
        return ALL_HOOKS.values().stream()
                .map(f -> (TestBlockFunction) () -> f.accept("hook", () -> {
                    throw new Exception();
                }))
                .map(f -> new Object[]{f})
                .iterator();
    }

    @Test(dataProvider = "hooksThrowExceptions")
    public void shouldAllowHookToThrowCheckedException(TestBlockFunction hook) throws Exception {

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
        verify(reporter).hookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class), isA(Exception.class));
    }

    @DataProvider
    private Iterator<Object[]> hooksThrowThrowable() {
        return ALL_HOOKS.values().stream()
                .map(f -> (TestBlockFunction) () -> f.accept("hook", () -> {
                    throw new AssertionError();
                }))
                .map(f -> new Object[]{f})
                .iterator();
    }

    @Test(dataProvider = "hooksThrowThrowable")
    public void shouldAllowHookToThrowThrowable(TestBlockFunction hook) throws Exception {

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
        verify(reporter).hookFail(eq(findHook(rootBlock, "hook")), anyListOf(TestBlock.class),
                isA(AssertionError.class));
    }
}
