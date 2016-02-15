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
        verify(reporter).start();
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

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).testPass(findTest(rootBlock, "test"));
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

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).testFail(findTest(rootBlock, "test"), exception);
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

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).describeStart(findTestBlock(rootBlock, "describe"));
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

        //When
        runTests(rootBlock, reporter);

        //Then
        verify(reporter).describeEnd(findTestBlock(rootBlock, "describe"));
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

        //When
        runTests(rootBlock, reporter);

        //Then
        InOrder inOrder = inOrder(reporter);
        inOrder.verify(reporter).start();
        inOrder.verify(reporter).describeStart(findTestBlock(rootBlock, "describe"));
        inOrder.verify(reporter).describeStart(findTestBlock(rootBlock, "when when"));
        inOrder.verify(reporter).testPass(findTest(rootBlock, "test"));
        inOrder.verify(reporter).describeEnd(findTestBlock(rootBlock, "when when"));
        inOrder.verify(reporter).describeEnd(findTestBlock(rootBlock, "describe"));
        inOrder.verify(reporter).end();
    }
}
