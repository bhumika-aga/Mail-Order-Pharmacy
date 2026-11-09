package com.pharmacy.refill.security;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

/**
 * Members whose data was erased because their account was deleted (table
 * {@code refill_erased_members}). A token issued to such a member before the erasure must no
 * longer be accepted; one issued afterwards belongs to a new account that was
 * given the same member ID, and is fine.
 */
@Component
public class ErasedMembers {
    
    private final JdbcTemplate jdbc;
    private final Clock clock;
    
    public ErasedMembers(JdbcTemplate jdbc, Clock clock) {
        this.jdbc = jdbc;
        this.clock = clock;
    }
    
    /**
     * Records that the member's data is being erased now.
     */
    public void record(String memberId) {
        long now = clock.millis();
        if (jdbc.update("UPDATE refill_erased_members SET erased_at_epoch_ms = ? WHERE member_id = ?", now, memberId) == 0) {
            try {
                jdbc.update("INSERT INTO refill_erased_members (member_id, erased_at_epoch_ms) VALUES (?, ?)", memberId, now);
            } catch (DuplicateKeyException e) { // recorded concurrently
                jdbc.update("UPDATE refill_erased_members SET erased_at_epoch_ms = ? WHERE member_id = ?", now, memberId);
            }
        }
    }
    
    /**
     * Whether the member was erased after a token was issued to them at {@code issuedAt}.
     */
    public boolean erasedSince(String memberId, Instant issuedAt) {
        Long count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM refill_erased_members WHERE member_id = ? AND erased_at_epoch_ms >= ?",
            Long.class, memberId, issuedAt.toEpochMilli());
        return count != null && count > 0;
    }
}
