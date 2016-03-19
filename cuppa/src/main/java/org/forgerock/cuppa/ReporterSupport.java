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

package org.forgerock.cuppa;

import static org.forgerock.cuppa.model.TestBlockType.WHEN;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;

/**
 * Provides utility methods for reporters.
 */
public final class ReporterSupport {

    private ReporterSupport() {
    }

    /**
     * Get a short human-readable description for the given test block.
     *
     * <p>This description is only suitable for display where there is sufficient context to uniquely identify
     * the test block.</p>
     *
     * @param testBlock The test block.
     * @return A description of the test block.
     */
    public static String getDescription(TestBlock testBlock) {
        return (testBlock.type == WHEN) ? "when " + testBlock.description : testBlock.description;
    }

    /**
     * Get a short human-readable description for the given hook.
     *
     * <p>This description is only suitable for display where there is sufficient context to uniquely identify
     * the hook.</p>
     *
     * @param hook The hook.
     * @return A description of the hook.
     */
    public static String getDescription(Hook hook) {
        String description = "\"" + hook.type.description + "\" hook";
        if (hook.description.isPresent()) {
            description += " \"" + hook.description.get() + "\"";
        }
        return description;
    }

    private static String getDescription(List<TestBlock> testBlocks) {
        return testBlocks.stream().map(ReporterSupport::getDescription).collect(Collectors.joining(" ")).trim();
    }

    /**
     * Get a full human-readable description for the given test block.
     *
     * <p>This description is verbose but is suitable for display where there is no context to help identify the test
     * block.</p>
     *
     * @param testBlock The test block.
     * @param parents The ancestor test blocks, starting with the root block and ending with the immediate parent.
     * @return A description of the test block.
     */
    public static String getFullDescription(TestBlock testBlock, List<TestBlock> parents) {
        return getDescription(Stream.concat(parents.stream(), Stream.of(testBlock)).collect(Collectors.toList()));
    }

    /**
     * Get a full human-readable description for the given test.
     *
     * <p>This description is verbose but is suitable for display where there is no context to help identify the
     * test.</p>
     *
     * @param test The test.
     * @param parents The ancestor test blocks, starting with the root block and ending with the immediate parent.
     * @return A description of the test.
     */
    public static String getFullDescription(Test test, List<TestBlock> parents) {
        return getDescription(parents) + " " + test.description;
    }

    /**
     * Get a full human-readable description for the given hook.
     *
     * <p>This description is verbose but is suitable for display where there is no context to help identify the
     * hook.</p>
     *
     * @param hook Test hook.
     * @param parents The ancestor test blocks, starting with the root block and ending with the immediate parent.
     * @return A description of the hook.
     */
    public static String getFullDescription(Hook hook, List<TestBlock> parents) {
        return getDescription(parents) + " " + getDescription(hook);
    }

    /**
     * Modify a {@link Throwable}'s stacktrace by removing any stack elements that are not relevant
     * to a test. If the {@link Throwable} has a cause, it will also be modified. The modification
     * is applied to all transitive causes.
     *
     * @param throwable a throwable to modify.
     */
    public static void filterStackTrace(Throwable throwable) {
        throwable.setStackTrace(filterStackTrace(throwable.getStackTrace()));
        if (throwable.getCause() != null) {
            filterStackTrace(throwable.getCause());
        }
    }

    private static StackTraceElement[] filterStackTrace(StackTraceElement[] stackTraceElements) {
        StackTraceElement[] newStackTraceElements = getStackTraceUpToCuppaElements(stackTraceElements);
        if (newStackTraceElements.length > 0
                && isStackTraceElementUseless(newStackTraceElements[newStackTraceElements.length - 1])) {
            newStackTraceElements = Arrays.copyOf(newStackTraceElements, newStackTraceElements.length - 1);
        }
        if (newStackTraceElements.length > 0
                && isStackTraceElementForLambda(newStackTraceElements[newStackTraceElements.length - 1])) {
            StackTraceElement oldElement = newStackTraceElements[newStackTraceElements.length - 1];
            newStackTraceElements[newStackTraceElements.length - 1] =
                    new StackTraceElement(oldElement.getClassName(), "<cuppa test>", oldElement.getFileName(),
                            oldElement.getLineNumber());
        }
        return newStackTraceElements;
    }

    private static StackTraceElement[] getStackTraceUpToCuppaElements(StackTraceElement[] stackTraceElements) {
        Optional<StackTraceElement> first = Arrays.stream(stackTraceElements)
                .filter(s -> s.getClassName().startsWith(Cuppa.class.getPackage().getName()))
                .findFirst();
        if (first.isPresent()) {
            int index = Arrays.asList(stackTraceElements).indexOf(first.get());
            return Arrays.copyOf(stackTraceElements, index);
        }
        return stackTraceElements;
    }

    private static boolean isStackTraceElementUseless(StackTraceElement element) {
        return element.getFileName() == null;
    }

    private static boolean isStackTraceElementForLambda(StackTraceElement element) {
        return element.getMethodName().startsWith("lambda$");
    }
}
