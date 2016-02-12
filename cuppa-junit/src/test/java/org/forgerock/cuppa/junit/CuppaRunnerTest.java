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

package org.forgerock.cuppa.junit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.forgerock.cuppa.Cuppa.*;
import static org.forgerock.cuppa.model.Behaviour.skip;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.internal.TestContainer;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CuppaRunnerTest {

    Description suiteDescription;

    @BeforeMethod
    public void setup() {
        TestContainer.INSTANCE.reset();
        TestContainer.INSTANCE.setTestClass(CuppaRunnerTest.class);
    }

    @Test
    public void shouldReportPassingTest() {

        //When
        Result result = JUnitCore.runClasses(CuppaRunnerTest.PassingTest.class);

        //Then
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(result.getRunCount()).isEqualTo(1);
        assertThat(result.getIgnoreCount()).isEqualTo(0);
        assertThat(result.getFailureCount()).isEqualTo(0);
    }

    @Test
    public void shouldReportFailingTest() {

        //When
        Result result = JUnitCore.runClasses(CuppaRunnerTest.FailingTest.class);

        //Then
        assertThat(result.wasSuccessful()).isFalse();
        assertThat(result.getRunCount()).isEqualTo(1);
        assertThat(result.getIgnoreCount()).isEqualTo(0);
        assertThat(result.getFailureCount()).isEqualTo(1);
    }

    @Test
    public void shouldReportFailingTestDescription() {

        //When
        Result result = JUnitCore.runClasses(CuppaRunnerTest.FailingTest.class);

        //Then
        assertThat(result.getFailures()).hasSize(1);
        Failure failure = result.getFailures().get(0);
        assertThat(failure.getDescription().getDisplayName())
                .isEqualTo("shows a failing test as failing(org.forgerock.cuppa.junit.CuppaRunnerTest$FailingTest)");
    }

    @Test
    public void shouldReportErroringTest() {

        //When
        Result result = JUnitCore.runClasses(CuppaRunnerTest.ErroringTest.class);

        //Then
        assertThat(result.wasSuccessful()).isFalse();
        assertThat(result.getRunCount()).isEqualTo(1);
        assertThat(result.getIgnoreCount()).isEqualTo(0);
        assertThat(result.getFailureCount()).isEqualTo(1);
    }

    @Test
    public void shouldReportErroringTestDescription() {

        //When
        Result result = JUnitCore.runClasses(CuppaRunnerTest.ErroringTest.class);

        //Then
        assertThat(result.getFailures()).hasSize(1);
        Failure failure = result.getFailures().get(0);
        assertThat(failure.getDescription().getDisplayName())
                .isEqualTo("erroring test(org.forgerock.cuppa.junit.CuppaRunnerTest$ErroringTest)");
        assertThat(failure.getException()).hasMessage("Test is bad").isInstanceOf(RuntimeException.class);
    }

    @Test
    public void shouldReportPendingTest() {

        //When
        Result result = JUnitCore.runClasses(CuppaRunnerTest.PendingTest.class);

        //Then
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(result.getRunCount()).isEqualTo(0);
        assertThat(result.getIgnoreCount()).isEqualTo(1);
        assertThat(result.getFailureCount()).isEqualTo(0);
    }

    @Test
    public void shouldReportSkippedTest() {

        //When
        Result result = JUnitCore.runClasses(CuppaRunnerTest.SkippedTest.class);

        //Then
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(result.getRunCount()).isEqualTo(0);
        assertThat(result.getIgnoreCount()).isEqualTo(1);
        assertThat(result.getFailureCount()).isEqualTo(0);
    }

    @Test
    public void shouldReportFailingBeforeHookTest() {

        //When
        Result result = JUnitCore.runClasses(CuppaRunnerTest.FailingBeforeHookTest.class);

        //Then
        assertThat(result.wasSuccessful()).isFalse();
        assertThat(result.getRunCount()).isEqualTo(0);
        assertThat(result.getIgnoreCount()).isEqualTo(0);
        assertThat(result.getFailureCount()).isEqualTo(1);
    }

    @Test
    public void shouldReportFailingAfterHookTest() {

        //When
        Result result = JUnitCore.runClasses(CuppaRunnerTest.FailingAfterHookTest.class);

        //Then
        assertThat(result.wasSuccessful()).isFalse();
        assertThat(result.getRunCount()).isEqualTo(1);
        assertThat(result.getIgnoreCount()).isEqualTo(0);
        assertThat(result.getFailureCount()).isEqualTo(1);
    }

    @Test
    public void shouldReportFailingBeforeEachHookTest() {

        //When
        Result result = JUnitCore.runClasses(CuppaRunnerTest.FailingBeforeEachHookTest.class);

        //Then
        assertThat(result.wasSuccessful()).isFalse();
        assertThat(result.getRunCount()).isEqualTo(0);
        assertThat(result.getIgnoreCount()).isEqualTo(0);
        assertThat(result.getFailureCount()).isEqualTo(1);
    }

    @Test
    public void shouldReportFailingAfterEachHookTest() {

        //When
        Result result = JUnitCore.runClasses(CuppaRunnerTest.FailingAfterEachHookTest.class);

        //Then
        assertThat(result.wasSuccessful()).isFalse();
        assertThat(result.getRunCount()).isEqualTo(1);
        assertThat(result.getIgnoreCount()).isEqualTo(0);
        assertThat(result.getFailureCount()).isEqualTo(1);
    }

    @Test
    public void shouldReportCorrectDescriptionsOfSuite() {
        JUnitCore jUnit = new JUnitCore();
        jUnit.addListener(new RunListener() {
            @Override
            public void testRunStarted(Description description) throws Exception {
                suiteDescription = description;
            }
        });
        jUnit.run(CuppaRunnerTest.PassingTest.class);

        assertThat(suiteDescription).isNotNull();
        assertThat(suiteDescription.isSuite()).isTrue();
        assertThat(suiteDescription.getChildren()).hasSize(1);

        Description cuppaDescription = suiteDescription.getChildren().get(0);

        assertThat(cuppaDescription.isSuite()).isTrue();
        assertThat(cuppaDescription.getDisplayName()).isEqualTo(CuppaRunnerTest.PassingTest.class.getName());
        assertThat(cuppaDescription.getChildren()).hasSize(1);

        Description describeDescription = cuppaDescription.getChildren().get(0);

        assertThat(describeDescription.isSuite()).isTrue();
        assertThat(describeDescription.getDisplayName()).isEqualTo("Cuppa");
        assertThat(describeDescription.getChildren()).hasSize(1);

        Description whenDescription = describeDescription.getChildren().get(0);

        assertThat(whenDescription.isSuite()).isTrue();
        assertThat(whenDescription.getDisplayName()).isEqualTo("when running with CuppaRunner");
        assertThat(whenDescription.getChildren()).hasSize(1);

        Description testDescription = whenDescription.getChildren().get(0);

        assertThat(testDescription.isTest()).isTrue();
        assertThat(testDescription.getDisplayName()).isEqualTo("shows a passing test as passing("
                + CuppaRunnerTest.PassingTest.class.getName() + ")");
        assertThat(testDescription.getTestClass()).isEqualTo(CuppaRunnerTest.PassingTest.class);
    }

    @Test
    public void shouldReportSameTestDescriptionsAsGivenBeforeStart() {

        List<Description> testDescriptions = new ArrayList<>();
        //When
        JUnitCore jUnit = new JUnitCore();
        jUnit.addListener(new RunListener() {
            @Override
            public void testRunStarted(Description description) throws Exception {
                suiteDescription = description;
            }

            @Override
            public void testStarted(Description description) throws Exception {
                testDescriptions.add(description);
            }
        });
        jUnit.run(CuppaRunnerTest.PassingTest.class);

        //Then

        Description testDescription = suiteDescription.getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0);

        assertThat(testDescriptions).hasSize(1);
        assertThat(testDescriptions.get(0)).isEqualTo(testDescription);
    }

    @Test
    public void shouldReportDistinctDescriptionsForTestsWithSameName() {

        List<Description> testDescriptions = new ArrayList<>();
        //When
        JUnitCore jUnit = new JUnitCore();
        jUnit.addListener(new RunListener() {
            @Override
            public void testRunStarted(Description description) throws Exception {
                suiteDescription = description;
            }

            @Override
            public void testStarted(Description description) throws Exception {
                testDescriptions.add(description);
            }
        });
        jUnit.run(CuppaRunnerTest.DuplicateTests.class);

        //Then
        Description describeDescription = suiteDescription.getChildren().get(0)
                .getChildren().get(0);

        Description test1Description = describeDescription.getChildren().get(0).getChildren().get(0);
        Description test2Description = describeDescription.getChildren().get(1).getChildren().get(0);

        assertThat(test1Description).isNotEqualTo(test2Description);

        assertThat(testDescriptions).hasSize(2);
        assertThat(testDescriptions.get(0)).isNotEqualTo(testDescriptions.get(1));

        assertThat(testDescriptions.get(0)).isEqualTo(test1Description);
        assertThat(testDescriptions.get(1)).isEqualTo(test2Description);
    }

    @Test
    public void shouldReportDistinctDescriptionsForTestBlocksWithSameName() {

        List<Description> testDescriptions = new ArrayList<>();
        //When
        JUnitCore jUnit = new JUnitCore();
        jUnit.addListener(new RunListener() {
            @Override
            public void testRunStarted(Description description) throws Exception {
                suiteDescription = description;
            }

            @Override
            public void testStarted(Description description) throws Exception {
                testDescriptions.add(description);
            }
        });
        jUnit.run(CuppaRunnerTest.DuplicateTestBlocks.class);

        //Then
        Description when1Description = suiteDescription.getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0);
        Description when2Description = suiteDescription.getChildren().get(0)
                .getChildren().get(1)
                .getChildren().get(0);

        assertThat(when1Description).isNotEqualTo(when2Description);
    }

    @RunWith(CuppaRunner.class)
    public static class PassingTest {
        {
            describe("Cuppa", () -> {
                when("running with CuppaRunner", () -> {
                    it("shows a passing test as passing", TestFunction.identity());
                });
            });
        }
    }

    @RunWith(CuppaRunner.class)
    public static class FailingTest {
        {
            describe("Cuppa", () -> {
                when("running with CuppaRunner", () -> {
                    it("shows a failing test as failing", () -> {
                        Assertions.assertThat(true).isFalse();
                    });
                });
            });
        }
    }

    @RunWith(CuppaRunner.class)
    public static class ErroringTest {
        {
            describe("Cuppa", () -> {
                when("running with CuppaRunner", () -> {
                    it("erroring test", () -> {
                        throw new RuntimeException("Test is bad");
                    });
                });
            });
        }
    }

    @RunWith(CuppaRunner.class)
    public static class PendingTest {
        {
            describe("Cuppa", () -> {
                when("running with CuppaRunner", () -> {
                    it("pending test");
                });
            });
        }
    }

    @RunWith(CuppaRunner.class)
    public static class SkippedTest {
        {
            describe("Cuppa", () -> {
                when("running with CuppaRunner", () -> {
                    skip.it("skipped test", TestFunction.identity());
                });
            });
        }
    }

    @RunWith(CuppaRunner.class)
    public static class FailingBeforeHookTest {
        {
            describe("Cuppa", () -> {
                when("running with CuppaRunner", () -> {
                    before("failing before hook", () -> {
                        throw new RuntimeException("Before hook is bad");
                    });
                    it("does not run this test", TestFunction.identity());
                });
            });
        }
    }

    @RunWith(CuppaRunner.class)
    public static class FailingAfterHookTest {
        {
            describe("Cuppa", () -> {
                when("running with CuppaRunner", () -> {
                    after("failing after hook", () -> {
                        throw new RuntimeException("After hook is bad");
                    });
                    it("shows a passing test as passing", TestFunction.identity());
                });
            });
        }
    }

    @RunWith(CuppaRunner.class)
    public static class FailingBeforeEachHookTest {
        {
            describe("Cuppa", () -> {
                when("running with CuppaRunner", () -> {
                    beforeEach("failing before each hook", () -> {
                        throw new RuntimeException("Before each hook is bad");
                    });
                    it("does not run this test", TestFunction.identity());
                });
            });
        }
    }

    @RunWith(CuppaRunner.class)
    public static class FailingAfterEachHookTest {
        {
            describe("Cuppa", () -> {
                when("running with CuppaRunner", () -> {
                    afterEach("failing after each hook", () -> {
                        throw new RuntimeException("After each hook is bad");
                    });
                    it("shows a passing test as passing", TestFunction.identity());
                });
            });
        }
    }

    @RunWith(CuppaRunner.class)
    public static class DuplicateTests {
        {
            describe("Cuppa", () -> {
                when("running with CuppaRunner", () -> {
                    it("is a duplicate test name", TestFunction.identity());
                });
                when("in another block", () -> {
                    it("is a duplicate test name", TestFunction.identity());
                });
            });
        }
    }

    @RunWith(CuppaRunner.class)
    public static class DuplicateTestBlocks {
        {
            describe("Cuppa", () -> {
                when("duplicate test blocks", () -> {
                    it("supports tests in both blocks", TestFunction.identity());
                });
            });
            describe("something else", () -> {
                when("duplicate test blocks", () -> {
                    it("supports tests in both blocks but different names", TestFunction.identity());
                });
            });
        }
    }
}
