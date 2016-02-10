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

import java.util.ArrayDeque;
import java.util.Deque;

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
    private final Deque<String> blockStack = new ArrayDeque<>();

    /**
     * Constructs a reporter that adapts events to Surefire.
     *
     * @param listener The JUnit {@link RunListener} instance.
     */
    CuppaSurefireReporter(RunListener listener) {
        this.listener = listener;
    }

    @Override
    public void start() {
        listener.testSetStarting(new SimpleReportEntry(CuppaSurefireProvider.class.getName(), "Cuppa"));
    }

    @Override
    public void end() {
        listener.testSetCompleted(new SimpleReportEntry(CuppaSurefireProvider.class.getName(), "Cuppa"));
    }

    @Override
    public void describeStart(TestBlock testBlock) {
        blockStack.addLast(testBlock.description);
    }

    @Override
    public void describeEnd(TestBlock testBlock) {
        blockStack.removeLast();
    }

    @Override
    public void hookError(Hook hook, Throwable cause) {
    }

    @Override
    public void testStart(Test test) {
        listener.testStarting(new SimpleReportEntry(test.testClass.getCanonicalName(), getFullTestDescription(test)));
    }

    @Override
    public void testEnd(Test test) {
    }

    @Override
    public void testPass(Test test) {
        listener.testSucceeded(new SimpleReportEntry(test.testClass.getCanonicalName(), getFullTestDescription(test)));
    }

    @Override
    public void testFail(Test test, AssertionError cause) {
        ReporterSupport.filterStackTrace(cause);
        String description = getFullTestDescription(test);
        listener.testFailed(new SimpleReportEntry(test.testClass.getCanonicalName(), description,
                new PojoStackTraceWriter(test.testClass.getCanonicalName(), description, cause), 0));
    }

    @Override
    public void testError(Test test, Throwable cause) {
        ReporterSupport.filterStackTrace(cause);
        String description = getFullTestDescription(test);
        listener.testError(new SimpleReportEntry(test.testClass.getCanonicalName(), description,
                new PojoStackTraceWriter(test.testClass.getCanonicalName(), description, cause), 0));
    }

    @Override
    public void testPending(Test test) {
        listener.testSkipped(new SimpleReportEntry(test.testClass.getCanonicalName(), getFullTestDescription(test)));
    }

    @Override
    public void testSkip(Test test) {
        listener.testSkipped(new SimpleReportEntry(test.testClass.getCanonicalName(), getFullTestDescription(test)));
    }

    private String getFullTestDescription(Test test) {
        return (String.join(" ", blockStack) + " " + test.description).trim();
    }
}
