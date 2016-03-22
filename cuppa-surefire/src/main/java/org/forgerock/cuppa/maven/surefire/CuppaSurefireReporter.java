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

package org.forgerock.cuppa.maven.surefire;

import java.util.List;

import org.apache.maven.surefire.report.PojoStackTraceWriter;
import org.apache.maven.surefire.report.RunListener;
import org.apache.maven.surefire.report.SimpleReportEntry;
import org.forgerock.cuppa.ReporterSupport;
import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;

/**
 * Reporter for Maven Surefire and Failsafe plugins.
 */
final class CuppaSurefireReporter implements Reporter {

    private final RunListener listener;

    /**
     * Constructs a reporter that adapts events to Surefire's {@link RunListener}.
     *
     * @param listener The {@link RunListener} instance.
     */
    CuppaSurefireReporter(RunListener listener) {
        this.listener = listener;
    }

    @Override
    public void start(TestBlock rootBlock) {
        listener.testSetStarting(new SimpleReportEntry(CuppaSurefireProvider.class.getName(), "Cuppa"));
    }

    @Override
    public void end() {
        listener.testSetCompleted(new SimpleReportEntry(CuppaSurefireProvider.class.getName(), "Cuppa"));
    }

    @Override
    public void hookFail(Hook hook, List<TestBlock> parents, Throwable cause) {
        ReporterSupport.filterStackTrace(cause);
        String fullDescription = ReporterSupport.getFullDescription(hook, parents);
        String className = hook.testClass.getCanonicalName();
        listener.testError(new SimpleReportEntry(className, fullDescription,
                new PojoStackTraceWriter(className, fullDescription, cause), 0));
    }

    @Override
    public void testStart(Test test, List<TestBlock> parents) {
        listener.testStarting(new SimpleReportEntry(test.testClass.getCanonicalName(),
                ReporterSupport.getFullDescription(test, parents)));
    }

    @Override
    public void testPass(Test test, List<TestBlock> parents) {
        listener.testSucceeded(new SimpleReportEntry(test.testClass.getCanonicalName(),
                ReporterSupport.getFullDescription(test, parents)));
    }

    @Override
    public void testFail(Test test, List<TestBlock> parents, Throwable cause) {
        ReporterSupport.filterStackTrace(cause);
        String description = ReporterSupport.getFullDescription(test, parents);
        listener.testFailed(new SimpleReportEntry(test.testClass.getCanonicalName(), description,
                new PojoStackTraceWriter(test.testClass.getCanonicalName(), description, cause), 0));
    }

    @Override
    public void testPending(Test test, List<TestBlock> parents) {
        testSkip(test, parents);
    }

    @Override
    public void testSkip(Test test, List<TestBlock> parents) {
        listener.testSkipped(new SimpleReportEntry(test.testClass.getCanonicalName(),
                ReporterSupport.getFullDescription(test, parents)));
    }
}
