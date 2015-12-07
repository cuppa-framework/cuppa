package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.forgerock.cuppa.Assertions.assertTestResources;
import static org.forgerock.cuppa.Cuppa.*;
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
    public void basicApiUsageWithSingleTestBlock() {

        //Given
        Function testFunction = mock(Function.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("runs the test", testFunction);
                });
            });
        }

        //When
        TestResults results = Cuppa.runTests();

        //Then
        verify(testFunction).apply();
        assertTestResources(results, 1, 0, 0);
    }

    @Test
    public void basicApiUsageWithMultipleDescribeWhenAndTestBlocks() {

        //Given
        Function[] testFunctions = Stream.generate(() -> mock(Function.class)).limit(8).toArray(Function[]::new);
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
        TestResults results = Cuppa.runTests();

        //Then
        Arrays.stream(testFunctions).forEach((f) -> verify(f).apply());
        assertTestResources(results, 8, 0, 0);
    }

    @Test
    public void basicApiUsageWithNestedDescribeBlock() {

        //Given
        Function testFunction = mock(Function.class);
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
        TestResults results = Cuppa.runTests();

        //Then
        verify(testFunction).apply();
        assertTestResources(results, 1, 0, 0);
    }

    @Test
    public void basicApiUsageWithNestedDescribeInWhenBlock() {

        //Given
        Function testFunction = mock(Function.class);
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
        TestResults results = Cuppa.runTests();

        //Then
        verify(testFunction).apply();
        assertTestResources(results, 1, 0, 0);
    }

    @Test
    public void basicApiUsageShouldThrowErrorWithTopLevelWhenBlock() {

        //Given
        Function whenFunction = mock(Function.class);

        //When/Then
        assertThatThrownBy(() -> when("basic API usage", whenFunction))
                .hasCauseInstanceOf(IllegalStateException.class);
        verify(whenFunction, never()).apply();
    }

    @Test
    public void basicApiUsageShouldThrowErrorWithTopLevelItBlock() {

        //Given
        Function testFunction = mock(Function.class);

        //When/Then
        assertThatThrownBy(() -> it("basic API usage", testFunction))
                .hasCauseInstanceOf(IllegalStateException.class);
        verify(testFunction, never()).apply();
    }

    @Test
    public void basicApiUsageShouldReportTestErrorWithDescribeBlockNestedUnderItBlock() {

        //Given
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
        TestResults results = Cuppa.runTests();

        //Then
        assertTestResources(results, 0, 0, 1);
    }

    @Test
    public void basicApiUsageShouldReportTestErrorWithWhenNestedUnderItBlock() {

        //Given
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
        TestResults results = Cuppa.runTests();

        //Then
        assertTestResources(results, 0, 0, 1);
    }

    @Test
    public void basicApiUsageShouldRunTestsAfterErroredTest() {

        //Given
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("runs the first test, which fails", () -> {
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
        TestResults results = Cuppa.runTests();

        //Then
        assertTestResources(results, 1, 0, 1);
    }

    @Test
    public void basicApiUsageWithSingleFailingTest() {

        //Given
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
        TestResults results = Cuppa.runTests();

        //Then
        assertTestResources(results, 0, 1, 0);
    }

    @Test
    public void basicApiUsageWithSingleErroredTest() {

        //Given
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
        TestResults results = Cuppa.runTests();

        //Then
        assertTestResources(results, 0, 0, 1);
    }

    @Test
    public void basicApiUsageWithPassingFailingAndErroredTests() {

        //Given
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
        TestResults results = Cuppa.runTests();

        //Then
        assertTestResources(results, 1, 1, 1);
    }
}
