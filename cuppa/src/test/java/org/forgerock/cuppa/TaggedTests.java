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
import static org.forgerock.cuppa.TestCuppaSupport.defineTests;
import static org.forgerock.cuppa.TestCuppaSupport.runTests;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.model.Tags;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TaggedTests {
    @Test
    public void shouldRunAllTestsWithNoRunTagsSpecified() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testFunction = mock(TestFunction.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("tagged tests", () -> {
                with(tags("smoke")).
                it("runs the tagged test", testFunction);
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(testFunction).apply();
        verify(reporter).testPass(any(), anyListOf(TestBlock.class));
    }

    @Test
    public void shouldBeAbleToTagADescribeBlock() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        TestBlock rootBlock = defineTests(() -> {
            with(tags("smoke")).
            describe("tagged tests", () -> {
                it("runs the tagged test", testFunction);
            });
        });

        //When
        runTests(rootBlock, reporter, Tags.tags(tags));

        //Then
        verify(testFunction).apply();
        verify(reporter).testPass(any(), anyListOf(TestBlock.class));
    }

    @Test
    public void shouldOnlyRunMatchingTaggedTest() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testOneFunction = mock(TestFunction.class);
        TestFunction testTwoFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        TestBlock rootBlock = defineTests(() -> {
            describe("tagged tests", () -> {
                with(tags("smoke")).
                it("runs the tagged test", testOneFunction);
                with(tags("long")).
                it("does not runs the non-matching tagged test", testTwoFunction);
            });
        });

        //When
        runTests(rootBlock, reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(testTwoFunction, never()).apply();
        verify(reporter).testPass(any(), anyListOf(TestBlock.class));
    }

    @Test
    public void shouldOnlyRunMatchingTaggedTestAtWhenBlockLevel() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testOneFunction = mock(TestFunction.class);
        TestFunction testTwoFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        TestBlock rootBlock = defineTests(() -> {
            describe("tagged tests", () -> {
                with(tags("smoke")).
                when("the when is tagged 'smoke'", () -> {
                    it("runs the tagged test", testOneFunction);
                });
                with(tags("long")).
                when("the when is tagged 'long'", () -> {
                    it("does not runs the non-matching tagged test", testTwoFunction);
                });
            });
        });

        //When
        runTests(rootBlock, reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(testTwoFunction, never()).apply();
        verify(reporter).testPass(any(), anyListOf(TestBlock.class));
    }

    @Test
    public void shouldOnlyRunMatchingTaggedTestAtDescribeBlockLevel() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testOneFunction = mock(TestFunction.class);
        TestFunction testTwoFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        TestBlock rootBlock = defineTests(() -> {
            with(tags("smoke")).
            describe("the describe is tagged 'smoke'", () -> {
                it("runs the tagged test", testOneFunction);
            });
            with(tags("long")).
            describe("the describe is tagged 'long'", () -> {
                it("does not runs the non-matching tagged test", testTwoFunction);
            });
        });

        //When
        runTests(rootBlock, reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(testTwoFunction, never()).apply();
        verify(reporter).testPass(any(), anyListOf(TestBlock.class));
    }

    @Test
    public void shouldRunAllMatchingTaggedTests() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testOneFunction = mock(TestFunction.class);
        TestFunction testTwoFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        TestBlock rootBlock = defineTests(() -> {
            describe("tagged tests", () -> {
                with(tags("smoke")).
                it("runs the tagged test", testOneFunction);
                with(tags("smoke")).
                it("runs the second tagged test", testTwoFunction);
            });
        });

        //When
        runTests(rootBlock, reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(testTwoFunction).apply();
        verify(reporter, times(2)).testPass(any(), anyListOf(TestBlock.class));
    }

    @Test
    public void shouldRunAllMatchingTaggedTestsAtWhenBlockLevel() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testOneFunction = mock(TestFunction.class);
        TestFunction testTwoFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        TestBlock rootBlock = defineTests(() -> {
            describe("tagged tests", () -> {
                with(tags("smoke")).
                when("the when is tagged 'smoke'", () -> {
                    it("runs the tagged test", testOneFunction);
                });
                with(tags("smoke")).
                when("the when is tagged 'smoke'", () -> {
                    it("runs the second tagged test", testTwoFunction);
                });
            });
        });

        //When
        runTests(rootBlock, reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(testTwoFunction).apply();
        verify(reporter, times(2)).testPass(any(), anyListOf(TestBlock.class));
    }

    @Test
    public void shouldRunAllMatchingTaggedTestsAtDescribeBlockLevel() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testOneFunction = mock(TestFunction.class);
        TestFunction testTwoFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        TestBlock rootBlock = defineTests(() -> {
            with(tags("smoke")).
            describe("the describe is tagged 'smoke'", () -> {
                it("runs the tagged test", testOneFunction);
            });
            with(tags("smoke")).
            describe("the describe is tagged 'smoke'", () -> {
                it("runs the second tagged test", testTwoFunction);
            });
        });

        //When
        runTests(rootBlock, reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(testTwoFunction).apply();
        verify(reporter, times(2)).testPass(any(), anyListOf(TestBlock.class));
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
        TestBlock rootBlock = defineTests(() -> {
            describe("tagged tests", () -> {
                with(tags("smoke", "long", "big")).
                it("runs the tagged test", testOneFunction);
            });
        });

        //When
        runTests(rootBlock, reporter, Tags.tags(tags));

        //Then
        verify(testOneFunction).apply();
        verify(reporter).testPass(any(), anyListOf(TestBlock.class));
    }

    @Test
    public void shouldRunAllTestsWhichContainAnyRunTag() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testFunctionRun = mock(TestFunction.class);
        TestFunction testFunctionNotRun = mock(TestFunction.class);
        Set<String> tags = new HashSet<>(Arrays.asList("smoke", "big"));
        TestBlock rootBlock = defineTests(() -> {
            describe("tagged tests", () -> {
                with(tags("smoke", "long")).
                it("runs the tagged test", testFunctionRun);
                with(tags("smoke", "big")).
                it("runs the tagged test", testFunctionRun);
                with(tags("long", "big")).
                it("runs the tagged test", testFunctionRun);
                with(tags("smoke")).
                it("runs the tagged test", testFunctionRun);
                with(tags("long")).
                it("runs the tagged test", testFunctionNotRun);
                with(tags("big")).
                it("runs the tagged test", testFunctionRun);
                with(tags("long", "nightly")).
                it("runs the tagged test", testFunctionNotRun);
            });
        });

        //When
        runTests(rootBlock, reporter, Tags.tags(tags));

        //Then
        verify(testFunctionRun, times(5)).apply();
        verify(testFunctionNotRun, never()).apply();
    }

    @Test
    public void shouldNotRunTestsWhichMatchExcludedTags() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testFunction = mock(TestFunction.class);
        Set<String> excludedTags = Collections.singleton("smoke");
        TestBlock rootBlock = defineTests(() -> {
            describe("tagged tests", () -> {
                with(tags("smoke")).
                it("runs the tagged test", testFunction);
            });
        });

        //When
        runTests(rootBlock, reporter, Tags.excludedTags(excludedTags));

        //Then
        verify(testFunction, never()).apply();
    }

    @Test
    public void shouldRunTestsWhichDoNotMatchExcludedTags() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction taggedFunction = mock(TestFunction.class);
        TestFunction untaggedFunction = mock(TestFunction.class);
        Set<String> excludedTags = Collections.singleton("smoke");
        TestBlock rootBlock = defineTests(() -> {
            with(tags("something")).
            describe("tagged tests", () -> {
                with(tags("smoke")).
                it("excludes the tagged test", taggedFunction);
                it("runs the untagged test", untaggedFunction);
            });
        });

        //When
        runTests(rootBlock, reporter, Tags.excludedTags(excludedTags));

        //Then
        verify(taggedFunction, never()).apply();
        verify(untaggedFunction).apply();
    }

    @Test
    public void shouldNotRunTestsWhichMatchBothRunTagsAndExcludedTags() throws Exception {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestFunction testFunction = mock(TestFunction.class);
        Set<String> tags = Collections.singleton("smoke");
        Set<String> excludedTags = Collections.singleton("smoke");
        TestBlock rootBlock = defineTests(() -> {
            describe("tagged tests", () -> {
                with(tags("smoke")).
                it("runs the tagged test", testFunction);
            });
        });

        //When
        runTests(rootBlock, reporter, new Tags(tags, excludedTags));

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
        TestBlock rootBlock = defineTests(() -> {
            describe("tagged tests", () -> {
                with(tags("smoke", "long")).
                it("runs the tagged test", testFunctionNotRun);
                with(tags("long")).
                it("runs the tagged test", testFunctionRun);
                with(tags("big")).
                it("runs the tagged test", testFunctionNotRun);
            });
        });

        //When
        runTests(rootBlock, reporter, new Tags(tags, excludedTags));

        //Then
        verify(testFunctionNotRun, never()).apply();
        verify(testFunctionRun).apply();
    }
}
