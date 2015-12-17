package org.forgerock.cuppa.internal;

import org.forgerock.cuppa.model.TestBlock;

final class HookException extends RuntimeException {

    private final TestBlock testBlock;

    HookException(TestBlock testBlock, Throwable cause) {
        super(cause);
        this.testBlock = testBlock;
    }

    TestBlock getTestBlock() {
        return testBlock;
    }
}
