package org.forgerock.cuppa;

import static org.forgerock.cuppa.Cuppa.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class HookTests {

    @BeforeMethod
    public void setup() {
        Cuppa.reset();
    }

    @Test
    public void beforeShouldRunOnceBeforeTests() {

        //Given
        Function topLevelBeforeFunction = mock(Function.class);
        Function nestedBeforeFunction = mock(Function.class);
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
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verify(topLevelBeforeFunction).apply();
        verify(nestedBeforeFunction).apply();
    }

    @Test
    public void multipleBeforeHooksShouldRunInOrderOfDefinition() {

        //Given
        Function firstBeforeFunction = mock(Function.class);
        Function secondBeforeFunction = mock(Function.class);
        {
            describe("before blocks", () -> {
                before(firstBeforeFunction);
                before(secondBeforeFunction);
                it("runs the third test", () -> {
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verifiedCalledInOrder(firstBeforeFunction, secondBeforeFunction);
    }

    @Test
    public void afterShouldRunOnceAfterTests() {

        //Given
        Function topLevelAfterFunction = mock(Function.class);
        Function nestedAfterFunction = mock(Function.class);
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
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verify(topLevelAfterFunction).apply();
        verify(nestedAfterFunction).apply();
    }

    @Test
    public void multipleAfterHooksShouldRunInOrderOfDefinition() {

        //Given
        Function firstAfterFunction = mock(Function.class);
        Function secondAfterFunction = mock(Function.class);
        {
            describe("before blocks", () -> {
                after(firstAfterFunction);
                after(secondAfterFunction);
                it("runs the third test", () -> {
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verifiedCalledInOrder(firstAfterFunction, secondAfterFunction);
    }

    @Test
    public void beforeEachShouldRunBeforeEachTest() {

        //Given
        Function topLevelBeforeEachFunction = mock(Function.class);
        Function nestedBeforeEachFunction = mock(Function.class);
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
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verify(topLevelBeforeEachFunction, times(3)).apply();
        verify(nestedBeforeEachFunction, times(2)).apply();
    }

    @Test
    public void multipleBeforeEachHooksShouldRunInOrderOfDefinition() {

        //Given
        Function firstBeforeEachFunction = mock(Function.class);
        Function secondBeforeEachFunction = mock(Function.class);
        {
            describe("before blocks", () -> {
                beforeEach(firstBeforeEachFunction);
                beforeEach(secondBeforeEachFunction);
                it("runs the third test", () -> {
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verifiedCalledInOrder(firstBeforeEachFunction, secondBeforeEachFunction);
    }

    @Test
    public void afterEachShouldRunAfterEachTest() {

        //Given
        Function topLevelAfterEachFunction = mock(Function.class);
        Function nestedAfterEachFunction = mock(Function.class);
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
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verify(topLevelAfterEachFunction, times(3)).apply();
        verify(nestedAfterEachFunction, times(2)).apply();
    }

    @Test
    public void multipleAfterEachHooksShouldRunInOrderOfDefinition() {

        //Given
        Function firstAfterEachFunction = mock(Function.class);
        Function secondAfterEachFunction = mock(Function.class);
        {
            describe("before blocks", () -> {
                afterEach(firstAfterEachFunction);
                afterEach(secondAfterEachFunction);
                it("runs the third test", () -> {
                });
            });
        }

        //When
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verifiedCalledInOrder(firstAfterEachFunction, secondAfterEachFunction);
    }

    @Test
    public void shouldRunAllHooksInTheCorrectOrder() {

        //Given
        Function topLevelBeforeFunction = mock(Function.class, "topLevelBeforeFunction");
        Function topLevelBeforeEachFunction = mock(Function.class, "topLevelBeforeEachFunction");
        Function topLevelAfterEachFunction = mock(Function.class, "topLevelAfterEachFunction");
        Function topLevelAfterFunction = mock(Function.class, "topLevelAfterFunction");
        Function nestedBeforeFunction = mock(Function.class, "nestedBeforeFunction");
        Function nestedBeforeEachFunction = mock(Function.class, "nestedBeforeEachFunction");
        Function nestedAfterEachFunction = mock(Function.class, "nestedAfterEachFunction");
        Function nestedAfterFunction = mock(Function.class, "nestedAfterFunction");
        Function testFunction = mock(Function.class, "testFunction");
        Function nestedTestFunction = mock(Function.class, "nestedTestFunction");

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
        Cuppa.runTests(mock(Reporter.class));

        //Then
        verifiedCalledInOrder(topLevelBeforeFunction, topLevelBeforeEachFunction, testFunction,
                topLevelAfterEachFunction, nestedBeforeFunction, topLevelBeforeEachFunction, nestedBeforeEachFunction,
                nestedTestFunction, nestedAfterEachFunction, topLevelAfterEachFunction, nestedAfterFunction,
                topLevelAfterFunction);
    }

    private void verifiedCalledInOrder(Function... functions) {
        InOrder inOrder = inOrder(functions);
        Arrays.stream(functions).forEach((f) -> inOrder.verify(f).apply());
    }
}
