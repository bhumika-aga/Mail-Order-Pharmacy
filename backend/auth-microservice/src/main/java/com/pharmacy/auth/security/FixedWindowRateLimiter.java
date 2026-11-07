package com.pharmacy.auth.security;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Allows at most {@code limit} events per key in each fixed time window.
 * In-memory: limits apply per service instance, which is enough for a single
 * instance; several instances would need a shared store (e.g. Redis).
 */
public class FixedWindowRateLimiter {

    private record Window(long startMillis, int count) {
    }

    private static final int CLEANUP_THRESHOLD = 10_000;

    private final int limit;
    private final long windowMillis;
    private final Clock clock;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int limit, Duration window, Clock clock) {
        this.limit = limit;
        this.windowMillis = window.toMillis();
        this.clock = clock;
    }

    /** Records one event for {@code key}; returns false if the key is over its limit. */
    public boolean tryAcquire(String key) {
        long now = clock.millis();
        if (windows.size() > CLEANUP_THRESHOLD) {
            windows.values().removeIf(w -> now - w.startMillis() >= windowMillis);
        }
        Window window = windows.compute(key, (k, current) ->
                                                 current == null || now - current.startMillis() >= windowMillis
                                                     ? new Window(now, 1)
                                                     : new Window(current.startMillis(), current.count() + 1));
        return window.count() <= limit;
    }

    /** Seconds until {@code key}'s current window ends (for a Retry-After header). */
    public long secondsUntilReset(String key) {
        Window window = windows.get(key);
        if (window == null) {
            return 0;
        }
        long remaining = window.startMillis() + windowMillis - clock.millis();
        return Math.max(1, (remaining + 999) / 1000);
    }
}
