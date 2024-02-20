package dev.zawarudo.holo.utils;

/**
 * A wrapper class around the unstable API.
 */
@SuppressWarnings({"UnstableApiUsage", "unused"})
public class RateLimiter {

    private final com.google.common.util.concurrent.RateLimiter rateLimiter;

    /**
     * Creates an object with the specified permits per second.
     *
     * @param permitsPerSecond Number of times an action can be executed. Must be a positive non-null value.
     */
    public RateLimiter(int permitsPerSecond) {
        if (permitsPerSecond < 1) {
            throw new IllegalArgumentException("Parameter value must be greater than zero! Given value: " + permitsPerSecond);
        }
        rateLimiter = com.google.common.util.concurrent.RateLimiter.create(permitsPerSecond);
    }

    /**
     * Acquires a single permit. Blocking until the permit has been acquired.
     */
    public void acquire() {
        rateLimiter.acquire();
    }

    /**
     * Acquires a specified amount of permits. Blocking until that number of permits has been acquired.
     *
     * @param amount Amount of permits to be acquired.
     */
    public void acquire(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Parameter value must be greater than zero! Given value: " + amount);
        }
        rateLimiter.acquire(amount);
    }

    public void tryAcquire() {
        rateLimiter.tryAcquire();
    }

    public void tryAcquire(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Parameter value must be greater than zero! Given value: " + amount);
        }
        rateLimiter.tryAcquire(amount);
    }
}