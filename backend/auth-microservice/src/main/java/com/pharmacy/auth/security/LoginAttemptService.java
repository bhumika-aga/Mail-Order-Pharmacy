package com.pharmacy.auth.security;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Locks out password guessing: after {@value #MAX_FAILURES} failed sign-ins for the
 * same username from the same client address within {@link #FAILURE_WINDOW}, further
 * attempts are refused for {@link #LOCK_DURATION}.
 * <p>
 * Keying on username AND address means an attacker cannot lock a real user out
 * from everywhere just by failing logins for their username. In-memory, so it
 * applies per service instance.
 */
@Component
public class LoginAttemptService {
    
    static final int MAX_FAILURES = 5;
    static final Duration FAILURE_WINDOW = Duration.ofMinutes(15);
    static final Duration LOCK_DURATION = Duration.ofMinutes(15);
    private static final int CLEANUP_THRESHOLD = 10_000;
    private final Clock clock;
    private final ConcurrentHashMap<String, Attempts> attempts = new ConcurrentHashMap<>();
    
    public LoginAttemptService(Clock clock) {
        this.clock = clock;
    }
    
    public static String key(String username, String clientAddress) {
        return (username == null ? "" : username.trim().toLowerCase()) + "|" + clientAddress;
    }
    
    /**
     * How much longer {@code key} stays locked, if it is locked.
     */
    public Optional<Duration> lockedFor(String key) {
        Attempts a = attempts.get(key);
        long now = clock.millis();
        if (a == null || a.lockedUntilMillis() <= now) {
            return Optional.empty();
        }
        return Optional.of(Duration.ofMillis(a.lockedUntilMillis() - now));
    }
    
    public void recordFailure(String key) {
        long now = clock.millis();
        if (attempts.size() > CLEANUP_THRESHOLD) {
            attempts.values().removeIf(a -> a.lockedUntilMillis() <= now
                                                && now - a.firstFailureMillis() >= FAILURE_WINDOW.toMillis());
        }
        attempts.compute(key, (k, a) -> {
            if (a == null || now - a.firstFailureMillis() >= FAILURE_WINDOW.toMillis()) {
                a = new Attempts(0, now, 0);
            }
            int failures = a.failures() + 1;
            long lockedUntil = failures >= MAX_FAILURES ? now + LOCK_DURATION.toMillis() : a.lockedUntilMillis();
            // after a lockout, start counting afresh
            return failures >= MAX_FAILURES ? new Attempts(0, now, lockedUntil)
                       : new Attempts(failures, a.firstFailureMillis(), lockedUntil);
        });
    }
    
    public void recordSuccess(String key) {
        attempts.remove(key);
    }
    
    private record Attempts(int failures, long firstFailureMillis, long lockedUntilMillis) {
    }
}
