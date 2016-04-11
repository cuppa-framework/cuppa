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

package org.forgerock.cuppa.reporters;

import java.util.List;

import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;

/**
 * A strategy for reporting on a suite of test runs.
 */
public interface Reporter {

    /**
     * Called before any tests are run.
     *
     * @param rootBlock The root test block containing all the tests that will be run.
     */
    default void start(TestBlock rootBlock) {
    }

    /**
     * Called after all tests have been run.
     */
    default void end() {
    }

    /**
     * Called before any tests are run in a test block.
     *
     * @param testBlock The test block.
     * @param parents The ancestor test blocks, starting with the root block and ending with the immediate parent.
     */
    default void testBlockStart(TestBlock testBlock, List<TestBlock> parents) {
    }

    /**
     * Called after all tests in a test block have completed.
     *
     * @param testBlock The test block.
     * @param parents The ancestor test blocks, starting with the root block and ending with the immediate parent.
     */
    default void testBlockEnd(TestBlock testBlock, List<TestBlock> parents) {
    }

    /**
     * Called when a beforeEach or afterEach hook failed due to it throwing an exception.
     *
     * @param hook The hook that threw an exception.
     * @param hookParents The parent test blocks of the hook, starting with the root block and ending with the immediate
     *                    parent.
     * @param test The test that was associated with the beforeEach/afterEach.
     * @param testParents The parent test blocks of the test, starting with the root block and ending with the immediate
     *                    parent.
     * @param cause The throwable that the hook threw.
     */
    default void testHookFail(Hook hook, List<TestBlock> hookParents, Test test, List<TestBlock> testParents,
            Throwable cause) {
    }

    /**
     * Called when a before or after hook failed due to it throwing an exception.
     *
     * @param hook The hook that threw an exception.
     * @param parents The parent test blocks of the hook, starting with the root block and ending with the immediate
     *                parent.
     * @param cause The throwable that the hook threw.
     */
    default void blockHookFail(Hook hook, List<TestBlock> parents, Throwable cause) {
    }
    /**
     * Called when a hook failed due to it throwing an exception.
     *
     * @param hook The hook that threw an exception.
     * @param parents The ancestor test blocks, starting with the root block and ending with the immediate parent.
     * @param cause The throwable that the hook threw.
     * @deprecated Use {@link Reporter#testHookFail(Hook, List, Test, List, Throwable)} and
     *             {@link Reporter#blockHookFail(Hook, List, Throwable)} instead.
     */
    @Deprecated
    default void hookFail(Hook hook, List<TestBlock> parents, Throwable cause) {
    }

    /**
     * Called before a test is run.
     *
     * @param test The test that is being run.
     * @param parents The ancestor test blocks, starting with the root block and ending with the immediate parent.
     */
    default void testStart(Test test, List<TestBlock> parents) {
    }

    /**
     * Called after a test is run.
     *
     * @param test The test that has been run.
     * @param parents The ancestor test blocks, starting with the root block and ending with the immediate parent.
     */
    default void testEnd(Test test, List<TestBlock> parents) {
    }

    /**
     * Called after a test has successfully executed without throwing an exception.
     *
     * @param test The test that passed.
     * @param parents The ancestor test blocks, starting with the root block and ending with the immediate parent.
     */
    default void testPass(Test test, List<TestBlock> parents) {
    }

    /**
     * Called after a test has failed due to it throwing an exception.
     *
     * @param test The test that failed.
     * @param parents The ancestor test blocks, starting with the root block and ending with the immediate parent.
     * @param cause The assertion error that the test threw.
     */
    default void testFail(Test test, List<TestBlock> parents, Throwable cause) {
    }

    /**
     * Called when a test cannot be run as it has not yet been implemented.
     *
     * @param test The pending test.
     * @param parents The ancestor test blocks, starting with the root block and ending with the immediate parent.
     */
    default void testPending(Test test, List<TestBlock> parents) {
    }

    /**
     * Called when a test has been skipped.
     *
     * @param test The skipped test.
     * @param parents The ancestor test blocks, starting with the root block and ending with the immediate parent.
     */
    default void testSkip(Test test, List<TestBlock> parents) {
    }
}
