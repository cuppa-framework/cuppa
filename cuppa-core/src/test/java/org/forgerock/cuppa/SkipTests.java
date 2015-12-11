package org.forgerock.cuppa;

import static org.forgerock.cuppa.Behaviour.SKIP;
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.it;
import static org.forgerock.cuppa.Cuppa.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SkipTests {

    @BeforeMethod
    public void setup() {
        Cuppa.reset();
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
        Cuppa.runTests(mock(Reporter.class));

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
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testSkip("test");
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
        Cuppa.runTests(mock(Reporter.class));

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
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testSkip("test1");
        verify(reporter).testSkip("test2");
    }
}
