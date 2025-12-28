package dev.zawarudo.holo.database;

/**
 * Result of executing a batch of SQL statements.
 *
 * @param attempted number of statements executed
 * @param affected  total rows affected, or -1 if unknown (driver returned SUCCESS_NO_INFO)
 */
public record BatchResult(int attempted, int affected) {

    /**
     * True if {@code affected} is a known, exact count.
     */
    public boolean isExact() {
        return affected >= 0;
    }

    /**
     * Returns {@code affected} if exact, otherwise throws.
     */
    public int affectedExact() {
        if (!isExact()) throw new IllegalStateException("Affected row count is unknown");
        return affected;
    }

    /**
     * Convenience factory for the "unknown count" case.
     */
    public static BatchResult unknown(int attempted) {
        return new BatchResult(attempted, -1);
    }
}