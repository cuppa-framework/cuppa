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
import static org.forgerock.cuppa.Cuppa.when;
import static org.forgerock.cuppa.TestCuppaSupport.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;
import org.mockito.InOrder;
import org.testng.annotations.Test;

public class ReportingTests {
    @Test
    public void reporterShouldBeNotifiedAtTheStart() {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).start(rootBlock);
    }

    @Test
    public void reporterShouldBeNotifiedAtTheEnd() {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        });

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).end();
    }

    @Test
    public void reporterShouldBeNotifiedOfPassingTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        });
        List<TestBlock> parents = Arrays.asList(rootBlock, rootBlock.testBlocks.get(0));

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).testPass(eq(findTest(rootBlock, "test")), eq(parents));
    }

    @Test
    public void reporterShouldBeNotifiedOfFailingTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        IllegalStateException exception = new IllegalStateException();
        TestBlock rootBlock = defineTests(() -> {
            describe("describe", () -> {
                it("test", () -> {
                    throw exception;
                });
            });
        });
        List<TestBlock> parents = Arrays.asList(rootBlock, rootBlock.testBlocks.get(0));

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).testFail(eq(findTest(rootBlock, "test")), eq(parents), eq(exception));
    }

    @Test
    public void reporterShouldBeNotifiedOfStartOfDescribe() {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        });
        List<TestBlock> parents = Collections.singletonList(rootBlock);

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).testBlockStart(eq(findTestBlock(rootBlock, "describe")), eq(parents));
    }

    @Test
    public void reporterShouldBeNotifiedOfEndOfDescribe() {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        });
        List<TestBlock> parents = Collections.singletonList(rootBlock);

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).testBlockEnd(eq(findTestBlock(rootBlock, "describe")), eq(parents));
    }

    @Test
    public void reporterShouldBeNotifiedInTheCorrectOrder() {

        //Given
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("describe", () -> {
                when("when", () -> {
                    it("test", () -> {
                    });
                });
            });
        });
        TestBlock describeBlock = rootBlock.testBlocks.get(0);
        TestBlock whenBlock = describeBlock.testBlocks.get(0);
        List<TestBlock> describeBlockParents = Collections.singletonList(rootBlock);
        List<TestBlock> whenBlockParents = Arrays.asList(rootBlock, describeBlock);
        List<TestBlock> testParents = Arrays.asList(rootBlock, describeBlock, whenBlock);

        //When
        runTests(rootBlock, reporter);

        //Then

        InOrder inOrder = inOrder(reporter);
        inOrder.verify(reporter).start(rootBlock);
        inOrder.verify(reporter).testBlockStart(eq(findTestBlock(rootBlock, "describe")), eq(describeBlockParents));
        inOrder.verify(reporter).testBlockStart(eq(findTestBlock(rootBlock, "when")), eq(whenBlockParents));
        inOrder.verify(reporter).testPass(eq(findTest(rootBlock, "test")), eq(testParents));
        inOrder.verify(reporter).testBlockEnd(eq(findTestBlock(rootBlock, "when")), eq(whenBlockParents));
        inOrder.verify(reporter).testBlockEnd(eq(findTestBlock(rootBlock, "describe")), eq(describeBlockParents));
        inOrder.verify(reporter).end();
    }

    @Test
    public void reporterShouldBeNotifiedOfHookFailure() {

        //Given
        RuntimeException exception = new RuntimeException();
        Reporter reporter = mock(Reporter.class);
        TestBlock rootBlock = defineTests(() -> {
            describe("describe", () -> {
                beforeEach("hook", () -> {
                    throw exception;
                });
                when("when", () -> {
                    it("test", () -> {
                    });
                });
            });
        });
        List<TestBlock> parents = Arrays.asList(rootBlock, rootBlock.testBlocks.get(0));

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).hookFail(eq(findHook(rootBlock, "hook")), eq(parents), eq(exception));
    }
}
