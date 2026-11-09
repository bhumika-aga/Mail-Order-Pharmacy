-- Stock releases already applied, by the caller's release ID. A release that is
-- retried (e.g. its reply was lost) is recognised here and not applied twice.
CREATE TABLE IF NOT EXISTS stock_releases
(
    release_id  VARCHAR(64) PRIMARY KEY,
    drug_id     VARCHAR(255) NOT NULL,
    location    VARCHAR(255) NOT NULL,
    quantity    INTEGER      NOT NULL,
    released_at TIMESTAMP(6) NOT NULL
);
