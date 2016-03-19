/*
 * Copyright 2015-2016 ForgeRock AS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.forgerock.cuppa.Cuppa.*;
import static org.forgerock.cuppa.TestCuppaSupport.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.stream.Stream;

import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

public class BasicApiTests {

    private TestBlock testBlock;

    @Test
    public void basicApiUsageWithSingleTestBlock() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("runs the test", testFunction);
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(testFunction).apply();
    }

    @Test
    public void basicApiUsageWithMultipleDescribeWhenAndTestBlocks() throws Exception {

        //Given
        TestFunction[] testFunctions = Stream.generate(() -> mock(TestFunction.class))
                .limit(8)
                .toArray(TestFunction[]::new);

        TestBlock rootBlock = defineTests(() -> {
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
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

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
        TestBlock rootBlock = defineTests(() -> {
            describe("basic API usage", () -> {
                describe("nested describe", () -> {
                    when("the 'when' block is run", () -> {
                        it("runs the test", testFunction);
                    });
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(testFunction).apply();
    }

    @Test
    public void basicApiUsageWithNestedDescribeInWhenBlock() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("basic API usage", () -> {
                when("the top level 'when' block is run", () -> {
                    when("the nested 'when' block is run", () -> {
                        it("runs the test", testFunction);
                    });
                });
            });
        });

        //When
        runTests(rootBlock, mock(Reporter.class));

        //Then
        verify(testFunction).apply();
    }

    @Test
    public void basicApiUsageShouldThrowErrorWithTopLevelItBlock() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);

        //When/Then
        assertThatThrownBy(() -> defineTests(() -> it("basic API usage", testFunction)))
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'it' must be nested within a 'describe' or 'when' block");
        verify(testFunction, never()).apply();
    }

    @Test
    public void basicApiUsageShouldReportTestFailureWithDescribeBlockNestedUnderItBlock() {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the test, which errors", () -> {
                        describe("invalid use of 'describe'", () -> {
                        });
                    });
                });
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(reporter).testFail(eq(findTest(rootBlock, "runs the test, which errors")), anyListOf(TestBlock.class),
                captor.capture());
        assertThat(captor.getValue())
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'describe' may only be nested within a 'describe' or 'when' block");
    }

    @Test
    public void basicApiUsageShouldReportTestFailureWithWhenNestedUnderItBlock() {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the test, which errors", () -> {
                        when("invalid use of 'when'", () -> {
                        });
                    });
                });
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(reporter).testFail(eq(findTest(rootBlock, "runs the test, which errors")), anyListOf(TestBlock.class),
                captor.capture());
        assertThat(captor.getValue())
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'when' may only be nested within a 'describe' or 'when' block");
    }

    @Test
    public void basicApiUsageShouldReportTestFailureWithTestNestedUnderItBlock() {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the test, which errors", () -> {
                        it("invalid use of 'it'", () -> {
                        });
                    });
                });
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(reporter).testFail(eq(findTest(rootBlock, "runs the test, which errors")), anyListOf(TestBlock.class),
                captor.capture());
        assertThat(captor.getValue())
                .isExactlyInstanceOf(CuppaException.class)
                .hasMessage("'it' may only be nested within a 'describe' or 'when' block");
    }

    @Test
    public void basicApiUsageShouldRunTestsAfterFailedTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
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
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).testFail(eq(findTest(rootBlock, "runs the first test, which errors")),
                anyListOf(TestBlock.class), isA(Throwable.class));
        verify(reporter).testPass(eq(findTest(rootBlock, "runs the second test, which passes")),
                anyListOf(TestBlock.class));
    }

    @Test
    public void basicApiUsageWithSingleFailingTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        RuntimeException exception = new RuntimeException();
        TestBlock rootBlock = defineTests(() -> {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the test, which fails", () -> {
                        throw exception;
                    });
                });
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).testFail(eq(findTest(rootBlock, "runs the test, which fails")), anyListOf(TestBlock.class),
                eq(exception));
    }

    @Test
    public void basicApiUsageWithPassingAndFailingTests() {

        //Given
        Reporter reporter = mock(Reporter.class);
        RuntimeException exception = new RuntimeException();
        TestBlock rootBlock = defineTests(() -> {
            describe("basic API usage", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the first test, which fails", () -> {
                        throw exception;
                    });
                    it("runs the second test, which passes", TestFunction.identity());
                });
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).testFail(eq(findTest(rootBlock, "runs the first test, which fails")),
                anyListOf(TestBlock.class), any(Throwable.class));
        verify(reporter).testPass(eq(findTest(rootBlock, "runs the second test, which passes")),
                anyListOf(TestBlock.class));
    }

    @Test
    public void aTestShouldBeAbleToThrowACheckedException() {

        //Given
        Reporter reporter = mock(Reporter.class);
        Exception exception = new Exception();
        TestBlock rootBlock = defineTests(() -> {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("runs the test", () -> {
                        throw exception;
                    });
                });
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).testFail(eq(findTest(rootBlock, "runs the test")), anyListOf(TestBlock.class), eq(exception));
    }

    @Test
    public void shouldThrowWhenCallingCuppaMethodsOutsideOfTestDefinitionContext() {
        assertThatThrownBy(() -> {
            describe("something", () -> {
            });
        }).isExactlyInstanceOf(CuppaException.class).hasMessage("Attempted to defined Cuppa tests from outside of"
                + " Cuppa's control. Is something else instantiating your test class?");
    }

    /**
     * This test ensures that Cuppa can be tested by tests written in Cuppa.
     */
    @Test
    public void shouldAllowNestedTestDefinitions() {
        TestBlock rootBlock = defineTests(() -> {
            describe("basic API usage", () -> {
                when("testing cuppa from cuppa tests", () -> {
                    it("does not throw", () -> {
                        testBlock = defineTests(() -> {
                            describe("Cuppa", () -> {
                                it("keeps the tests segregated");
                            });
                        });
                    });
                });
            });
        });
        runTests(rootBlock, mock(Reporter.class));
        assertThat(testBlock).isNotNull();
    }
}
