package org.forgerock.cuppa;

class BeforeEachException extends RuntimeException {
    private final DescribeBlock describeBlock;

    BeforeEachException(DescribeBlock describeBlock, Throwable cause) {
        super(cause);
        this.describeBlock = describeBlock;
    }

    DescribeBlock getDescribeBlock() {
        return describeBlock;
    }
}
