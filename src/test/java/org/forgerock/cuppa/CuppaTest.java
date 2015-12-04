package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.forgerock.cuppa.Cuppa.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.stream.Stream;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CuppaTest {

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

    @Test
    public void beforeShouldRunOnceBeforeTests() {

        //Given
        Function topLevelBeforeFunction = mock(Function.class);
        Function nestedBeforeFunction = mock(Function.class);
        {
            describe("before blocks", () -> {
                before("running any tests", topLevelBeforeFunction);
                when("the first 'when' block is run", () -> {
                    before(nestedBeforeFunction);
                    it("runs the first test", () -> {
                    });
                    it("runs the second test", () -> {
                    });
                });
                when("the second 'when' block is run", () -> {
                    it("runs the third test", () -> {
                    });
                });
            });
        }

        //When
        TestResults results = Cuppa.runTests();

        //Then
        verify(topLevelBeforeFunction).apply();
        verify(nestedBeforeFunction).apply();
        assertTestResources(results, 3, 0, 0);
    }

    @Test
    public void afterShouldRunOnceAfterTests() {

        //Given
        Function topLevelAfterFunction = mock(Function.class);
        Function nestedAfterFunction = mock(Function.class);
        {
            describe("after blocks", () -> {
                after("running any tests", topLevelAfterFunction);
                when("the first 'when' block is run", () -> {
                    after(nestedAfterFunction);
                    it("runs the first test", () -> {
                    });
                    it("runs the second test", () -> {
                    });
                });
                when("the second 'when' block is run", () -> {
                    it("runs the third test", () -> {
                    });
                });
            });
        }

        //When
        TestResults results = Cuppa.runTests();

        //Then
        verify(topLevelAfterFunction).apply();
        verify(nestedAfterFunction).apply();
        assertTestResources(results, 3, 0, 0);
    }

    @Test
    public void beforeEachShouldRunBeforeEachTest() {

        //Given
        Function topLevelBeforeEachFunction = mock(Function.class);
        Function nestedBeforeEachFunction = mock(Function.class);
        {
            describe("beforeEach blocks", () -> {
                beforeEach("running each test", topLevelBeforeEachFunction);
                when("the first 'when' block is run", () -> {
                    beforeEach(nestedBeforeEachFunction);
                    it("runs the first test", () -> {
                    });
                    it("runs the second test", () -> {
                    });
                });
                when("the second 'when' block is run", () -> {
                    it("runs the third test", () -> {
                    });
                });
            });
        }

        //When
        TestResults results = Cuppa.runTests();

        //Then
        verify(topLevelBeforeEachFunction, times(3)).apply();
        verify(nestedBeforeEachFunction, times(2)).apply();
        assertTestResources(results, 3, 0, 0);
    }

    @Test
    public void afterEachShouldRunAfterEachTest() {

        //Given
        Function topLevelAfterEachFunction = mock(Function.class);
        Function nestedAfterEachFunction = mock(Function.class);
        {
            describe("afterEach blocks", () -> {
                afterEach("running each test", topLevelAfterEachFunction);
                when("the first 'when' block is run", () -> {
                    afterEach(nestedAfterEachFunction);
                    it("runs the first test", () -> {
                    });
                    it("runs the second test", () -> {
                    });
                });
                when("the second 'when' block is run", () -> {
                    it("runs the third test", () -> {
                    });
                });
            });
        }

        //When
        TestResults results = Cuppa.runTests();

        //Then
        verify(topLevelAfterEachFunction, times(3)).apply();
        verify(nestedAfterEachFunction, times(2)).apply();
        assertTestResources(results, 3, 0, 0);
    }

    private void assertTestResources(TestResults results, int passed, int failed, int errored) {
        assertThat(results.getPassedTestsCount()).isEqualTo(passed);
        assertThat(results.getFailedTestsCount()).isEqualTo(failed);
        assertThat(results.getErroredTestsCount()).isEqualTo(errored);
    }
}
