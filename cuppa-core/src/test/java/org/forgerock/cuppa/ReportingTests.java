package org.forgerock.cuppa;

import static org.forgerock.cuppa.Cuppa.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ReportingTests {

    @BeforeMethod
    public void setup() {
        Cuppa.reset();
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
        Cuppa.runTests(reporter);

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
        Cuppa.runTests(reporter);

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
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testPass("test");
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
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testFail("test", assertionError);
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
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testError("test", exception);
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
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).describeStart("describe");
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
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).describeEnd("describe");
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
        Cuppa.runTests(reporter);

        //Then
        InOrder inOrder = inOrder(reporter);
        inOrder.verify(reporter).start();
        inOrder.verify(reporter).describeStart("describe");
        inOrder.verify(reporter).describeStart("when");
        inOrder.verify(reporter).testPass("test");
        inOrder.verify(reporter).describeEnd("when");
        inOrder.verify(reporter).describeEnd("describe");
        inOrder.verify(reporter).end();
    }
}
