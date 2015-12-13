package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.forgerock.cuppa.Cuppa.*;
import static org.forgerock.cuppa.Cuppa.after;
import static org.forgerock.cuppa.Cuppa.when;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HookExceptionTests {

    private static final List<BiConsumer<String, HookFunction>> ALL_HOOKS =
            new ArrayList<BiConsumer<String, HookFunction>>() {
        {
            add(Cuppa::before);
            add(Cuppa::after);
            add(Cuppa::beforeEach);
            add(Cuppa::afterEach);
        }
    };

    @BeforeMethod
    public void setup() {
        Cuppa.reset();
    }

    @Test
    public void shouldReturnSingleErrorResultIfBeforeHookThrowsException() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        HookFunction beforeFunction = mock(HookFunction.class, "beforeFunction");
        RuntimeException exception = new RuntimeException("Before failed");
        doThrow(exception).when(beforeFunction).apply();
        {
            describe("before blocks", () -> {
                before(beforeFunction);
                it("a test", () -> {
                });
                it("a second test", () -> {
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testError("before", exception);
    }

    @Test
    public void shouldRunAfterHookIfBeforeHookThrowsException() throws Exception {

        //Given
        HookFunction beforeFunction = mock(HookFunction.class, "beforeFunction");
        HookFunction afterFunction = mock(HookFunction.class, "afterFunction");

        doThrow(new RuntimeException("Before failed")).when(beforeFunction).apply();

        {
            describe("before blocks", () -> {
                before(beforeFunction);
                after(afterFunction);
                it("a test", () -> {
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verify(afterFunction).apply();
    }

    @Test
    public void shouldSkipBeforeEachHookIfBeforeHookThrowsException() throws Exception {

        //Given
        HookFunction beforeFunction = mock(HookFunction.class, "beforeFunction");
        HookFunction beforeEachFunction = mock(HookFunction.class, "beforeEachFunction");

        doThrow(new RuntimeException("Before failed")).when(beforeFunction).apply();

        {
            describe("before blocks", () -> {
                before(beforeFunction);
                beforeEach(beforeEachFunction);
                it("a test", () -> {
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verify(beforeEachFunction, never()).apply();
    }

    @Test
    public void shouldSkipAfterEachHookIfBeforeHookThrowsException() throws Exception {

        //Given
        HookFunction beforeFunction = mock(HookFunction.class, "beforeFunction");
        HookFunction afterEachFunction = mock(HookFunction.class, "afterEachFunction");

        doThrow(new RuntimeException("Before failed")).when(beforeFunction).apply();

        {
            describe("before blocks", () -> {
                before(beforeFunction);
                afterEach(afterEachFunction);
                it("a test", () -> {
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verify(afterEachFunction, never()).apply();
    }

    @Test
    public void shouldSkipTestsIfBeforeHookThrowsException() throws Exception {

        //Given
        HookFunction beforeFunction = mock(HookFunction.class, "beforeFunction");
        TestFunction testFunction = mock(TestFunction.class, "testFunction");

        doThrow(new RuntimeException("Before failed")).when(beforeFunction).apply();

        {
            describe("before blocks", () -> {
                before(beforeFunction);
                it("a test", testFunction);
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

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

        {
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
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

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
        {
            describe("beforeEach blocks", () -> {
                beforeEach(beforeEachFunction);
                it("a test", () -> {
                });
                it("a second test", () -> {
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testError("beforeEach", exception);
    }

    @Test
    public void shouldRunAfterHookIfBeforeEachHookThrowsException() throws Exception {

        //Given
        HookFunction beforeEachFunction = mock(HookFunction.class, "beforeEachFunction");
        HookFunction afterFunction = mock(HookFunction.class, "afterFunction");

        doThrow(new RuntimeException("Before each failed")).when(beforeEachFunction).apply();

        {
            describe("beforeEach blocks", () -> {
                beforeEach(beforeEachFunction);
                after(afterFunction);
                it("a test", () -> {
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verify(afterFunction).apply();
    }

    @Test
    public void shouldSkipAfterEachRunIfBeforeEachHookThrowsException() throws Exception {

        //Given
        HookFunction beforeEachFunction = mock(HookFunction.class, "beforeEachFunction");
        HookFunction afterEachFunction = mock(HookFunction.class, "afterEachFunction");

        doThrow(new RuntimeException("Before each failed")).when(beforeEachFunction).apply();

        {
            describe("beforeEach blocks", () -> {
                beforeEach(beforeEachFunction);
                afterEach(afterEachFunction);
                it("a test", () -> {
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

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

        {
            describe("beforeEach blocks", () -> {
                beforeEach(beforeEachFunction);
                it("a test", testFunction1);
                it("a second test", testFunction2);
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

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

        {
            describe("beforeEach blocks", () -> {
                beforeEach(topLevelBeforeEachFunction);
                when("nested block", () -> {
                    beforeEach(nestedBeforeEachFunction);
                    afterEach(nestedAfterEachFunction);
                    it("doesn't run the test nested", nestedTestFunction);
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

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

        {
            describe("beforeEach blocks", () -> {
                beforeEach(topLevelBeforeEachFunction);
                when("nested block", () -> {
                    before(nestedBeforeFunction);
                    after(nestedAfterFunction);
                    it("doesn't run the test nested", () -> {
                    });
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

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

        {
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
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verify(nestedBeforeFunction, never()).apply();
    }

    @Test
    public void shouldHandleDoubleNestedBlocksIfTopLevelBeforeEachThrowsException() throws Exception {

        //Given
        HookFunction topLevelBeforeEachFunction = mock(HookFunction.class, "topLevelBeforeEachFunction");
        HookFunction nestedBeforeFunction = mock(HookFunction.class, "nestedBeforeFunction");

        doThrow(new RuntimeException("Before each failed")).when(topLevelBeforeEachFunction).apply();

        {
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
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

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
        {
            describe("before blocks", () -> {
                before(beforeFunction);
                it("does not run the first test", TestFunction.identity());
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testError("before", exception);
        verify(reporter, never()).testFail(anyString(), any());
        verify(reporter, never()).testPass(anyString());
    }

    @Test
    public void shouldReturnAdditionalErrorResultIfAfterHookThrowsException() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        HookFunction afterFunction = mock(HookFunction.class, "afterFunction");
        RuntimeException exception = new RuntimeException("after failed");
        doThrow(exception).when(afterFunction).apply();
        {
            describe("after blocks", () -> {
                after(afterFunction);
                it("runs the first test", TestFunction.identity());
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testError("after", exception);
    }

    @Test
    public void shouldReturnErrorResultInPlaceOfTestsIfBeforeEachHookThrowsException() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        HookFunction beforeEachFunction = mock(HookFunction.class, "beforeEachFunction");
        RuntimeException exception = new RuntimeException("beforeEach failed");
        doThrow(exception).when(beforeEachFunction).apply();
        {
            describe("beforeEach blocks", () -> {
                beforeEach(beforeEachFunction);
                it("does not run the test", TestFunction.identity());
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testError("beforeEach", exception);
        verify(reporter, never()).testFail(anyString(), any());
        verify(reporter, never()).testPass(anyString());
    }

    @Test
    public void shouldReturnAdditionalErrorResultIfAfterEachHookThrowsException() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        HookFunction afterEachFunction = mock(HookFunction.class, "afterEachFunction");
        RuntimeException exception = new RuntimeException("afterEach failed");
        doThrow(exception).when(afterEachFunction).apply();
        {
            describe("afterEach blocks", () -> {
                afterEach(afterEachFunction);
                it("runs the first test", TestFunction.identity());
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testError("afterEach", exception);
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
        {
            describe("afterEach blocks", () -> {
                beforeEach(beforeEachFunction);
                afterEach(afterEachFunction);
                it("runs the first test", TestFunction.identity());
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testError("beforeEach", beforeEachException);
        verify(reporter).testError("afterEach", afterEachException);
        verify(reporter, times(2)).testError(anyString(), any());
        verify(reporter, never()).testFail(anyString(), any());
        verify(reporter, never()).testPass(anyString());
    }

    @Test
    public void shouldSkipRemainingTestsIfAfterEachThrowsException() throws Exception {

        // Given
        HookFunction afterEachFunction = mock(HookFunction.class, "afterEachFunction");
        TestFunction testFunction1 = mock(TestFunction.class, "testFunction1");
        TestFunction testFunction2 = mock(TestFunction.class, "testFunction2");

        doThrow(new RuntimeException("After each failed")).when(afterEachFunction).apply();

        {
            describe("afterEach blocks", () -> {
                afterEach(afterEachFunction);
                it("runs the first test", testFunction1);
                it("doesn't run the second test", testFunction2);
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

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

        {
            describe("afterEach blocks", () -> {
                afterEach(afterEachFunction);
                when("nested when", () -> {
                    it("runs the first test", testFunction1);
                });
                when("nested when", () -> {
                    it("doesn't run the second test", testFunction2);
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

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

        {
            describe("afterEach blocks", () -> {
                when("nested when", () -> {
                    afterEach(afterEachFunction);
                    it("runs the first test", testFunction1);
                });
                when("nested when", () -> {
                    it("runs the second test", testFunction2);
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

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

        {
            describe("after blocks", () -> {
                when("nested when", () -> {
                    after(afterFunction);
                    it("runs the first test", testFunction1);
                });
                when("nested when", () -> {
                    it("runs the second test", testFunction2);
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verify(testFunction2).apply();
    }

    @DataProvider
    private Iterator<Object[]> testInHooks() {
        return ALL_HOOKS.stream()
                .map(f -> (TestDefinitionFunction) () -> f.accept("", () -> it("", TestFunction.identity())))
                .map(f -> new Object[]{f})
                .iterator();
    }

    @Test(dataProvider = "testInHooks")
    public void addingTestsInHookShouldThrowException(TestDefinitionFunction hookWithTest) {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("", () -> {
                hookWithTest.apply();
                it("", TestFunction.identity());
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testError(anyString(), isA(CuppaException.class));
    }

    @DataProvider
    private Iterator<Object[]> hooks() {
        return ALL_HOOKS.stream()
                .map(f -> (TestFunction) () -> f.accept("", HookFunction.identity()))
                .map(f -> new Object[]{f})
                .iterator();
    }

    @Test(dataProvider = "hooks")
    public void addingHookAtTopLevelShouldThrowException(TestFunction hook) {
        assertThatThrownBy(hook::apply).hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test(dataProvider = "hooks")
    public void addingHookInTestShouldThrowException(TestFunction hook) {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("", () -> {
                it("", hook);
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testError(eq(""), isA(CuppaException.class));
    }

    @DataProvider
    private Iterator<Object[]> hooksInHooks() {
        return ALL_HOOKS.stream()
                .flatMap(f ->
                        ALL_HOOKS.stream().map(g ->
                                (TestDefinitionFunction) () -> f.accept("", () ->
                                        g.accept("", HookFunction.identity()))))
                .map(f -> new Object[]{f})
                .iterator();
    }

    @Test(dataProvider = "hooksInHooks")
    public void addingNestedHookInHookShouldThrowException(TestDefinitionFunction nestedHook) {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("", () -> {
                nestedHook.apply();
                it("", TestFunction.identity());
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testError(anyString(), isA(CuppaException.class));
    }
}
