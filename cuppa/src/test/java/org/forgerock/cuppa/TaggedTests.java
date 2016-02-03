/*
 * Copyright 2016 ForgeRock AS.
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
import static org.forgerock.cuppa.Cuppa.when;
import static org.forgerock.cuppa.CuppaTestProvider.runTests;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.internal.TestContainer;
import org.forgerock.cuppa.model.Tags;
import org.forgerock.cuppa.reporters.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TaggedTests {

    @BeforeMethod
    public void setup() {
        TestContainer.INSTANCE.reset();
        TestContainer.INSTANCE.setTestClass(HookTests.class);
    }

    @Test
    public void shouldRunAllTestsWithNoRunTagsSpecified() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testFunction = mock(TestFunction.class);
        {
            describe("tagged tests", () -> {
                it("runs the tagged test")
                        .withTags("smoke")
                        .asserts(testFunction);
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(testFunction).apply();
        verify(reporter).testPass(any());
    }

    @Test
    public void shouldBeAbleToTagADescribeBlock() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        {
            describe("tagged tests")
                    .eachWithTags("smoke")
                    .then(() -> {
                        it("runs the tagged test", testFunction);
                    });
        }

        //When
        runTests(reporter, Tags.tags(tags));

        //Then
        verify(testFunction).apply();
        verify(reporter).testPass(any());
    }

    @Test
    public void shouldOnlyRunMatchingTaggedTest() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testOneFunction = mock(TestFunction.class);
        TestFunction testTwoFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        {
            describe("tagged tests", () -> {
                it("runs the tagged test")
                        .withTags("smoke")
                        .asserts(testOneFunction);
                it("does not runs the non-matching tagged test")
                        .withTags("long")
                        .asserts(testTwoFunction);
            });
        }

        //When
        runTests(reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(testTwoFunction, never()).apply();
        verify(reporter).testPass(any());
    }

    @Test
    public void shouldOnlyRunMatchingTaggedTestAtWhenBlockLevel() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testOneFunction = mock(TestFunction.class);
        TestFunction testTwoFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        {
            describe("tagged tests", () -> {
                when("the when is tagged 'smoke'")
                        .eachWithTags("smoke")
                        .then(() -> {
                            it("runs the tagged test", testOneFunction);
                        });
                when("the when is tagged 'long'")
                        .eachWithTags("long")
                        .then(() -> {
                            it("does not runs the non-matching tagged test", testTwoFunction);
                        });
            });
        }

        //When
        runTests(reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(testTwoFunction, never()).apply();
        verify(reporter).testPass(any());
    }

    @Test
    public void shouldOnlyRunMatchingTaggedTestAtDescribeBlockLevel() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testOneFunction = mock(TestFunction.class);
        TestFunction testTwoFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        {
            describe("the describe is tagged 'smoke'")
                    .eachWithTags("smoke")
                    .then(() -> {
                        it("runs the tagged test", testOneFunction);
                    });
            describe("the describe is tagged 'long'")
                    .eachWithTags("long")
                    .then(() -> {
                        it("does not runs the non-matching tagged test", testTwoFunction);
                    });
        }

        //When
        runTests(reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(testTwoFunction, never()).apply();
        verify(reporter).testPass(any());
    }

    @Test
    public void shouldRunAllMatchingTaggedTests() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testOneFunction = mock(TestFunction.class);
        TestFunction testTwoFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        {
            describe("tagged tests", () -> {
                it("runs the tagged test")
                        .withTags("smoke")
                        .asserts(testOneFunction);
                it("runs the second tagged test")
                        .withTags("smoke")
                        .asserts(testTwoFunction);
            });
        }

        //When
        runTests(reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(testTwoFunction).apply();
        verify(reporter, times(2)).testPass(any());
    }

    @Test
    public void shouldRunAllMatchingTaggedTestsAtWhenBlockLevel() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testOneFunction = mock(TestFunction.class);
        TestFunction testTwoFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        {
            describe("tagged tests", () -> {
                when("the when is tagged 'smoke'")
                        .eachWithTags("smoke")
                        .then(() -> {
                            it("runs the tagged test", testOneFunction);
                        });
                when("the when is tagged 'smoke'")
                        .eachWithTags("smoke")
                        .then(() -> {
                            it("runs the second tagged test", testTwoFunction);
                        });
            });
        }

        //When
        runTests(reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(testTwoFunction).apply();
        verify(reporter, times(2)).testPass(any());
    }

    @Test
    public void shouldRunAllMatchingTaggedTestsAtDescribeBlockLevel() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testOneFunction = mock(TestFunction.class);
        TestFunction testTwoFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        {
            describe("the describe is tagged 'smoke'")
                    .eachWithTags("smoke")
                    .then(() -> {
                        it("runs the tagged test", testOneFunction);
                    });
            describe("the describe is tagged 'smoke'")
                    .eachWithTags("smoke")
                    .then(() -> {
                        it("runs the second tagged test", testTwoFunction);
                    });
        }

        //When
        runTests(reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(testTwoFunction).apply();
        verify(reporter, times(2)).testPass(any());
    }

    @DataProvider
    private Object[][] testTagsContainRunTag() {
        return new Object[][]{
            {"smoke"},
            {"big"},
            {"long"},
        };
    }

    @Test(dataProvider = "testTagsContainRunTag")
    public void shouldRunTestsWhichMatchAnyTag(String tag) throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testOneFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton(tag);
        {
            describe("tagged tests", () -> {
                it("runs the tagged test")
                        .withTags("smoke", "long", "big")
                        .asserts(testOneFunction);
            });
        }

        //When
        runTests(reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(reporter).testPass(any());
    }

    @Test
    public void shouldRunAllTestsWhichContainAnyRunTag() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testFunctionRun = mock(TestFunction.class);
        TestFunction testFunctionNotRun = mock(TestFunction.class);
        Set<String> tags = new HashSet<>(Arrays.asList("smoke", "big"));
        {
            describe("tagged tests", () -> {
                it("runs the tagged test")
                        .withTags("smoke", "long")
                        .asserts(testFunctionRun);
                it("runs the tagged test")
                        .withTags("smoke", "big")
                        .asserts(testFunctionRun);
                it("runs the tagged test")
                        .withTags("long", "big")
                        .asserts(testFunctionRun);
                it("runs the tagged test")
                        .withTags("smoke")
                        .asserts(testFunctionRun);
                it("runs the tagged test")
                        .withTags("long")
                        .asserts(testFunctionNotRun);
                it("runs the tagged test")
                        .withTags("big")
                        .asserts(testFunctionRun);
                it("runs the tagged test")
                        .withTags("long", "nightly")
                        .asserts(testFunctionNotRun);
            });
        }

        //When
        runTests(reporter, Tags.tags(tags));

        //Then
        verify(testFunctionRun, times(5)).apply();
        verify(testFunctionNotRun, never()).apply();
    }

    @Test
    public void shouldNotRunTestsWhichMatchRunAntiTag() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testFunction = mock(TestFunction.class);
        Set<String> excludedTags = Collections.singleton("smoke");
        {
            describe("tagged tests", () -> {
                it("runs the tagged test")
                        .withTags("smoke")
                        .asserts(testFunction);
            });
        }

        //When
        runTests(reporter, Tags.excludedTags(excludedTags));

        //Then
        verify(testFunction, never()).apply();
    }

    @Test
    public void shouldNotRunTestsWhichMatchBothRunTagAndAntiTag() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        Set<String> excludedTags = Collections.singleton("smoke");
        {
            describe("tagged tests", () -> {
                it("runs the tagged test")
                        .withTags("smoke")
                        .asserts(testFunction);
            });
        }

        //When
        runTests(reporter, new Tags(tags, excludedTags));

        //Then
        verify(testFunction, never()).apply();
    }

    @Test
    public void shouldRunTestsWhichMatchRunTagAndNotAntiTag() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testFunctionNotRun = mock(TestFunction.class);
        TestFunction testFunctionRun = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("long");
        Set<String> excludedTags = Collections.singleton("smoke");
        {
            describe("tagged tests", () -> {
                it("runs the tagged test")
                        .withTags("smoke", "long")
                        .asserts(testFunctionNotRun);
                it("runs the tagged test")
                        .withTags("long")
                        .asserts(testFunctionRun);
                it("runs the tagged test")
                        .withTags("big")
                        .asserts(testFunctionNotRun);
            });
        }

        //When
        runTests(reporter, new Tags(tags, excludedTags));

        //Then
        verify(testFunctionNotRun, never()).apply();
        verify(testFunctionRun).apply();
    }
}
