-- When a pending stock release is next due. A release that keeps failing is
-- retried with growing gaps (1 minute, doubling, at most 1 hour) instead of every
-- minute indefinitely. Existing rows are due straight away.
ALTER TABLE pending_stock_releases
    ADD COLUMN IF NOT EXISTS next_attempt_at TIMESTAMP(6);
UPDATE pending_stock_releases
SET next_attempt_at = created_at
WHERE next_attempt_at IS NULL;
