package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.forgerock.cuppa.Cuppa.*;
import static org.forgerock.cuppa.Cuppa.when;
import static org.forgerock.cuppa.Reporter.Outcome.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.stream.Stream;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BasicApiTests {

    @BeforeMethod
    public void setup() {
        Cuppa.reset();
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
        Cuppa.runTests(mock(Reporter.class));

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
        Cuppa.runTests(mock(Reporter.class));

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
        Cuppa.runTests(mock(Reporter.class));

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
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verify(testFunction).apply();
    }

    @Test
    public void basicApiUsageShouldThrowErrorWithTopLevelWhenBlock() throws Exception {

        //Given
        TestDefinitionFunction whenFunction = mock(TestDefinitionFunction.class);

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
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome("runs the test, which errors", ERRORED);
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
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome("runs the test, which errors", ERRORED);
    }

    @Test
    public void basicApiUsageShouldRunTestsAfterErroredTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("runs the first test, which errors", () -> {
                        describe("invalid use of 'describe'", () -> {

                        });
                    });
                    it("runs the second test, which passes", () -> {
                        assertThat(true).isTrue();
                    });
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome("runs the first test, which errors", ERRORED);
        verify(reporter).testOutcome("runs the second test, which passes", PASSED);
    }

    @Test
    public void basicApiUsageWithSingleFailingTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the test, which fails", () -> {
                        assertThat(true).isFalse();
                    });
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome("runs the test, which fails", FAILED);
    }

    @Test
    public void basicApiUsageWithSingleErroredTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the test, which errors", () -> {
                        throw new RuntimeException();
                    });
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome("runs the test, which errors", ERRORED);
    }

    @Test
    public void basicApiUsageWithPassingFailingAndErroredTests() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the first test, which errors", () -> {
                        throw new RuntimeException();
                    });
                    it("runs the second test, which fails", () -> {
                        assertThat(true).isFalse();
                    });
                    it("runs the third test, which passes", () -> {
                        assertThat(true).isTrue();
                    });
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome("runs the first test, which errors", ERRORED);
        verify(reporter).testOutcome("runs the second test, which fails", FAILED);
        verify(reporter).testOutcome("runs the third test, which passes", PASSED);
    }

    @Test
    public void aTestShouldBeAbleToThrowACheckedException() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("runs the test", () -> {
                        throw new Exception();
                    });
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        verify(reporter).testOutcome("runs the test", ERRORED);
    }
}
