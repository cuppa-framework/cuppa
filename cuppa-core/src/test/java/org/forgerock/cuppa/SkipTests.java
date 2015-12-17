package org.forgerock.cuppa;

import static org.forgerock.cuppa.Cuppa.*;
import static org.forgerock.cuppa.Cuppa.when;
import static org.forgerock.cuppa.CuppaTestProvider.runTests;
import static org.forgerock.cuppa.ModelFinder.findTest;
import static org.forgerock.cuppa.model.Behaviour.ONLY;
import static org.forgerock.cuppa.model.Behaviour.SKIP;
import static org.mockito.Mockito.*;

import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.internal.TestContainer;
import org.forgerock.cuppa.reporters.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SkipTests {

    @BeforeMethod
    public void setup() {
        TestContainer.INSTANCE.reset();
    }

    @Test
    public void shouldSkipTestIfTestIsMarkedSkip() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it(SKIP, "test", testFunction);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunction, never()).apply();
    }

    @Test
    public void shouldReportTestSkippedIfTestIsMarkedSkip() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testFunction = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it(SKIP, "test", testFunction);
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testSkip(findTest("test"));
    }

    @Test
    public void shouldSkipTestsNestedInSkippedBlock() throws Exception {

        //Given
        TestFunction testFunction1 = mock(TestFunction.class);
        TestFunction testFunction2 = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when(SKIP, "the 'when' is run", () -> {
                    it("runs the test", testFunction1);
                    it("runs the test", testFunction2);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunction1, never()).apply();
        verify(testFunction2, never()).apply();
    }

    @Test
    public void shouldReportAllSkippedTestsNestedInSkippedBlock() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testFunction1 = mock(TestFunction.class);
        TestFunction testFunction2 = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when(SKIP, "the 'when' is run", () -> {
                    it("test1", testFunction1);
                    it("test2", testFunction2);
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testSkip(findTest("test1"));
        verify(reporter).testSkip(findTest("test2"));
    }

    @Test
    public void shouldIgnoreTestsIfOtherTestIsMarkedOnly() throws Exception {

        //Given
        TestFunction testFunctionBefore = mock(TestFunction.class);
        TestFunction testFunction = mock(TestFunction.class);
        TestFunction testFunctionAfter = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("before test", testFunctionBefore);
                    it(ONLY, "test", testFunction);
                    it("after test", testFunctionAfter);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunctionBefore, never()).apply();
        verify(testFunction).apply();
        verify(testFunctionAfter, never()).apply();
    }

    @Test
    public void shouldNotReportTestsIfOtherTestIsMarkedOnly() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("before test", TestFunction.identity());
                    it(ONLY, "test", TestFunction.identity());
                    it("after test", TestFunction.identity());
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testPass(any());
        verify(reporter).testPass(findTest("test"));
    }

    @Test
    public void shouldRunAllTestsMarkedOnly() throws Exception {

        //Given
        TestFunction testFunctionOnly1 = mock(TestFunction.class);
        TestFunction testFunctionOnly2 = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("before test", TestFunction.identity());
                    it(ONLY, "only test 1", testFunctionOnly1);
                    it(ONLY, "only test 2", testFunctionOnly2);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunctionOnly1).apply();
        verify(testFunctionOnly2).apply();
    }

    @Test
    public void shouldRunTestsInBlockMarkedOnly() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        TestFunction testFunctionAfter = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when(ONLY, "only when", () -> {
                    it("test", testFunction);
                });
                when("after when", () -> {
                    it("test", testFunctionAfter);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunction).apply();
        verify(testFunctionAfter, never()).apply();
    }

    @Test
    public void shouldSkipTestsMarkedSkipInBlockMarkedOnly() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when(ONLY, "only when", () -> {
                    it(SKIP, "test", testFunction);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunction, never()).apply();
    }

    @Test
    public void shouldSkipTestsMarkedOnlyInBlockMarkedSkip() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when(SKIP, "only when", () -> {
                    it(ONLY, "test", testFunction);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunction, never()).apply();
    }

    @Test
    public void shouldIgnoreTestIfOtherTestIsMarkedOnlyInSkipBlock() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when(SKIP, "only when", () -> {
                    it(ONLY, "test", TestFunction.identity());
                });
                it("test 2", testFunction);
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunction, never()).apply();
    }
}
