package org.forgerock.cuppa.reporters;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.forgerock.cuppa.Cuppa;
import org.forgerock.cuppa.Reporter;

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
     * Constructs a reporter that writes to the specified stream.
     *
     * @param stream A stream to write to.
     */
    public DefaultReporter(OutputStream stream) {
        this.stream = new PrintStream(stream);
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
                Cuppa.filterStackTrace(failure.cause);
                failure.cause.printStackTrace(stream);
            }
        }
    }

    @Override
    public void describeStart(String description) {
        stream.println(getIndent() + description);
        blockStack.addLast(description);
    }

    @Override
    public void describeEnd(String description) {
        blockStack.removeLast();
    }

    @Override
    public void testPass(String description) {
        passed++;
        stream.println(getIndent() + "✓ " + description);
    }

    @Override
    public void testError(String description, Throwable cause) {
        failed++;
        failures.add(new TestFailure(String.join(" ", blockStack) + " " + description, cause));
        stream.println(getIndent() + failures.size() + ") " + description);
    }

    @Override
    public void testFail(String description, AssertionError cause) {
        testError(description, cause);
    }

    @Override
    public void testPending(String description) {
        pending++;
        stream.println(getIndent() + "- " + description);
    }

    @Override
    public void testSkip(String description) {
        skipped++;
        stream.println(getIndent() + "- " + description);
    }

    private String getIndent() {
        return Stream.generate(() -> "  ").limit(blockStack.size()).collect(Collectors.joining());
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
