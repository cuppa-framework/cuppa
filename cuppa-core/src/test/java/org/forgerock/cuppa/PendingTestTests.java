package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Cuppa.*;
import static org.forgerock.cuppa.Cuppa.when;
import static org.forgerock.cuppa.CuppaTestProvider.runTests;
import static org.forgerock.cuppa.ModelFinder.findTest;
import static org.mockito.Mockito.*;

import org.forgerock.cuppa.internal.TestContainer;
import org.forgerock.cuppa.reporters.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PendingTestTests {

    @BeforeMethod
    public void setup() {
        TestContainer.INSTANCE.reset();
    }

    @Test
    public void supportPendingTest() {

        //Given
        Reporter reporter = mock(Reporter.class);
        {
            describe("support pending tests", () -> {
                when("the 'when' block is run", () -> {
                    it("runs the first test, which passes", () -> {
                        assertThat(true).isTrue();
                    });
                    it("marks the second test as pending");
                    it("runs the third test, which passes", () -> {
                        assertThat(true).isTrue();
                    });
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter, times(2)).testPass(any());
        verify(reporter).testPending(findTest("marks the second test as pending"));
    }
}
