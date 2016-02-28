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

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.forgerock.cuppa.ReporterSupport;
import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.HookType;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;

/**
 * An RSpec-like reporter.
 */
public final class DefaultReporter implements Reporter {
    private final PrintStream stream;
    private final List<TestFailure> failures = new ArrayList<>();
    private final Deque<String> blockStack = new ArrayDeque<>();
    private int passed;
    private int failed;
    private int pending;
    private int skipped;

    /**
     * Constructs a reporter that writes to standard out.
     */
    public DefaultReporter() {
        this(System.out);
    }

    /**
     * Constructs a reporter that writes to the specified stream, using the JVM's default charset.
     *
     * @param stream A stream to write to.
     */
    public DefaultReporter(OutputStream stream) {
        try {
            this.stream = new PrintStream(stream, false, Charset.defaultCharset().toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("The JVM default charset is not supported!");
        }
    }

    /**
     * Constructs a reporter that writes to the specified stream.
     *
     * @param stream A stream to write to.
     * @param charset The charset to convert the output to.
     * @throws UnsupportedEncodingException If the charset is not supported by the JVM.
     */
    public DefaultReporter(OutputStream stream, Charset charset) throws UnsupportedEncodingException {
        this.stream = new PrintStream(stream, false, charset.toString());
    }

    @Override
    public void start() {
        stream.println();
    }

    @Override
    public void end() {
        stream.println();
        stream.println();
        stream.println("  " + passed + " passing");
        if (failed > 0) {
            stream.println("  " + failed + " failing");
        }
        if (pending + skipped > 0) {
            stream.println("  " + (pending + skipped) + " pending");
        }
        if (failed > 0) {
            stream.println();
            for (int i = 0; i < failures.size(); i++) {
                TestFailure failure = failures.get(i);
                stream.println("  " + (i + 1) + ")" + failure.description + ":");
                stream.print("     ");
                ReporterSupport.filterStackTrace(failure.cause);
                failure.cause.printStackTrace(stream);
            }
        }
    }

    @Override
    public void describeStart(TestBlock testBlock) {
        stream.println(getIndent() + testBlock.description);
        blockStack.addLast(testBlock.description);
    }

    @Override
    public void describeEnd(TestBlock testBlock) {
        blockStack.removeLast();
    }

    @Override
    public void hookError(Hook hook, Throwable cause) {
        String description = "\"" + getHookType(hook.type) + "\" hook";
        if (hook.description.isPresent()) {
            description += " \"" + hook.description.get() + "\"";
        }
        failed++;
        failures.add(new TestFailure(String.join(" ", blockStack) + " " + description, cause));
        stream.println(getIndent() + failures.size() + ") " + description);
    }

    @Override
    public void testStart(Test test) {
    }

    @Override
    public void testEnd(Test test) {
    }

    @Override
    public void testPass(Test test) {
        passed++;
        stream.println(getIndent() + "✓ " + test.description);
    }

    @Override
    public void testError(Test test, Throwable cause) {
        failed++;
        failures.add(new TestFailure(String.join(" ", blockStack) + " " + test.description, cause));
        stream.println(getIndent() + failures.size() + ") " + test.description);
    }

    @Override
    public void testFail(Test test, AssertionError cause) {
        testError(test, cause);
    }

    @Override
    public void testPending(Test test) {
        pending++;
        stream.println(getIndent() + "- " + test.description);
    }

    @Override
    public void testSkip(Test test) {
        skipped++;
        stream.println(getIndent() + "- " + test.description);
    }

    private String getIndent() {
        return Stream.generate(() -> "  ").limit(blockStack.size()).collect(Collectors.joining());
    }

    private String getHookType(HookType type) {
        switch (type) {
            case BEFORE:
                return "before";
            case BEFORE_EACH:
                return "before each";
            case AFTER_EACH:
                return "after each";
            case AFTER:
                return "after";
            default:
                throw new IllegalStateException("unknown hook type");
        }
    }

    private static final class TestFailure {
        private final String description;
        private final Throwable cause;

        private TestFailure(String description, Throwable cause) {
            this.description = description;
            this.cause = cause;
        }
    }
}
