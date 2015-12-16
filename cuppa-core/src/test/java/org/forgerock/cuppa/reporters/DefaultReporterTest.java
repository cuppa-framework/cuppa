package org.forgerock.cuppa.reporters;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Behaviour.SKIP;
import static org.forgerock.cuppa.Cuppa.*;

import java.io.ByteArrayOutputStream;

import org.forgerock.cuppa.Cuppa;
import org.forgerock.cuppa.TestFunction;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DefaultReporterTest {

    @BeforeMethod
    public void setup() {
        Cuppa.reset();
    }

    @Test
    public void reporterShouldLookGreatForPassingTests() {

        //Given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Reporter reporter = new DefaultReporter(outputStream);
        {
            describe("describe", () -> {
                when("when", () -> {
                    it("passing test", TestFunction.identity());
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        String output = new String(outputStream.toByteArray(), UTF_8);
        String[] expectedLines = {
                "",
                "",
                "  describe",
                "    when",
                "      ✓ passing test",
                "",
                "",
                "  1 passing",
        };
        String expectedOutput = String.join(System.lineSeparator(), expectedLines);
        assertThat(output).startsWith(expectedOutput);
    }

    @Test
    public void reporterShouldLookGreatForFailingTests() {

        //Given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Reporter reporter = new DefaultReporter(outputStream);
        {
            describe("describe", () -> {
                when("when", () -> {
                    it("failing test", () -> {
                        assertThat(false).isTrue();
                    });
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        String output = new String(outputStream.toByteArray(), UTF_8);
        String[] expectedLines = {
                "",
                "",
                "  describe",
                "    when",
                "      1) failing test",
                "",
                "",
                "  0 passing",
                "  1 failing",
                "",
                "  1) describe when failing test:",
                "     org.junit.ComparisonFailure: expected:<[tru]e> but was:<[fals]e>",
        };
        String expectedOutput = String.join(System.lineSeparator(), expectedLines);
        assertThat(output).startsWith(expectedOutput);
    }

    @Test
    public void reporterShouldLookGreatForErroringTests() {

        //Given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Reporter reporter = new DefaultReporter(outputStream);
        {
            describe("describe", () -> {
                when("when", () -> {
                    it("erroring test", () -> {
                        throw new IllegalStateException();
                    });
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        String output = new String(outputStream.toByteArray(), UTF_8);
        String[] expectedLines = {
                "",
                "",
                "  describe",
                "    when",
                "      1) erroring test",
                "",
                "",
                "  0 passing",
                "  1 failing",
                "",
                "  1) describe when erroring test:",
                "     java.lang.IllegalStateException",
        };
        String expectedOutput = String.join(System.lineSeparator(), expectedLines);
        assertThat(output).startsWith(expectedOutput);
    }

    @Test
    public void reporterShouldLookGreatForPendingTests() {

        //Given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Reporter reporter = new DefaultReporter(outputStream);
        {
            describe("describe", () -> {
                when("when", () -> {
                    it("pending test");
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        String output = new String(outputStream.toByteArray(), UTF_8);
        String[] expectedLines = {
                "",
                "",
                "  describe",
                "    when",
                "      - pending test",
                "",
                "",
                "  0 passing",
                "  1 pending",
        };
        String expectedOutput = String.join(System.lineSeparator(), expectedLines);
        assertThat(output).startsWith(expectedOutput);
    }

    @Test
    public void reporterShouldLookGreatForSkippedTests() {

        //Given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Reporter reporter = new DefaultReporter(outputStream);
        {
            describe("describe", () -> {
                when("when", () -> {
                    it(SKIP, "skipped test", TestFunction.identity());
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        String output = new String(outputStream.toByteArray(), UTF_8);
        String[] expectedLines = {
                "",
                "",
                "  describe",
                "    when",
                "      - skipped test",
                "",
                "",
                "  0 passing",
                "  1 pending",
        };
        String expectedOutput = String.join(System.lineSeparator(), expectedLines);
        assertThat(output).startsWith(expectedOutput);
    }

    @Test
    public void reporterShouldLookGreatWithNoTests() {

        //Given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Reporter reporter = new DefaultReporter(outputStream);
        {
            describe("describe", () -> {
                when("when", () -> {
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        String output = new String(outputStream.toByteArray(), UTF_8);
        String[] expectedLines = {
                "",
                "",
                "  describe",
                "    when",
                "",
                "",
                "  0 passing",
        };
        String expectedOutput = String.join(System.lineSeparator(), expectedLines);
        assertThat(output).startsWith(expectedOutput);
    }

    @Test
    public void reporterShouldLookGreat() {

        //Given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Reporter reporter = new DefaultReporter(outputStream);
        {
            describe("describe", () -> {
                when("when", () -> {
                    it("passing test", TestFunction.identity());
                    it("failing test", () -> {
                        assertThat(false).isTrue();
                    });
                    it("erroring test", () -> {
                        throw new IllegalStateException();
                    });
                    it("pending test");
                    it(SKIP, "skipped test", TestFunction.identity());
                });
            });
        }

        //When
        Cuppa.runTests(reporter);

        //Then
        String output = new String(outputStream.toByteArray(), UTF_8);
        String[] expectedLines = {
                "",
                "",
                "  describe",
                "    when",
                "      ✓ passing test",
                "      1) failing test",
                "      2) erroring test",
                "      - pending test",
                "      - skipped test",
                "",
                "",
                "  1 passing",
                "  2 failing",
                "  2 pending",
                "",
                "  1) describe when failing test:",
                "     org.junit.ComparisonFailure: expected:<[tru]e> but was:<[fals]e>",
        };
        String expectedOutput = String.join(System.lineSeparator(), expectedLines);
        assertThat(output).startsWith(expectedOutput);
    }
}
