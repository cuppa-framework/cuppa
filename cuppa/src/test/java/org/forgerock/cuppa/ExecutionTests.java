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

package org.forgerock.cuppa;

import static org.forgerock.cuppa.Cuppa.*;
import static org.forgerock.cuppa.TestCuppaSupport.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExecutionTests {
    private Reporter reporter;
    private HookFunction outerBeforeFn1;
    private HookFunction outerBeforeFn2;
    private HookFunction outerAfterFn1;
    private HookFunction outerAfterFn2;
    private HookFunction outerBeforeEachFn1;
    private HookFunction outerBeforeEachFn2;
    private HookFunction outerAfterEachFn1;
    private HookFunction outerAfterEachFn2;
    private TestFunction outerTest1Fn;
    private TestFunction outerTest2Fn;
    private HookFunction nested1BeforeFn;
    private HookFunction nested1AfterFn;
    private HookFunction nested1BeforeEachFn;
    private HookFunction nested1AfterEachFn;
    private TestFunction nested1Test1Fn;
    private TestFunction nested1Test2Fn;
    private HookFunction nested2BeforeFn;
    private HookFunction nested2AfterFn;
    private HookFunction nested2BeforeEachFn;
    private HookFunction nested2AfterEachFn;
    private TestFunction nested2Test1Fn;
    private TestFunction nested2Test2Fn;

    private TestBlock root;
    private TestBlock outer;
    private TestBlock nested1;
    private TestBlock nested2;

    private org.forgerock.cuppa.model.Test outerTest1;
    private org.forgerock.cuppa.model.Test outerTest2;
    private org.forgerock.cuppa.model.Test nested1Test1;
    private org.forgerock.cuppa.model.Test nested1Test2;
    private org.forgerock.cuppa.model.Test nested2Test1;
    private org.forgerock.cuppa.model.Test nested2Test2;

    private List<TestBlock> parentsOfOuter;
    private List<TestBlock> parentsOfOuterChildren;
    private List<TestBlock> parentsOfNested1Children;
    private List<TestBlock> parentsOfNested2Children;

    private Hook outerBefore1;
    private Hook outerAfter1;
    private Hook outerBeforeEach1;
    private Hook outerAfterEach1;
    private Hook nested1Before;
    private Hook nested1After;
    private Hook nested1BeforeEach;
    private Hook nested1AfterEach;

    private InOrder order;

    private Exception exception;

    @BeforeMethod
    public void setupMocks() throws Exception {
        reporter = mock(Reporter.class, "reporter");
        outerBeforeFn1 = mock(HookFunction.class, "outerBeforeFn1");
        outerBeforeFn2 = mock(HookFunction.class, "outerBeforeFn2");
        outerAfterFn1 = mock(HookFunction.class, "outerAfterFn1");
        outerAfterFn2 = mock(HookFunction.class, "outerAfterFn2");
        outerBeforeEachFn1 = mock(HookFunction.class, "outerBeforeEachFn1");
        outerBeforeEachFn2 = mock(HookFunction.class, "outerBeforeEachFn2");
        outerAfterEachFn1 = mock(HookFunction.class, "outerAfterEachFn1");
        outerAfterEachFn2 = mock(HookFunction.class, "outerAfterEachFn2");
        outerTest1Fn = mock(TestFunction.class, "outerTest1Fn");
        outerTest2Fn = mock(TestFunction.class, "outerTest2Fn");
        nested1BeforeFn = mock(HookFunction.class, "nested1BeforeFn");
        nested1AfterFn = mock(HookFunction.class, "nested1AfterFn");
        nested1BeforeEachFn = mock(HookFunction.class, "nested1BeforeEachFn");
        nested1AfterEachFn = mock(HookFunction.class, "nested1AfterEachFn");
        nested1Test1Fn = mock(TestFunction.class, "nested1Test1Fn");
        nested1Test2Fn = mock(TestFunction.class, "nested1Test2Fn");
        nested2BeforeFn = mock(HookFunction.class, "nested2BeforeFn");
        nested2AfterFn = mock(HookFunction.class, "nested2AfterFn");
        nested2BeforeEachFn = mock(HookFunction.class, "nested2BeforeEachFn");
        nested2AfterEachFn = mock(HookFunction.class, "nested2AfterEachFn");
        nested2Test1Fn = mock(TestFunction.class, "nested2Test1Fn");
        nested2Test2Fn = mock(TestFunction.class, "nested2Test2Fn");
        exception = new Exception();

        root = defineTests(() -> {
            describe("outer", () -> {
                before("outerBefore1", outerBeforeFn1);
                before("outerBefore2", outerBeforeFn2);
                after("outerAfter1", outerAfterFn1);
                after("outerAfter2", outerAfterFn2);
                beforeEach("outerBeforeEach1", outerBeforeEachFn1);
                beforeEach("outerBeforeEach2", outerBeforeEachFn2);
                afterEach("outerAfterEach1", outerAfterEachFn1);
                afterEach("outerAfterEach2", outerAfterEachFn2);
                it("outerTest1", outerTest1Fn);
                it("outerTest2", outerTest2Fn);
                describe("nested1", () -> {
                    before("nested1Before", nested1BeforeFn);
                    after("nested1After", nested1AfterFn);
                    beforeEach("nested1BeforeEach", nested1BeforeEachFn);
                    afterEach("nested1AfterEach", nested1AfterEachFn);
                    it("nested1Test1", nested1Test1Fn);
                    it("nested1Test2", nested1Test2Fn);
                });
                describe("nested2", () -> {
                    before("nested2Before", nested2BeforeFn);
                    after("nested2After", nested2AfterFn);
                    beforeEach("nested2BeforeEach", nested2BeforeEachFn);
                    afterEach("nested2AfterEach", nested2AfterEachFn);
                    it("nested2Test1", nested2Test1Fn);
                    it("nested2Test2", nested2Test2Fn);
                });
            });
        });

        parentsOfOuter = Collections.singletonList(root);
        outer = findTestBlock(root, "outer");
        parentsOfOuterChildren = Arrays.asList(root, outer);
        nested1 = findTestBlock(root, "nested1");
        parentsOfNested1Children = Arrays.asList(root, outer, nested1);
        nested2 = findTestBlock(root, "nested2");
        parentsOfNested2Children = Arrays.asList(root, outer, nested2);

        outerTest1 = findTest(root, "outerTest1");
        outerTest2 = findTest(root, "outerTest2");
        nested1Test1 = findTest(root, "nested1Test1");
        nested1Test2 = findTest(root, "nested1Test2");
        nested2Test1 = findTest(root, "nested2Test1");
        nested2Test2 = findTest(root, "nested2Test2");

        outerBefore1 = findHook(root, "outerBefore1");
        outerAfter1 = findHook(root, "outerAfter1");
        outerBeforeEach1 = findHook(root, "outerBeforeEach1");
        outerAfterEach1 = findHook(root, "outerAfterEach1");
        nested1Before = findHook(root, "nested1Before");
        nested1After = findHook(root, "nested1After");
        nested1BeforeEach = findHook(root, "nested1BeforeEach");
        nested1AfterEach = findHook(root, "nested1AfterEach");

        order = inOrder(reporter, outerBeforeFn1, outerBeforeFn2, outerAfterFn1, outerAfterFn2,
                outerBeforeEachFn1, outerBeforeEachFn2, outerAfterEachFn1, outerAfterEachFn2, outerTest1Fn,
                outerTest2Fn, nested1BeforeFn, nested1AfterFn, nested1BeforeEachFn, nested1AfterEachFn,
                nested1Test1Fn, nested1Test2Fn, nested2BeforeFn, nested2AfterFn, nested2BeforeEachFn,
                nested2AfterEachFn, nested2Test1Fn, nested2Test2Fn);
    }

    @AfterMethod
    public void verifyNoMoreInteractionsWithMocks() {
        verifyNoMoreInteractions(reporter, outerBeforeFn1, outerBeforeFn2, outerAfterFn1, outerAfterFn2,
                outerBeforeEachFn1, outerBeforeEachFn2, outerAfterEachFn1, outerAfterEachFn2, outerTest1Fn,
                outerTest2Fn, nested1BeforeFn, nested1AfterFn, nested1BeforeEachFn, nested1AfterEachFn,
                nested1Test1Fn, nested1Test2Fn, nested2BeforeFn, nested2AfterFn, nested2BeforeEachFn,
                nested2AfterEachFn, nested2Test1Fn, nested2Test2Fn);
    }

    @Test
    public void verifyExecutionWithNoFailures() throws Exception {
        runTests(root, reporter);

        verifyStart();
        verifyNormalOuterExecution();
        verifyNormalNested1Execution();
        verifyNormalNested2Execution();
        order.verify(outerAfterFn1).apply();
        order.verify(outerAfterFn2).apply();
        verifyEnd();
    }

    @Test
    public void verifyExecutionWithOuterTestFailure() throws Exception {
        doThrow(exception).when(outerTest1Fn).apply();

        runTests(root, reporter);

        verifyStart();
        order.verify(outerBeforeFn1).apply();
        order.verify(outerBeforeFn2).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();

        order.verify(reporter).testStart(outerTest1, parentsOfOuterChildren);
        order.verify(outerTest1Fn).apply();
        order.verify(reporter).testFail(outerTest1, parentsOfOuterChildren, exception);
        order.verify(reporter).testEnd(outerTest1, parentsOfOuterChildren);
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();
        verifyNormalTestExecution(outerTest2, parentsOfOuterChildren);
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        verifyNormalNested1Execution();
        verifyNormalNested2Execution();
        order.verify(outerAfterFn1).apply();
        order.verify(outerAfterFn2).apply();
        verifyEnd();
    }

    @Test
    public void verifyExecutionWithOuterBeforeHookFailure() throws Exception {
        setupHookFailure(outerBeforeFn1);

        runTests(root, reporter);

        verifyStart();
        order.verify(outerBeforeFn1).apply();
        order.verify(reporter).blockHookFail(outerBefore1, parentsOfOuterChildren, exception);
        order.verify(reporter).testSkip(outerTest1, parentsOfOuterChildren);
        order.verify(reporter).testSkip(outerTest2, parentsOfOuterChildren);
        verifySkipNested1Execution();
        verifySkipNested2Execution();
        order.verify(outerAfterFn1).apply();
        order.verify(outerAfterFn2).apply();
        verifyEnd();
    }

    @Test
    public void verifyExecutionWithOuterAfterHookFailure() throws Exception {
        setupHookFailure(outerAfterFn1);

        runTests(root, reporter);

        verifyStart();
        verifyNormalOuterExecution();
        verifyNormalNested1Execution();
        verifyNormalNested2Execution();
        order.verify(outerAfterFn1).apply();
        order.verify(reporter).blockHookFail(outerAfter1, parentsOfOuterChildren, exception);
        verifyEnd();
    }

    @Test
    public void verifyExecutionWithOuterBeforeEachHookFailure() throws Exception {
        setupHookFailure(outerBeforeEachFn1);

        runTests(root, reporter);

        verifyStart();
        order.verify(outerBeforeFn1).apply();
        order.verify(outerBeforeFn2).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(reporter).testHookFail(outerBeforeEach1, parentsOfOuterChildren, outerTest1,
                parentsOfOuterChildren, exception);
        order.verify(reporter).testSkip(outerTest1, parentsOfOuterChildren);
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        order.verify(reporter).testSkip(outerTest2, parentsOfOuterChildren);
        verifySkipNested1Execution();
        verifySkipNested2Execution();
        order.verify(outerAfterFn1).apply();
        order.verify(outerAfterFn2).apply();
        verifyEnd();
    }

    @Test
    public void verifyExecutionWithOuterBeforeEachHookFailureOnSecondCall() throws Exception {
        setupHookFailure(outerBeforeEachFn1, 1);

        runTests(root, reporter);

        verifyStart();
        order.verify(outerBeforeFn1).apply();
        order.verify(outerBeforeFn2).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();
        verifyNormalTestExecution(outerTest1, parentsOfOuterChildren);
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(reporter).testHookFail(outerBeforeEach1, parentsOfOuterChildren, outerTest2,
                parentsOfOuterChildren, exception);
        order.verify(reporter).testSkip(outerTest2, parentsOfOuterChildren);
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        verifySkipNested1Execution();
        verifySkipNested2Execution();
        order.verify(outerAfterFn1).apply();
        order.verify(outerAfterFn2).apply();
        verifyEnd();
    }

    @Test
    public void verifyExecutionWithOuterBeforeEachHookFailureOnThirdCall() throws Exception {
        setupHookFailure(outerBeforeEachFn1, 2);

        runTests(root, reporter);

        verifyStart();
        verifyNormalOuterExecution();
        order.verify(reporter).testBlockStart(nested1, parentsOfOuterChildren);
        order.verify(nested1BeforeFn).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(reporter).testHookFail(outerBeforeEach1, parentsOfOuterChildren, nested1Test1,
                parentsOfNested1Children, exception);
        order.verify(reporter).testSkip(nested1Test1, parentsOfNested1Children);
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        order.verify(reporter).testSkip(nested1Test2, parentsOfNested1Children);
        order.verify(nested1AfterFn).apply();
        order.verify(reporter).testBlockEnd(nested1, parentsOfOuterChildren);
        verifySkipNested2Execution();
        order.verify(outerAfterFn1).apply();
        order.verify(outerAfterFn2).apply();
        verifyEnd();
    }

    @Test
    public void verifyExecutionWithOuterAfterEachHookFailure() throws Exception {
        setupHookFailure(outerAfterEachFn1);

        runTests(root, reporter);

        verifyStart();
        order.verify(outerBeforeFn1).apply();
        order.verify(outerBeforeFn2).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();
        verifyNormalTestExecution(outerTest1, parentsOfOuterChildren);
        order.verify(outerAfterEachFn1).apply();
        order.verify(reporter).testHookFail(outerAfterEach1, parentsOfOuterChildren, outerTest1, parentsOfOuterChildren,
                exception);
        order.verify(reporter).testSkip(outerTest2, parentsOfOuterChildren);
        verifySkipNested1Execution();
        verifySkipNested2Execution();
        order.verify(outerAfterFn1).apply();
        order.verify(outerAfterFn2).apply();
        verifyEnd();
    }

    @Test
    public void verifyExecutionWithNested1BeforeHookFailure() throws Exception {
        setupHookFailure(nested1BeforeFn);

        runTests(root, reporter);

        verifyStart();
        verifyNormalOuterExecution();
        order.verify(reporter).testBlockStart(nested1, parentsOfOuterChildren);
        order.verify(nested1BeforeFn).apply();
        order.verify(reporter).blockHookFail(nested1Before, parentsOfNested1Children, exception);
        order.verify(reporter).testSkip(nested1Test1, parentsOfNested1Children);
        order.verify(reporter).testSkip(nested1Test2, parentsOfNested1Children);
        order.verify(nested1AfterFn).apply();
        order.verify(reporter).testBlockEnd(nested1, parentsOfOuterChildren);
        verifyNormalNested2Execution();
        order.verify(outerAfterFn1).apply();
        order.verify(outerAfterFn2).apply();
        verifyEnd();
    }

    @Test
    public void verifyExecutionWithNested1AfterHookFailure() throws Exception {
        setupHookFailure(nested1AfterFn);

        runTests(root, reporter);

        verifyStart();
        verifyNormalOuterExecution();
        order.verify(reporter).testBlockStart(nested1, parentsOfOuterChildren);
        order.verify(nested1BeforeFn).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();
        order.verify(nested1BeforeEachFn).apply();
        verifyNormalTestExecution(nested1Test1, parentsOfNested1Children);
        order.verify(nested1AfterEachFn).apply();
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();
        order.verify(nested1BeforeEachFn).apply();
        verifyNormalTestExecution(nested1Test2, parentsOfNested1Children);
        order.verify(nested1AfterEachFn).apply();
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        order.verify(nested1AfterFn).apply();
        order.verify(reporter).blockHookFail(nested1After, parentsOfNested1Children, exception);
        order.verify(reporter).testBlockEnd(nested1, parentsOfOuterChildren);
        verifyNormalNested2Execution();
        order.verify(outerAfterFn1).apply();
        order.verify(outerAfterFn2).apply();
        verifyEnd();
    }

    @Test
    public void verifyExecutionWithNested1BeforeEachHookFailure() throws Exception {
        setupHookFailure(nested1BeforeEachFn);

        runTests(root, reporter);

        verifyStart();
        verifyNormalOuterExecution();
        order.verify(reporter).testBlockStart(nested1, parentsOfOuterChildren);
        order.verify(nested1BeforeFn).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();
        order.verify(nested1BeforeEachFn).apply();
        order.verify(reporter).testHookFail(nested1BeforeEach, parentsOfNested1Children, nested1Test1,
                parentsOfNested1Children, exception);
        order.verify(reporter).testSkip(nested1Test1, parentsOfNested1Children);
        order.verify(nested1AfterEachFn).apply();
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        order.verify(reporter).testSkip(nested1Test2, parentsOfNested1Children);
        order.verify(nested1AfterFn).apply();
        order.verify(reporter).testBlockEnd(nested1, parentsOfOuterChildren);
        verifyNormalNested2Execution();
        order.verify(outerAfterFn1).apply();
        order.verify(outerAfterFn2).apply();
        verifyEnd();
    }

    @Test
    public void verifyExecutionWithNested1AfterEachHookFailure() throws Exception {
        setupHookFailure(nested1AfterEachFn);

        runTests(root, reporter);

        verifyStart();
        verifyNormalOuterExecution();
        order.verify(reporter).testBlockStart(nested1, parentsOfOuterChildren);
        order.verify(nested1BeforeFn).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();
        order.verify(nested1BeforeEachFn).apply();
        verifyNormalTestExecution(nested1Test1, parentsOfNested1Children);
        order.verify(nested1AfterEachFn).apply();
        order.verify(reporter).testHookFail(nested1AfterEach, parentsOfNested1Children, nested1Test1,
                parentsOfNested1Children, exception);
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        order.verify(reporter).testSkip(nested1Test2, parentsOfNested1Children);
        order.verify(nested1AfterFn).apply();
        order.verify(reporter).testBlockEnd(nested1, parentsOfOuterChildren);
        verifyNormalNested2Execution();
        order.verify(outerAfterFn1).apply();
        order.verify(outerAfterFn2).apply();
        verifyEnd();
    }

    private void setupHookFailure(HookFunction f) throws Exception {
        setupHookFailure(f, 0);
    }

    private void setupHookFailure(HookFunction f, int goodCalls) throws Exception {
        doAnswer(new Answer() {
            private int calls;

            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (calls >= goodCalls) {
                    throw exception;
                }
                calls++;
                return null;
            }
        }).when(f).apply();
    }

    private void verifyNormalTestExecution(org.forgerock.cuppa.model.Test test, List<TestBlock> parents)
            throws Exception {
        order.verify(reporter).testStart(test, parents);
        order.verify(test.function.get()).apply();
        order.verify(reporter).testPass(test, parents);
        order.verify(reporter).testEnd(test, parents);
    }

    private void verifyNormalOuterExecution() throws Exception {
        order.verify(outerBeforeFn1).apply();
        order.verify(outerBeforeFn2).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();
        verifyNormalTestExecution(outerTest1, parentsOfOuterChildren);
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();
        verifyNormalTestExecution(outerTest2, parentsOfOuterChildren);
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
    }

    private void verifyNormalNested1Execution() throws Exception {
        order.verify(reporter).testBlockStart(nested1, parentsOfOuterChildren);
        order.verify(nested1BeforeFn).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();
        order.verify(nested1BeforeEachFn).apply();
        verifyNormalTestExecution(nested1Test1, parentsOfNested1Children);
        order.verify(nested1AfterEachFn).apply();
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();
        order.verify(nested1BeforeEachFn).apply();
        verifyNormalTestExecution(nested1Test2, parentsOfNested1Children);
        order.verify(nested1AfterEachFn).apply();
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        order.verify(nested1AfterFn).apply();
        order.verify(reporter).testBlockEnd(nested1, parentsOfOuterChildren);
    }

    private void verifyNormalNested2Execution() throws Exception {
        order.verify(reporter).testBlockStart(nested2, parentsOfOuterChildren);
        order.verify(nested2BeforeFn).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();
        order.verify(nested2BeforeEachFn).apply();
        verifyNormalTestExecution(nested2Test1, parentsOfNested2Children);
        order.verify(nested2AfterEachFn).apply();
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        order.verify(outerBeforeEachFn1).apply();
        order.verify(outerBeforeEachFn2).apply();
        order.verify(nested2BeforeEachFn).apply();
        verifyNormalTestExecution(nested2Test2, parentsOfNested2Children);
        order.verify(nested2AfterEachFn).apply();
        order.verify(outerAfterEachFn1).apply();
        order.verify(outerAfterEachFn2).apply();
        order.verify(nested2AfterFn).apply();
        order.verify(reporter).testBlockEnd(nested2, parentsOfOuterChildren);
    }

    private void verifyStart() {
        order.verify(reporter).start(root);
        order.verify(reporter).testBlockStart(root, Collections.emptyList());
        order.verify(reporter).testBlockStart(outer, parentsOfOuter);
    }

    private void verifyEnd() {
        order.verify(reporter).testBlockEnd(outer, parentsOfOuter);
        order.verify(reporter).testBlockEnd(root, Collections.emptyList());
        order.verify(reporter).end();
    }

    private void verifySkipNested1Execution() throws Exception {
        order.verify(reporter).testBlockStart(nested1, parentsOfOuterChildren);
        order.verify(reporter).testSkip(nested1Test1, parentsOfNested1Children);
        order.verify(reporter).testSkip(nested1Test2, parentsOfNested1Children);
        order.verify(reporter).testBlockEnd(nested1, parentsOfOuterChildren);
    }

    private void verifySkipNested2Execution() throws Exception {
        order.verify(reporter).testBlockStart(nested2, parentsOfOuterChildren);
        order.verify(reporter).testSkip(nested2Test1, parentsOfNested2Children);
        order.verify(reporter).testSkip(nested2Test2, parentsOfNested2Children);
        order.verify(reporter).testBlockEnd(nested2, parentsOfOuterChildren);
    }
}
