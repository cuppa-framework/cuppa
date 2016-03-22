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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.forgerock.cuppa.ReporterSupport;
import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;

/**
 * An RSpec-like reporter.
 */
public final class DefaultReporter implements Reporter {
    private final PrintStream stream;
    private final List<TestFailure> failures = new ArrayList<>();
    private int depth;
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
    public void start(TestBlock rootBlock) {
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
                stream.println("  " + (i + 1) + ") " + failure.description + ":");
                stream.print("     ");
                ReporterSupport.filterStackTrace(failure.cause);
                failure.cause.printStackTrace(stream);
            }
        }
    }

    @Override
    public void testBlockStart(TestBlock testBlock, List<TestBlock> parents) {
        stream.println(getIndent() + ReporterSupport.getDescription(testBlock));
        depth++;
    }

    @Override
    public void testBlockEnd(TestBlock testBlock, List<TestBlock> parents) {
        depth--;
    }

    @Override
    public void hookFail(Hook hook, List<TestBlock> parents, Throwable cause) {
        failed++;
        failures.add(new TestFailure(ReporterSupport.getFullDescription(hook, parents), cause));
        stream.println(getIndent() + failures.size() + ") " + ReporterSupport.getDescription(hook));
    }

    @Override
    public void testPass(Test test, List<TestBlock> parents) {
        passed++;
        stream.println(getIndent() + "✓ " + test.description);
    }

    @Override
    public void testFail(Test test, List<TestBlock> parents, Throwable cause) {
        failed++;
        failures.add(new TestFailure(ReporterSupport.getFullDescription(test, parents), cause));
        stream.println(getIndent() + failures.size() + ") " + test.description);
    }

    @Override
    public void testPending(Test test, List<TestBlock> parents) {
        pending++;
        stream.println(getIndent() + "- " + test.description);
    }

    @Override
    public void testSkip(Test test, List<TestBlock> parents) {
        skipped++;
        stream.println(getIndent() + "- " + test.description);
    }

    private String getIndent() {
        return Stream.generate(() -> "  ").limit(depth).collect(Collectors.joining());
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
