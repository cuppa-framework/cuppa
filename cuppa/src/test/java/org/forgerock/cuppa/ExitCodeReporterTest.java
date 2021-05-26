/*
 * Copyright 2021 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

public class ExitCodeReporterTest {

    @Test
    public void whenNoTestsRunExitCodeIs4() {
        //Given
        ExitCodeReporter reporter = new ExitCodeReporter();

        //When
        reporter.blockHookFail(null, null, null);
        reporter.testHookFail(null, null, null, null, null);

        //Then
        assertThat(reporter.getExitCode()).isEqualTo(4);
    }

    @Test
    public void whenBlockHookFailReportedExitCodeIs1() {
        //Given
        ExitCodeReporter reporter = new ExitCodeReporter();

        //When
        reporter.testStart(null, null);
        reporter.blockHookFail(null, null, null);

        //Then
        assertThat(reporter.getExitCode()).isEqualTo(1);
    }

    @Test
    public void whenTestHookFailReportedExitCodeIs1() {
        //Given
        ExitCodeReporter reporter = new ExitCodeReporter();

        //When
        reporter.testStart(null, null);
        reporter.testHookFail(null, null, null, null, null);

        //Then
        assertThat(reporter.getExitCode()).isEqualTo(1);
    }

    @Test
    public void whenTestFailReportedExitCodeIs1() {
        //Given
        ExitCodeReporter reporter = new ExitCodeReporter();

        //When
        reporter.testStart(null, null);
        reporter.testFail(null, null, null);

        //Then
        assertThat(reporter.getExitCode()).isEqualTo(1);
    }

    @Test
    public void whenTestSkippedReportedExitCodeIs2() {
        //Given
        ExitCodeReporter reporter = new ExitCodeReporter();

        //When
        reporter.testStart(null, null);
        reporter.testSkip(null, null);

        //Then
        assertThat(reporter.getExitCode()).isEqualTo(2);
    }

    @Test
    public void whenTestPendingReportedExitCodeIs3() {
        //Given
        ExitCodeReporter reporter = new ExitCodeReporter();

        //When
        reporter.testStart(null, null);
        reporter.testPending(null, null);

        //Then
        assertThat(reporter.getExitCode()).isEqualTo(3);
    }

    @Test
    public void whenTestFailAndTestSkipReportedExitCodeIs1() {
        //Given
        ExitCodeReporter reporter = new ExitCodeReporter();

        //When
        reporter.testStart(null, null);
        reporter.testFail(null, null, null);
        reporter.testSkip(null, null);

        //Then
        assertThat(reporter.getExitCode()).isEqualTo(1);
    }

    @Test
    public void whenTestFailAndTestPendingReportedExitCodeIs1() {
        //Given
        ExitCodeReporter reporter = new ExitCodeReporter();

        //When
        reporter.testStart(null, null);
        reporter.testFail(null, null, null);
        reporter.testPending(null, null);

        //Then
        assertThat(reporter.getExitCode()).isEqualTo(1);
    }
}
