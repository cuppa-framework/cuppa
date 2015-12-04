package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.forgerock.cuppa.Cuppa.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        AtomicInteger hasTestFunctionRun = new AtomicInteger();
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("runs the test", hasTestFunctionRun::incrementAndGet);
                });
            });
        }

        //When
        TestResults results = Cuppa.runTests();

        //Then
        assertThat(hasTestFunctionRun.get()).isEqualTo(1);
        assertTestResources(results, 1, 0, 0);
    }

    @Test
    public void basicApiUsageWithMultipleDescribeWhenAndTestBlocks() {

        //Given
        List<String> testFunctionRuns = new ArrayList<>();
        {
            describe("basic API usage with multiple blocks", () -> {
                when("the first 'when' block is run", () -> {
                    it("runs the first test", () -> {
                        testFunctionRuns.add("A");
                    });
                    it("runs the second test", () -> {
                        testFunctionRuns.add("B");
                    });
                });
                when("the second 'when' block is run", () -> {
                    it("runs the third test", () -> {
                        testFunctionRuns.add("C");
                    });
                    it("runs the fourth test", () -> {
                        testFunctionRuns.add("D");
                    });
                });
            });
            describe("second basic API usage with multiple blocks", () -> {
                when("the third 'when' block is run", () -> {
                    it("runs the fifth test", () -> {
                        testFunctionRuns.add("E");
                    });
                    it("runs the sixth test", () -> {
                        testFunctionRuns.add("F");
                    });
                });
                when("the fourth 'when' block is run", () -> {
                    it("runs the seventh test", () -> {
                        testFunctionRuns.add("G");
                    });
                    it("runs the eighth test", () -> {
                        testFunctionRuns.add("H");
                    });
                });
            });
        }

        //When
        TestResults results = Cuppa.runTests();

        //Then
        assertThat(testFunctionRuns).containsExactly("A", "B", "C", "D", "E", "F", "G", "H");
        assertTestResources(results, 8, 0, 0);
    }

    @Test
    public void basicApiUsageWithNestedDescribeBlock() {

        //Given
        AtomicInteger hasTestFunctionRun = new AtomicInteger();
        {
            describe("basic API usage", () -> {
                describe("nested describe", () -> {
                    when("the 'when' block is run", () -> {
                        it("runs the test", hasTestFunctionRun::incrementAndGet);
                    });
                });
            });
        }

        //When
        TestResults results = Cuppa.runTests();

        //Then
        assertThat(hasTestFunctionRun.get()).isEqualTo(1);
        assertTestResources(results, 1, 0, 0);
    }

    @Test
    public void basicApiUsageWithNestedDescribeInWhenBlock() {

        //Given
        AtomicInteger hasTestFunctionRun = new AtomicInteger();
        {
            describe("basic API usage", () -> {
                when("the top level 'when' block is run", () -> {
                    when("the nested 'when' block is run", () -> {
                        it("runs the test", hasTestFunctionRun::incrementAndGet);
                    });
                });
            });
        }

        //When
        TestResults results = Cuppa.runTests();

        //Then
        assertThat(hasTestFunctionRun.get()).isEqualTo(1);
        assertTestResources(results, 1, 0, 0);
    }

    @Test
    public void basicApiUsageShouldThrowErrorWithTopLevelWhenBlock() {

        //Given
        AtomicInteger hasWhenFunctionRun = new AtomicInteger();

        //When/Then
        assertThatThrownBy(() -> when("basic API usage", hasWhenFunctionRun::incrementAndGet))
                .hasCauseInstanceOf(IllegalStateException.class);
        assertThat(hasWhenFunctionRun.get()).isEqualTo(0);
    }

    @Test
    public void basicApiUsageShouldThrowErrorWithTopLevelItBlock() {

        //Given
        AtomicInteger hasTestFunctionRun = new AtomicInteger();

        //When/Then
        assertThatThrownBy(() -> it("basic API usage", hasTestFunctionRun::incrementAndGet))
                .hasCauseInstanceOf(IllegalStateException.class);
        assertThat(hasTestFunctionRun.get()).isEqualTo(0);
    }

    @Test
    public void basicApiUsageShouldReportTestErrorWithDescribeBlockNestedUnderItBlock() {

        //Given
        AtomicInteger hasInvalidUseOfDescribeFunctionRun = new AtomicInteger();
        {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the test, which errors", () -> {
                        describe("invalid use of 'describe'", hasInvalidUseOfDescribeFunctionRun::incrementAndGet);
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
        AtomicInteger hasInvalidUseOfWhenFunctionRun = new AtomicInteger();
        {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the test, which errors", () -> {
                        when("invalid use of 'when'", hasInvalidUseOfWhenFunctionRun::incrementAndGet);
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
        AtomicInteger hasInvalidUseOfDescribeFunctionRun = new AtomicInteger();
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("runs the first test, which fails", () -> {
                        describe("invalid use of 'describe'", hasInvalidUseOfDescribeFunctionRun::incrementAndGet);
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

    private void assertTestResources(TestResults results, int passed, int failed, int errored) {
        assertThat(results.getPassedTestsCount()).isEqualTo(passed);
        assertThat(results.getFailedTestsCount()).isEqualTo(failed);
        assertThat(results.getErroredTestsCount()).isEqualTo(errored);
    }
}
