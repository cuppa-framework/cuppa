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

package org.forgerock.cuppa.junit;

import java.util.List;

import org.forgerock.cuppa.ReporterSupport;
import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * A JUnit reporter that adapts between the Cuppa {@link Reporter} interface and JUnit's
 * {@link RunNotifier}.
 */
final class ReportJUnitAdapter implements Reporter {

    private final RunNotifier notifier;

    /**
     * Constructs a reporter that adapts events to JUnit.
     *
     * @param notifier The JUnit {@link RunNotifier} instance.
     */
    ReportJUnitAdapter(RunNotifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void hookError(Hook hook, List<TestBlock> parents, Throwable cause) {
        ReporterSupport.filterStackTrace(cause);
        notifier.fireTestFailure(new Failure(Description.createTestDescription(hook.testClass.getName(),
                ReporterSupport.getFullDescription(hook, parents)), cause));
    }

    @Override
    public void testStart(Test test, List<TestBlock> parents) {
        notifier.fireTestStarted(getDescription(test, parents));
    }

    @Override
    public void testEnd(Test test, List<TestBlock> parents) {
        notifier.fireTestFinished(getDescription(test, parents));
    }

    @Override
    public void testFail(Test test, List<TestBlock> parents, Throwable e) {
        ReporterSupport.filterStackTrace(e);
        notifier.fireTestFailure(new Failure(getDescription(test, parents), e));
    }

    @Override
    public void testPending(Test test, List<TestBlock> parents) {
        notifier.fireTestIgnored(getDescription(test, parents));
    }

    @Override
    public void testSkip(Test test, List<TestBlock> parents) {
        notifier.fireTestIgnored(getDescription(test, parents));
    }

    private Description getDescription(Test test, List<TestBlock> parents) {
        return Description.createTestDescription(test.testClass.getName(), test.description,
                ReporterSupport.getFullDescription(test, parents));
    }
}
