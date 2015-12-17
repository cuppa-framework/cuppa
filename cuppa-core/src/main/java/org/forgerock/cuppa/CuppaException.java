package org.forgerock.cuppa;

/**
 * Thrown to indicate that a failure whilst running Cuppa tests.
 */
public final class CuppaException extends RuntimeException {

    /**
     * Constructs a new Cuppa exception with the specified cause.
     *
     * @param cause The cause. (A {@code null} value is permitted, and indicates that the cause
     *         is nonexistent or unknown.)
     */
    public CuppaException(Throwable cause) {
        super(cause);
    }
}
