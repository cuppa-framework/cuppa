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
import static org.forgerock.cuppa.ModelFinder.findTest;
import static org.forgerock.cuppa.ModelFinder.findTestBlock;
import static org.mockito.Mockito.*;

import org.forgerock.cuppa.reporters.Reporter;
import org.mockito.InOrder;
import org.testng.annotations.Test;

public class ReportingTests extends AbstractTest {
    @Test
    public void reporterShouldBeNotifiedAtTheStart() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).start();
    }

    @Test
    public void reporterShouldBeNotifiedAtTheEnd() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).end();
    }

    @Test
    public void reporterShouldBeNotifiedOfPassingTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testPass(findTest("test"));
    }

    @Test
    public void reporterShouldBeNotifiedOfFailingTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        IllegalStateException exception = new IllegalStateException();
        {
            describe("describe", () -> {
                it("test", () -> {
                    throw exception;
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testFail(findTest("test"), exception);
    }

    @Test
    public void reporterShouldBeNotifiedOfStartOfDescribe() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).describeStart(findTestBlock("describe"));
    }

    @Test
    public void reporterShouldBeNotifiedOfEndOfDescribe() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("describe", () -> {
                it("test", () -> {
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).describeEnd(findTestBlock("describe"));
    }

    @Test
    public void reporterShouldBeNotifiedInTheCorrectOrder() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("describe", () -> {
                when("when", () -> {
                    it("test", () -> {
                    });
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        InOrder inOrder = inOrder(reporter);
        inOrder.verify(reporter).start();
        inOrder.verify(reporter).describeStart(findTestBlock("describe"));
        inOrder.verify(reporter).describeStart(findTestBlock("when when"));
        inOrder.verify(reporter).testPass(findTest("test"));
        inOrder.verify(reporter).describeEnd(findTestBlock("when when"));
        inOrder.verify(reporter).describeEnd(findTestBlock("describe"));
        inOrder.verify(reporter).end();
    }
}
