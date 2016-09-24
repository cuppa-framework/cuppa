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

import static org.forgerock.cuppa.model.Behaviour.NORMAL;
import static org.forgerock.cuppa.model.HookType.BEFORE;
import static org.forgerock.cuppa.model.TestBlockType.ROOT;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Options;
import org.forgerock.cuppa.model.TestBlock;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CompositeReporterTest {
    private Reporter mockReporter1;
    private Reporter mockReporter2;
    private InOrder order;
    private CompositeReporter reporter;
    private final TestBlock testBlock = new TestBlock(ROOT, NORMAL, CompositeReporterTest.class, "",
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Options.EMPTY);
    private final Hook hook = new Hook(BEFORE, CompositeReporterTest.class, Optional.empty(), () -> {
    });
    private final org.forgerock.cuppa.model.Test test = new org.forgerock.cuppa.model.Test(NORMAL,
            CompositeReporterTest.class, "", Optional.empty(), Options.EMPTY);
    private final List<TestBlock> parents = Collections.singletonList(testBlock);
    private final Exception cause = new Exception();

    @BeforeMethod
    public void setupReporters() {
        mockReporter1 = mock(Reporter.class, "mockReporter1");
        mockReporter2 = mock(Reporter.class, "mockReporter2");
        reporter = new CompositeReporter(Arrays.asList(mockReporter1, mockReporter2));
        order = inOrder(mockReporter1, mockReporter2);
    }

    @Test
    public void shouldCallStart() {
        reporter.start(testBlock);
        order.verify(mockReporter1).start(testBlock);
        order.verify(mockReporter2).start(testBlock);
    }

    @Test
    public void shouldCallEnd() {
        reporter.end();
        order.verify(mockReporter1).end();
        order.verify(mockReporter2).end();
    }

    @Test
    public void shouldCallTestBlockStart() {
        reporter.testBlockStart(testBlock, parents);
        order.verify(mockReporter1).testBlockStart(testBlock, parents);
        order.verify(mockReporter2).testBlockStart(testBlock, parents);
    }

    @Test
    public void shouldCallTestBlockEnd() {
        reporter.testBlockEnd(testBlock, parents);
        order.verify(mockReporter1).testBlockEnd(testBlock, parents);
        order.verify(mockReporter2).testBlockEnd(testBlock, parents);
    }

    @Test
    public void shouldCallBlockHookStart() {
        reporter.blockHookStart(hook, parents);
        order.verify(mockReporter1).blockHookStart(hook, parents);
        order.verify(mockReporter2).blockHookStart(hook, parents);
    }


    @Test
    public void shouldCallBlockHookPass() {
        reporter.blockHookPass(hook, parents);
        order.verify(mockReporter1).blockHookPass(hook, parents);
        order.verify(mockReporter2).blockHookPass(hook, parents);
    }
    @Test
    public void shouldCallBlockHookFail() {
        reporter.blockHookFail(hook, parents, cause);
        order.verify(mockReporter1).blockHookFail(hook, parents, cause);
        order.verify(mockReporter2).blockHookFail(hook, parents, cause);
    }

    @Test
    public void shouldCallTestHookStart() {
        reporter.testHookStart(hook, parents, test, parents);
        order.verify(mockReporter1).testHookStart(hook, parents, test, parents);
        order.verify(mockReporter2).testHookStart(hook, parents, test, parents);
    }

    @Test
    public void shouldCallTestHookPass() {
        reporter.testHookPass(hook, parents, test, parents);
        order.verify(mockReporter1).testHookPass(hook, parents, test, parents);
        order.verify(mockReporter2).testHookPass(hook, parents, test, parents);
    }

    @Test
    public void shouldCallTestHookFail() {
        reporter.testHookFail(hook, parents, test, parents, cause);
        order.verify(mockReporter1).testHookFail(hook, parents, test, parents, cause);
        order.verify(mockReporter2).testHookFail(hook, parents, test, parents, cause);
    }

    @Test
    public void shouldCallHookFail() {
        reporter.hookFail(hook, parents, cause);
        order.verify(mockReporter1).hookFail(hook, parents, cause);
        order.verify(mockReporter2).hookFail(hook, parents, cause);
    }

    @Test
    public void shouldCallTestStart() {
        reporter.testStart(test, parents);
        order.verify(mockReporter1).testStart(test, parents);
        order.verify(mockReporter2).testStart(test, parents);
    }

    @Test
    public void shouldCallTestEnd() {
        reporter.testEnd(test, parents);
        order.verify(mockReporter1).testEnd(test, parents);
        order.verify(mockReporter2).testEnd(test, parents);
    }

    @Test
    public void shouldCallTestPass() {
        reporter.testPass(test, parents);
        order.verify(mockReporter1).testPass(test, parents);
        order.verify(mockReporter2).testPass(test, parents);
    }

    @Test
    public void shouldCallTestFail() {
        reporter.testFail(test, parents, cause);
        order.verify(mockReporter1).testFail(test, parents, cause);
        order.verify(mockReporter2).testFail(test, parents, cause);
    }

    @Test
    public void shouldCallTestPending() {
        reporter.testPending(test, parents);
        order.verify(mockReporter1).testPending(test, parents);
        order.verify(mockReporter2).testPending(test, parents);
    }

    @Test
    public void shouldCallTestSkip() {
        reporter.testSkip(test, parents);
        order.verify(mockReporter1).testSkip(test, parents);
        order.verify(mockReporter2).testSkip(test, parents);
    }
}
