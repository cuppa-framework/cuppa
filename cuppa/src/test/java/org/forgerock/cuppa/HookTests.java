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
import static org.forgerock.cuppa.CuppaTestProvider.runTests;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.internal.TestContainer;
import org.forgerock.cuppa.reporters.Reporter;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class HookTests {

    @BeforeMethod
    public void setup() {
        TestContainer.INSTANCE.reset();
        TestContainer.INSTANCE.setTestClass(HookTests.class);
    }

    @Test
    public void beforeShouldRunOnceBeforeTests() throws Exception {

        //Given
        HookFunction topLevelBeforeFunction = mock(HookFunction.class);
        HookFunction nestedBeforeFunction = mock(HookFunction.class);
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
        runTests(mock(Reporter.class));

        //Then
        verify(topLevelBeforeFunction).apply();
        verify(nestedBeforeFunction).apply();
    }

    @Test
    public void multipleBeforeHooksShouldRunInOrderOfDefinition() {

        //Given
        HookFunction firstBeforeFunction = mock(HookFunction.class);
        HookFunction secondBeforeFunction = mock(HookFunction.class);
        {
            describe("before blocks", () -> {
                before(firstBeforeFunction);
                before(secondBeforeFunction);
                it("runs the third test", () -> {
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verifiedCalledInOrder(firstBeforeFunction, secondBeforeFunction);
    }

    @Test
    public void afterShouldRunOnceAfterTests() throws Exception {

        //Given
        HookFunction topLevelAfterFunction = mock(HookFunction.class);
        HookFunction nestedAfterFunction = mock(HookFunction.class);
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
        runTests(mock(Reporter.class));

        //Then
        verify(topLevelAfterFunction).apply();
        verify(nestedAfterFunction).apply();
    }

    @Test
    public void multipleAfterHooksShouldRunInOrderOfDefinition() {

        //Given
        HookFunction firstAfterFunction = mock(HookFunction.class);
        HookFunction secondAfterFunction = mock(HookFunction.class);
        {
            describe("before blocks", () -> {
                after(firstAfterFunction);
                after(secondAfterFunction);
                it("runs the third test", () -> {
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verifiedCalledInOrder(firstAfterFunction, secondAfterFunction);
    }

    @Test
    public void beforeEachShouldRunBeforeEachTest() throws Exception {

        //Given
        HookFunction topLevelBeforeEachFunction = mock(HookFunction.class);
        HookFunction nestedBeforeEachFunction = mock(HookFunction.class);
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
        runTests(mock(Reporter.class));

        //Then
        verify(topLevelBeforeEachFunction, times(3)).apply();
        verify(nestedBeforeEachFunction, times(2)).apply();
    }

    @Test
    public void multipleBeforeEachHooksShouldRunInOrderOfDefinition() {

        //Given
        HookFunction firstBeforeEachFunction = mock(HookFunction.class);
        HookFunction secondBeforeEachFunction = mock(HookFunction.class);
        {
            describe("before blocks", () -> {
                beforeEach(firstBeforeEachFunction);
                beforeEach(secondBeforeEachFunction);
                it("runs the third test", () -> {
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verifiedCalledInOrder(firstBeforeEachFunction, secondBeforeEachFunction);
    }

    @Test
    public void afterEachShouldRunAfterEachTest() throws Exception {

        //Given
        HookFunction topLevelAfterEachFunction = mock(HookFunction.class);
        HookFunction nestedAfterEachFunction = mock(HookFunction.class);
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
        runTests(mock(Reporter.class));

        //Then
        verify(topLevelAfterEachFunction, times(3)).apply();
        verify(nestedAfterEachFunction, times(2)).apply();
    }

    @Test
    public void multipleAfterEachHooksShouldRunInOrderOfDefinition() {

        //Given
        HookFunction firstAfterEachFunction = mock(HookFunction.class);
        HookFunction secondAfterEachFunction = mock(HookFunction.class);
        {
            describe("before blocks", () -> {
                afterEach(firstAfterEachFunction);
                afterEach(secondAfterEachFunction);
                it("runs the third test", () -> {
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        verifiedCalledInOrder(firstAfterEachFunction, secondAfterEachFunction);
    }

    @Test
    public void shouldRunAllHooksInTheCorrectOrder() throws Exception {

        //Given
        HookFunction topLevelBeforeFunction = mock(HookFunction.class, "topLevelBeforeFunction");
        HookFunction topLevelBeforeEachFunction = mock(HookFunction.class, "topLevelBeforeEachFunction");
        HookFunction topLevelAfterEachFunction = mock(HookFunction.class, "topLevelAfterEachFunction");
        HookFunction topLevelAfterFunction = mock(HookFunction.class, "topLevelAfterFunction");
        HookFunction nestedBeforeFunction = mock(HookFunction.class, "nestedBeforeFunction");
        HookFunction nestedBeforeEachFunction = mock(HookFunction.class, "nestedBeforeEachFunction");
        HookFunction nestedAfterEachFunction = mock(HookFunction.class, "nestedAfterEachFunction");
        HookFunction nestedAfterFunction = mock(HookFunction.class, "nestedAfterFunction");
        TestFunction testFunction = mock(TestFunction.class, "testFunction");
        TestFunction nestedTestFunction = mock(TestFunction.class, "nestedTestFunction");

        {
            describe("before blocks", () -> {
                before(topLevelBeforeFunction);
                beforeEach(topLevelBeforeEachFunction);
                afterEach(topLevelAfterEachFunction);
                after(topLevelAfterFunction);
                it("doesn't run the test", testFunction);
                when("the first 'before' block throws an exception", () -> {
                    before(nestedBeforeFunction);
                    beforeEach(nestedBeforeEachFunction);
                    afterEach(nestedAfterEachFunction);
                    after(nestedAfterFunction);
                    it("doesn't run the test nested", nestedTestFunction);
                });
            });
        }

        //When
        runTests(mock(Reporter.class));

        //Then
        InOrder inOrder = inOrder(topLevelBeforeFunction, topLevelBeforeEachFunction, testFunction,
                topLevelAfterEachFunction, nestedBeforeFunction, nestedBeforeEachFunction,
                nestedTestFunction, nestedAfterEachFunction, nestedAfterFunction,
                topLevelAfterFunction);
        inOrder.verify(topLevelBeforeFunction).apply();
        inOrder.verify(topLevelBeforeEachFunction).apply();
        inOrder.verify(testFunction).apply();
        inOrder.verify(topLevelAfterEachFunction).apply();
        inOrder.verify(nestedBeforeFunction).apply();
        inOrder.verify(topLevelBeforeEachFunction).apply();
        inOrder.verify(nestedBeforeEachFunction).apply();
        inOrder.verify(nestedTestFunction).apply();
        inOrder.verify(nestedAfterEachFunction).apply();
        inOrder.verify(topLevelAfterEachFunction).apply();
        inOrder.verify(nestedAfterFunction).apply();
        inOrder.verify(topLevelAfterFunction).apply();
    }

    private void verifiedCalledInOrder(HookFunction... functions) {
        InOrder inOrder = inOrder((Object[]) functions);
        Arrays.stream(functions).forEach((f) -> {
            try {
                inOrder.verify(f).apply();
            } catch (Exception ignored) {
            }
        });
    }
}
