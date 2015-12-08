package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.forgerock.cuppa.Cuppa.*;
import static org.forgerock.cuppa.Cuppa.after;
import static org.forgerock.cuppa.Cuppa.when;
import static org.forgerock.cuppa.Reporter.Outcome.ERRORED;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HookExceptionTests {

    private static final Function EMPTY_FUNCTION = () -> {
    };
    private static final List<BiConsumer<String, Function>> ALL_HOOKS =
            new ArrayList<BiConsumer<String, Function>>() {
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
    public void shouldReturnSingleErrorResultIfBeforeHookThrowsException() {

        //Given
        Reporter reporter = mock(Reporter.class);
        Function beforeFunction = mock(Function.class, "beforeFunction");
        doThrow(new RuntimeException("Before failed")).when(beforeFunction).apply();
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
        verify(reporter).testOutcome("before", ERRORED);
    }

    @Test
    public void shouldRunAfterHookIfBeforeHookThrowsException() {

        //Given
        Function beforeFunction = mock(Function.class, "beforeFunction");
        Function afterFunction = mock(Function.class, "afterFunction");

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
    public void shouldSkipBeforeEachHookIfBeforeHookThrowsException() {

        //Given
        Function beforeFunction = mock(Function.class, "beforeFunction");
        Function beforeEachFunction = mock(Function.class, "beforeEachFunction");

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
    public void shouldSkipAfterEachHookIfBeforeHookThrowsException() {

        //Given
        Function beforeFunction = mock(Function.class, "beforeFunction");
        Function afterEachFunction = mock(Function.class, "afterEachFunction");

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
    public void shouldSkipTestsIfBeforeHookThrowsException() {

        //Given
        Function beforeFunction = mock(Function.class, "beforeFunction");
        Function testFunction = mock(Function.class, "testFunction");

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
    public void shouldSkipNestedBlocksIfBeforeThrowsException() {

        //Given
        Function topLevelBeforeFunction = mock(Function.class, "topLevelBeforeFunction");
        Function nestedBeforeFunction = mock(Function.class, "nestedBeforeFunction");
        Function nestedBeforeEachFunction = mock(Function.class, "nestedBeforeEachFunction");
        Function nestedAfterEachFunction = mock(Function.class, "nestedAfterEachFunction");
        Function nestedAfterFunction = mock(Function.class, "nestedAfterFunction");
        Function nestedTestFunction = mock(Function.class, "nestedTestFunction");

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
    public void shouldReturnSingleErrorResultIfBeforeEachHookThrowsException() {

        //Given
        Reporter reporter = mock(Reporter.class);
        Function beforeEachFunction = mock(Function.class, "beforeEachFunction");
        doThrow(new RuntimeException("Before each failed")).when(beforeEachFunction).apply();
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
        verify(reporter).testOutcome("beforeEach", ERRORED);
    }

    @Test
    public void shouldRunAfterHookIfBeforeEachHookThrowsException() {

        //Given
        Function beforeEachFunction = mock(Function.class, "beforeEachFunction");
        Function afterFunction = mock(Function.class, "afterFunction");

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
    public void shouldSkipAfterEachRunIfBeforeEachHookThrowsException() {

        //Given
        Function beforeEachFunction = mock(Function.class, "beforeEachFunction");
        Function afterEachFunction = mock(Function.class, "afterEachFunction");

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
    public void shouldSkipTestsIfBeforeEachHookThrowsException() {

        //Given
        Function beforeEachFunction = mock(Function.class, "beforeEachFunction");
        Function testFunction1 = mock(Function.class, "testFunction");
        Function testFunction2 = mock(Function.class, "testFunction");

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
    public void shouldSkipNestedBeforeEachAfterEachAndTestsIfBeforeEachThrowsException() {

        //Given
        Function topLevelBeforeEachFunction = mock(Function.class, "topLevelBeforeEachFunction");
        Function nestedBeforeEachFunction = mock(Function.class, "nestedBeforeEachFunction");
        Function nestedAfterEachFunction = mock(Function.class, "nestedAfterEachFunction");
        Function nestedTestFunction = mock(Function.class, "nestedTestFunction");

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
    public void shouldRunNestedBeforeAndAfterHooksIfBeforeEachThrowsException() {

        //Given
        Function topLevelBeforeEachFunction = mock(Function.class, "topLevelBeforeEachFunction");
        Function nestedBeforeFunction = mock(Function.class, "nestedBeforeFunction");
        Function nestedAfterFunction = mock(Function.class, "nestedAfterFunction");

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
    public void shouldSkipAllNestedBlocksIfTopLevelBeforeEachThrowsException() {

        //Given
        Function topLevelBeforeEachFunction = mock(Function.class, "topLevelBeforeEachFunction");
        Function nestedBeforeFunction = mock(Function.class, "nestedBeforeFunction");

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
    public void shouldHandleDoubleNestedBlocksIfTopLevelBeforeEachThrowsException() {

        //Given
        Function topLevelBeforeEachFunction = mock(Function.class, "topLevelBeforeEachFunction");
        Function nestedBeforeFunction = mock(Function.class, "nestedBeforeFunction");

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
    public void shouldReturnErrorResultInPlaceOfNestedBlocksIfBeforeHookThrowsException() {

        //Given
        Reporter reporter = mock(Reporter.class);
        Function beforeFunction = mock(Function.class, "beforeFunction");
        doThrow(new RuntimeException("Before failed")).when(beforeFunction).apply();
        {
            describe("before blocks", () -> {
                before(beforeFunction);
                it("does not run the first test", EMPTY_FUNCTION);
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome(anyString(), any(Reporter.Outcome.class));
        verify(reporter).testOutcome("before", ERRORED);
    }

    @Test
    public void shouldReturnAdditionalErrorResultIfAfterHookThrowsException() {

        //Given
        Reporter reporter = mock(Reporter.class);
        Function afterFunction = mock(Function.class, "afterFunction");
        doThrow(new RuntimeException("Before failed")).when(afterFunction).apply();
        {
            describe("after blocks", () -> {
                after(afterFunction);
                it("runs the first test", EMPTY_FUNCTION);
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome("after", ERRORED);
    }

    @Test
    public void shouldReturnErrorResultInPlaceOfTestsIfBeforeEachHookThrowsException() {

        //Given
        Reporter reporter = mock(Reporter.class);
        Function beforeEachFunction = mock(Function.class, "beforeEachFunction");
        doThrow(new RuntimeException("Before each failed")).when(beforeEachFunction).apply();
        {
            describe("beforeEach blocks", () -> {
                beforeEach(beforeEachFunction);
                it("does not run the test", EMPTY_FUNCTION);
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome(anyString(), any(Reporter.Outcome.class));
        verify(reporter).testOutcome("beforeEach", ERRORED);
    }

    @Test
    public void shouldReturnAdditionalErrorResultIfAfterEachHookThrowsException() {

        //Given
        Reporter reporter = mock(Reporter.class);
        Function afterEachFunction = mock(Function.class, "afterEachFunction");
        doThrow(new RuntimeException("After each failed")).when(afterEachFunction).apply();
        {
            describe("afterEach blocks", () -> {
                afterEach(afterEachFunction);
                it("runs the first test", EMPTY_FUNCTION);
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome("afterEach", ERRORED);
    }

    @Test
    public void shouldReturnErrorResultsIfBeforeEachAndAfterEachHookThrowsException() {

        //Given
        Reporter reporter = mock(Reporter.class);
        Function beforeEachFunction = mock(Function.class, "beforeEachFunction");
        doThrow(new RuntimeException("Before each failed")).when(beforeEachFunction).apply();
        Function afterEachFunction = mock(Function.class, "afterEachFunction");
        doThrow(new RuntimeException("After each failed")).when(afterEachFunction).apply();
        {
            describe("afterEach blocks", () -> {
                beforeEach(beforeEachFunction);
                afterEach(afterEachFunction);
                it("runs the first test", EMPTY_FUNCTION);
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome("beforeEach", ERRORED);
        verify(reporter).testOutcome("afterEach", ERRORED);
        verify(reporter, times(2)).testOutcome(anyString(), any(Reporter.Outcome.class));
    }

    @Test
    public void shouldSkipRemainingTestsIfAfterEachThrowsException() {

        // Given
        Function afterEachFunction = mock(Function.class, "afterEachFunction");
        Function testFunction1 = mock(Function.class, "testFunction1");
        Function testFunction2 = mock(Function.class, "testFunction2");

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
    public void shouldSkipRemainingBlocksIfAfterEachThrowsException() {

        // Given
        Function afterEachFunction = mock(Function.class, "afterEachFunction");
        Function testFunction1 = mock(Function.class, "testFunction1");
        Function testFunction2 = mock(Function.class, "testFunction2");

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
    public void shouldRunRemainingBlocksInOuterScopeIfNestedAfterEachThrowsException() {

        // Given
        Function afterEachFunction = mock(Function.class, "afterEachFunction");
        Function testFunction1 = mock(Function.class, "testFunction1");
        Function testFunction2 = mock(Function.class, "testFunction2");

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
    public void shouldRunRemainingBlocksInOuterScopeIfNestedAfterThrowsException() {

        // Given
        Function afterFunction = mock(Function.class, "afterFunction");
        Function testFunction1 = mock(Function.class, "testFunction1");
        Function testFunction2 = mock(Function.class, "testFunction2");

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
                .map(f -> (Function) () -> f.accept("", () -> it("", EMPTY_FUNCTION)))
                .map(f -> new Object[]{f})
                .iterator();
    }

    @Test(dataProvider = "testInHooks")
    public void addingTestsInHookShouldThrowException(Function hookWithTest) {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("", () -> {
                hookWithTest.apply();
                it("", EMPTY_FUNCTION);
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome(anyString(), eq(ERRORED));
    }

    @DataProvider
    private Iterator<Object[]> hooks() {
        return ALL_HOOKS.stream()
                .map(f -> (Function) () -> f.accept("", EMPTY_FUNCTION))
                .map(f -> new Object[]{f})
                .iterator();
    }

    @Test(dataProvider = "hooks")
    public void addingHookAtTopLevelShouldThrowException(Function hook) {
        assertThatThrownBy(hook::apply).hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test(dataProvider = "hooks")
    public void addingHookInTestShouldThrowException(Function hook) {

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
        verify(reporter).testOutcome("", ERRORED);
    }

    @DataProvider
    private Iterator<Object[]> hooksInHooks() {
        return ALL_HOOKS.stream()
                .flatMap(f ->
                        ALL_HOOKS.stream().map(g -> (Function) () -> f.accept("", () -> g.accept("", EMPTY_FUNCTION))))
                .map(f -> new Object[]{f})
                .iterator();
    }

    @Test(dataProvider = "hooksInHooks")
    public void addingNestedHookInHookShouldThrowException(Function nestedHook) {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("", () -> {
                nestedHook.apply();
                it("", EMPTY_FUNCTION);
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome(anyString(), eq(ERRORED));
    }
}
