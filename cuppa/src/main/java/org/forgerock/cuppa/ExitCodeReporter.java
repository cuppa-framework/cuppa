/*
 * Copyright 2021 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package org.forgerock.cuppa;

import java.util.List;

import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;

/**
 * Reporter that tracks the outcome of hooks and tests to determine what the overall status of the test run
 * is.
 *
 * <p>The following list specifies the order in which the exit code is determined:
 * <ol>
 *     <li>If no tests are run the exit code will be {@literal 4}.</li>
 *     <li>If any block, test hook or test fails the exit code will be {@literal 1}.</li>
 *     <li>If any test is skipped the exit code will be {@literal 2}.</li>
 *     <li>If any test is pending the exit code will be {@literal 3}.</li>
 *     <li>If none of the above the exit code will be {@literal 0}.</li>
 * </ol>
 *
 * <p>Exit codes {@literal 0}, {@literal 2} and {@literal 3} could be seen as a successful run depending on
 * the configuration of the test run (i.e. transforms executed), this determination is up to the caller to
 * decide.
 */
public final class ExitCodeReporter implements Reporter {

    private static final int SUCCESS = 0;
    private static final int FAILED_TESTS = 1;
    private static final int SKIPPED_TESTS = 2;
    private static final int PENDING_TESTS = 3;
    private static final int NO_TESTS = 4;

    private boolean testsRun;
    private boolean failedTests;
    private boolean skippedTests;
    private boolean pendingTests;

    ExitCodeReporter() {
    }

    /**
     * Determines the exit code based on the results of the test run.
     *
     * @return The exit code.
     */
    int getExitCode() {
        int exitCode = SUCCESS;
        if (!testsRun) {
            exitCode = NO_TESTS;
        } else if (failedTests) {
            exitCode = FAILED_TESTS;
        } else if (skippedTests) {
            exitCode = SKIPPED_TESTS;
        } else if (pendingTests) {
            exitCode = PENDING_TESTS;
        }
        return exitCode;
    }

    @Override
    public void testHookFail(Hook hook, List<TestBlock> hookParents, Test test, List<TestBlock> testParents,
            Throwable cause) {
        failedTests = true;
    }

    @Override
    public void blockHookFail(Hook hook, List<TestBlock> parents, Throwable cause) {
        failedTests = true;
    }

    @Override
    public void testStart(Test test, List<TestBlock> parents) {
        testsRun = true;
    }

    @Override
    public void testFail(Test test, List<TestBlock> parents, Throwable cause) {
        failedTests = true;
    }

    @Override
    public void testPending(Test test, List<TestBlock> parents) {
        pendingTests = true;
    }

    @Override
    public void testSkip(Test test, List<TestBlock> parents) {
        skippedTests = true;
    }
}
