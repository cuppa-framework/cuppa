package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThat;

final class Assertions {

    private Assertions() {
    }

    static void assertTestResources(TestResults results, int passed, int failed, int errored) {
        assertThat(results.getPassedTestsCount()).isEqualTo(passed);
        assertThat(results.getFailedTestsCount()).isEqualTo(failed);
        assertThat(results.getErroredTestsCount()).isEqualTo(errored);
    }
}
