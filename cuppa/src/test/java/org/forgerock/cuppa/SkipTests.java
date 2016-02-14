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

import static org.forgerock.cuppa.Cuppa.*;
import static org.forgerock.cuppa.Cuppa.only;
import static org.forgerock.cuppa.Cuppa.when;
import static org.forgerock.cuppa.ModelFinder.findTest;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.reporters.Reporter;
import org.testng.annotations.Test;

public class SkipTests extends AbstractTest {
    @Test
    public void shouldSkipTestIfTestIsMarkedSkip() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    skip().it("test", testFunction);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

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
                    skip().it("test", testFunction);
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testSkip(findTest("test"));
    }

    @Test
    public void shouldSkipTestsNestedInSkippedBlock() throws Exception {

        //Given
        TestFunction testFunction1 = mock(TestFunction.class);
        TestFunction testFunction2 = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                skip().when("the 'when' is run", () -> {
                    it("runs the test", testFunction1);
                    it("runs the test", testFunction2);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

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
                skip().when("the 'when' is run", () -> {
                    it("test1", testFunction1);
                    it("test2", testFunction2);
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testSkip(findTest("test1"));
        verify(reporter).testSkip(findTest("test2"));
    }

    @Test
    public void shouldIgnoreTestsIfOtherTestIsMarkedOnly() throws Exception {

        //Given
        TestFunction testFunctionBefore = mock(TestFunction.class);
        TestFunction testFunction = mock(TestFunction.class);
        TestFunction testFunctionAfter = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("before test", testFunctionBefore);
                    only().it("test", testFunction);
                    it("after test", testFunctionAfter);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunctionBefore, never()).apply();
        verify(testFunction).apply();
        verify(testFunctionAfter, never()).apply();
    }

    @Test
    public void shouldNotReportTestsIfOtherTestIsMarkedOnly() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("before test", TestFunction.identity());
                    only().it("test", TestFunction.identity());
                    it("after test", TestFunction.identity());
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testPass(any());
        verify(reporter).testPass(findTest("test"));
    }

    @Test
    public void shouldRunAllTestsMarkedOnly() throws Exception {

        //Given
        TestFunction testFunctionOnly1 = mock(TestFunction.class);
        TestFunction testFunctionOnly2 = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                when("the 'when' is run", () -> {
                    it("before test", TestFunction.identity());
                    only().it("only test 1", testFunctionOnly1);
                    only().it("only test 2", testFunctionOnly2);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunctionOnly1).apply();
        verify(testFunctionOnly2).apply();
    }

    @Test
    public void shouldRunTestsInBlockMarkedOnly() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        TestFunction testFunctionAfter = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                only().when("only when", () -> {
                    it("test", testFunction);
                });
                when("after when", () -> {
                    it("test", testFunctionAfter);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunction).apply();
        verify(testFunctionAfter, never()).apply();
    }

    @Test
    public void shouldSkipTestsMarkedSkipInBlockMarkedOnly() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                only().when("only when", () -> {
                    skip().it("test", testFunction);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunction, never()).apply();
    }

    @Test
    public void shouldSkipTestsMarkedOnlyInBlockMarkedSkip() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                skip().when("only when", () -> {
                    only().it("test", testFunction);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunction, never()).apply();
    }

    @Test
    public void shouldIgnoreTestIfOtherTestIsMarkedOnlyInSkipBlock() throws Exception {

        //Given
        TestFunction testFunction = mock(TestFunction.class);
        {
            describe("basic API usage", () -> {
                skip().when("only when", () -> {
                    only().it("test", TestFunction.identity());
                });
                it("test 2", testFunction);
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verify(testFunction, never()).apply();
    }
}
