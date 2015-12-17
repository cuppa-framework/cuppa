package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.it;
import static org.forgerock.cuppa.CuppaTestProvider.runTests;
import static org.forgerock.cuppa.ModelFinder.findTest;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.forgerock.cuppa.internal.TestContainer;
import org.forgerock.cuppa.reporters.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DynamicDataTests {

    @BeforeMethod
    public void setup() {
        TestContainer.INSTANCE.reset();
    }

    @Test
    public void canCreateTestsDynamically() {

        //Given
        Reporter reporter = mock(Reporter.class);
        int[] testInputs = {1, 2, 3};
        {
            describe("dynamic data", () -> {
                Arrays.stream(testInputs).forEach((i) -> {
                    it("test " + i, () -> {
                        assertThat(i).isLessThan(3);
                    });
                });
            });
        }

        //When
        runTests(reporter);

        //Then
        verify(reporter).testPass(findTest("test 1"));
        verify(reporter).testPass(findTest("test 2"));
        verify(reporter).testFail(eq(findTest("test 3")), any(AssertionError.class));
    }
}
