package com.pharmacy.auth.security;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;

class BruteForceProtectionTest {
    
    @Test
    void locksSignInAfterFiveFailuresAndUnlocksAfterFifteenMinutes() {
        TestClock clock = new TestClock();
        LoginAttemptService attempts = new LoginAttemptService(clock);
        String key = LoginAttemptService.key("Member1", "203.0.113.7");
        
        for (int i = 0; i < 4; i++) {
            attempts.recordFailure(key);
        }
        assertThat(attempts.lockedFor(key)).isEmpty();
        
        attempts.recordFailure(key); // 5th failure
        assertThat(attempts.lockedFor(key)).isPresent();
        
        clock.advance(Duration.ofMinutes(14));
        assertThat(attempts.lockedFor(key)).isPresent();
        clock.advance(Duration.ofMinutes(2));
        assertThat(attempts.lockedFor(key)).isEmpty();
    }
    
    @Test
    void lockoutIsPerUsernameAndAddressAndCaseInsensitive() {
        LoginAttemptService attempts = new LoginAttemptService(new TestClock());
        for (int i = 0; i < 5; i++) {
            attempts.recordFailure(LoginAttemptService.key("member1", "198.51.100.1"));
        }
        assertThat(attempts.lockedFor(LoginAttemptService.key("MEMBER1", "198.51.100.1"))).isPresent();
        // The real user signing in from elsewhere is not locked out by an attacker.
        assertThat(attempts.lockedFor(LoginAttemptService.key("member1", "192.0.2.50"))).isEmpty();
    }
    
    @Test
    void successfulSignInClearsEarlierFailures() {
        LoginAttemptService attempts = new LoginAttemptService(new TestClock());
        String key = LoginAttemptService.key("member1", "198.51.100.1");
        for (int i = 0; i < 4; i++) {
            attempts.recordFailure(key);
        }
        attempts.recordSuccess(key);
        attempts.recordFailure(key);
        assertThat(attempts.lockedFor(key)).isEmpty();
    }
    
    @Test
    void failuresSpreadBeyondTheWindowDoNotLock() {
        TestClock clock = new TestClock();
        LoginAttemptService attempts = new LoginAttemptService(clock);
        String key = LoginAttemptService.key("member1", "198.51.100.1");
        for (int i = 0; i < 4; i++) {
            attempts.recordFailure(key);
        }
        clock.advance(Duration.ofMinutes(16)); // window expired: counting restarts
        attempts.recordFailure(key);
        assertThat(attempts.lockedFor(key)).isEmpty();
    }
    
    @Test
    void rateLimiterAllowsLimitPerWindowThenResets() {
        TestClock clock = new TestClock();
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(3, Duration.ofMinutes(1), clock);
        
        assertThat(limiter.tryAcquire("ip")).isTrue();
        assertThat(limiter.tryAcquire("ip")).isTrue();
        assertThat(limiter.tryAcquire("ip")).isTrue();
        assertThat(limiter.tryAcquire("ip")).isFalse();
        assertThat(limiter.tryAcquire("other-ip")).isTrue();
        assertThat(limiter.secondsUntilReset("ip")).isBetween(1L, 60L);
        
        clock.advance(Duration.ofSeconds(61));
        assertThat(limiter.tryAcquire("ip")).isTrue();
    }
    
    /**
     * A clock the test can move forward.
     */
    private static final class TestClock extends Clock {
        private Instant now = Instant.parse("2026-11-07T10:00:00Z");
        
        void advance(Duration d) {
            now = now.plus(d);
        }
        
        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }
        
        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }
        
        @Override
        public Instant instant() {
            return now;
        }
    }
}
