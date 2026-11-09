-- Reserved stock still to be returned to the drugs service (from cancelled or
-- erased orders, or an order that failed part-way). A row is written in the same
-- transaction as the change that frees the stock and deleted once the drugs
-- service confirms, so a release survives a drugs-service outage or a restart and
-- is retried until it succeeds. The drugs service ignores a release_id it has
-- already applied, so a retry never returns stock twice.
CREATE TABLE IF NOT EXISTS pending_stock_releases
(
    release_id      VARCHAR(64) PRIMARY KEY,
    refill_order_id VARCHAR(255) NOT NULL,
    drug_code       VARCHAR(255) NOT NULL,
    location        VARCHAR(255) NOT NULL,
    quantity        INTEGER      NOT NULL,
    created_at      TIMESTAMP(6) NOT NULL,
    attempts        INTEGER      NOT NULL,
    last_error      VARCHAR(500)
);
