package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.forgerock.cuppa.Cuppa.*;
import static org.forgerock.cuppa.Cuppa.when;
import static org.forgerock.cuppa.CuppaTestProvider.runTests;
import static org.forgerock.cuppa.ModelFinder.findTest;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.stream.Stream;

import org.forgerock.cuppa.functions.TestBlockFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.internal.TestContainer;
import org.forgerock.cuppa.reporters.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BasicApiTests {

    @BeforeMethod
    public void setup() {
        TestContainer.INSTANCE.reset();
    }

    @Test
    public void basicApiUsageWithSingleTestBlock() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("runs the test", testFunction);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunction).apply();
    }

    @Test
    public void basicApiUsageWithMultipleDescribeWhenAndTestBlocks() throws Exception {

        //Given
        TestFunction[] testFunctions = Stream.generate(() -> mock(TestFunction.class))
                .limit(8)
                .toArray(TestFunction[]::new);

        {
            describe("basic API usage with multiple blocks", () -> {
                when("the first 'when' block is run", () -> {
                    it("runs the first test", testFunctions[0]);
                    it("runs the second test", testFunctions[1]);
                });
                when("the second 'when' block is run", () -> {
                    it("runs the third test", testFunctions[2]);
                    it("runs the fourth test", testFunctions[3]);
                });
            });
            describe("second basic API usage with multiple blocks", () -> {
                when("the third 'when' block is run", () -> {
                    it("runs the fifth test", testFunctions[4]);
                    it("runs the sixth test", testFunctions[5]);
                });
                when("the fourth 'when' block is run", () -> {
                    it("runs the seventh test", testFunctions[6]);
                    it("runs the eighth test", testFunctions[7]);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        Arrays.stream(testFunctions).forEach((f) -> {
            try {
                verify(f).apply();
            } catch (Exception ignored) {
            }
        });
    }

    @Test
    public void basicApiUsageWithNestedDescribeBlock() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                describe("nested describe", () -> {
                    when("the 'when' block is run", () -> {
                        it("runs the test", testFunction);
                    });
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunction).apply();
    }

    @Test
    public void basicApiUsageWithNestedDescribeInWhenBlock() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when("the top level 'when' block is run", () -> {
                    when("the nested 'when' block is run", () -> {
                        it("runs the test", testFunction);
                    });
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunction).apply();
    }

    @Test
    public void basicApiUsageShouldThrowErrorWithTopLevelWhenBlock() throws Exception {

        //Given
        TestBlockFunction whenFunction = mock(TestBlockFunction.class);

        //When/Then
        assertThatThrownBy(() -> when("basic API usage", whenFunction))
                .hasCauseInstanceOf(IllegalStateException.class);
        verify(whenFunction, never()).apply();
    }

    @Test
    public void basicApiUsageShouldThrowErrorWithTopLevelItBlock() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);

        //When/Then
        assertThatThrownBy(() -> it("basic API usage", testFunction))
                .hasCauseInstanceOf(IllegalStateException.class);
        verify(testFunction, never()).apply();
    }

    @Test
    public void basicApiUsageShouldReportTestErrorWithDescribeBlockNestedUnderItBlock() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the test, which errors", () -> {
                        describe("invalid use of 'describe'", () -> {
                        });
                    });
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testError(eq(findTest("runs the test, which errors")), isA(CuppaException.class));
    }

    @Test
    public void basicApiUsageShouldReportTestErrorWithWhenNestedUnderItBlock() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the test, which errors", () -> {
                        when("invalid use of 'when'", () -> {
                        });
                    });
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testError(eq(findTest("runs the test, which errors")), isA(CuppaException.class));
    }

    @Test
    public void basicApiUsageShouldRunTestsAfterErroredTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("runs the first test, which errors", () -> {
                        throw new RuntimeException();
                    });
                    it("runs the second test, which passes", () -> {
                        assertThat(true).isTrue();
                    });
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testError(eq(findTest("runs the first test, which errors")), isA(Throwable.class));
        verify(reporter).testPass(findTest("runs the second test, which passes"));
    }

    @Test
    public void basicApiUsageWithSingleFailingTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        AssertionError assertionError = new AssertionError();
        {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the test, which fails", () -> {
                        throw assertionError;
                    });
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testFail(eq(findTest("runs the test, which fails")), any(AssertionError.class));
    }

    @Test
    public void basicApiUsageWithSingleErroredTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        IllegalStateException exception = new IllegalStateException();
        {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the test, which errors", () -> {
                        throw exception;
                    });
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testError(findTest("runs the test, which errors"), exception);
    }

    @Test
    public void basicApiUsageWithPassingFailingAndErroredTests() {

        //Given
        Reporter reporter = mock(Reporter.class);
        RuntimeException exception = new RuntimeException();
        AssertionError assertionError = new AssertionError();
        {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the first test, which errors", () -> {
                        throw exception;
                    });
                    it("runs the second test, which fails", () -> {
                        throw assertionError;
                    });
                    it("runs the third test, which passes", TestFunction.identity());
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testError(eq(findTest("runs the first test, which errors")), any(Throwable.class));
        verify(reporter).testFail(eq(findTest("runs the second test, which fails")), any(AssertionError.class));
        verify(reporter).testPass(findTest("runs the third test, which passes"));
    }

    @Test
    public void aTestShouldBeAbleToThrowACheckedException() {

        //Given
        Reporter reporter = mock(Reporter.class);
        Exception exception = new Exception();
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("runs the test", () -> {
                        throw exception;
                    });
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testError(findTest("runs the test"), exception);
    }
}
