-- Members whose data was erased because their account was deleted. Tokens issued
-- to a member before their erasure are rejected, so a deleted user can't keep
-- using (or re-populating) their old member ID until the token expires.
-- Epoch milliseconds, so the comparison with the token's issue time can't be
-- skewed by time zones.
CREATE TABLE IF NOT EXISTS refill_erased_members
(
    member_id          VARCHAR(255) PRIMARY KEY,
    erased_at_epoch_ms BIGINT NOT NULL
);
