package org.forgerock.cuppa;

class HookException extends RuntimeException {

    private final DescribeBlock describeBlock;

    HookException(DescribeBlock describeBlock, Throwable cause) {
        super(cause);
        this.describeBlock = describeBlock;
    }

    DescribeBlock getDescribeBlock() {
        return describeBlock;
    }
}
