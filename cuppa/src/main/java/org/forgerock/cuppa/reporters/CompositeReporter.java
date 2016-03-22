/*
 * Copyright 2016 ForgeRock AS.
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
 * A reporter that delegates to list of other reporters.
 */
public final class CompositeReporter implements Reporter {
    private final List<Reporter> reporters;

    /**
     * Constructs a new composite reporter.
     *
     * @param reporters The reporters to delegate calls to.
     */
    public CompositeReporter(List<Reporter> reporters) {
        this.reporters = reporters;
    }

    @Override
    public void start(TestBlock rootBlock) {
        reporters.forEach(r -> r.start(rootBlock));
    }

    @Override
    public void end() {
        reporters.forEach(Reporter::end);
    }

    @Override
    public void testBlockStart(TestBlock testBlock, List<TestBlock> parents) {
        reporters.forEach(r -> r.testBlockStart(testBlock, parents));
    }

    @Override
    public void testBlockEnd(TestBlock testBlock, List<TestBlock> parents) {
        reporters.forEach(r -> r.testBlockEnd(testBlock, parents));
    }

    @Override
    public void hookFail(Hook hook, List<TestBlock> parents, Throwable cause) {
        reporters.forEach(r -> r.hookFail(hook, parents, cause));
    }

    @Override
    public void testStart(Test test, List<TestBlock> parents) {
        reporters.forEach(r -> r.testStart(test, parents));
    }

    @Override
    public void testEnd(Test test, List<TestBlock> parents) {
        reporters.forEach(r -> r.testEnd(test, parents));
    }

    @Override
    public void testPass(Test test, List<TestBlock> parents) {
        reporters.forEach(r -> r.testPass(test, parents));
    }

    @Override
    public void testFail(Test test, List<TestBlock> parents, Throwable cause) {
        reporters.forEach(r -> r.testFail(test, parents, cause));
    }

    @Override
    public void testPending(Test test, List<TestBlock> parents) {
        reporters.forEach(r -> r.testPending(test, parents));
    }

    @Override
    public void testSkip(Test test, List<TestBlock> parents) {
        reporters.forEach(r -> r.testSkip(test, parents));
    }
}
