package org.forgerock.cuppa;

import static org.forgerock.cuppa.Cuppa.*;
import static org.forgerock.cuppa.Cuppa.when;
import static org.forgerock.cuppa.CuppaTestProvider.runTests;
import static org.forgerock.cuppa.ModelFinder.findTest;
import static org.forgerock.cuppa.ModelFinder.findTestBlock;
import static org.mockito.Mockito.*;

import org.forgerock.cuppa.internal.TestContainer;
import org.forgerock.cuppa.reporters.Reporter;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ReportingTests {

    @BeforeMethod
    public void setup() {
        TestContainer.INSTANCE.reset();
    }

    @Test
    public void reporterShouldBeNotifiedAtTheStart() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).start();
    }

    @Test
    public void reporterShouldBeNotifiedAtTheEnd() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).end();
    }

    @Test
    public void reporterShouldBeNotifiedOfPassingTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testPass(findTest("test"));
    }

    @Test
    public void reporterShouldBeNotifiedOfFailingTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        AssertionError assertionError = new AssertionError();
        {
            describe("describe", () -> {
                it("test", () -> {
                    throw assertionError;
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testFail(findTest("test"), assertionError);
    }

    @Test
    public void reporterShouldBeNotifiedOfErroredTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        IllegalStateException exception = new IllegalStateException();
        {
            describe("describe", () -> {
                it("test", () -> {
                    throw exception;
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testError(findTest("test"), exception);
    }

    @Test
    public void reporterShouldBeNotifiedOfStartOfDescribe() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).describeStart(findTestBlock("describe"));
    }

    @Test
    public void reporterShouldBeNotifiedOfEndOfDescribe() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).describeEnd(findTestBlock("describe"));
    }

    @Test
    public void reporterShouldBeNotifiedInTheCorrectOrder() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("describe", () -> {
                when("when", () -> {
                    it("test", () -> {
                    });
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        InOrder inOrder = inOrder(reporter);
        inOrder.verify(reporter).start();
        inOrder.verify(reporter).describeStart(findTestBlock("describe"));
        inOrder.verify(reporter).describeStart(findTestBlock("when"));
        inOrder.verify(reporter).testPass(findTest("test"));
        inOrder.verify(reporter).describeEnd(findTestBlock("when"));
        inOrder.verify(reporter).describeEnd(findTestBlock("describe"));
        inOrder.verify(reporter).end();
    }
}
