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

import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.it;
import static org.forgerock.cuppa.ModelFinder.findTest;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.forgerock.cuppa.reporters.Reporter;
import org.testng.annotations.Test;

public class DynamicDataTests extends AbstractTest {
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
