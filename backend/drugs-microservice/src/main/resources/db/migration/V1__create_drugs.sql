-- Baseline schema: the tables as Hibernate previously generated them.
-- IF NOT EXISTS makes this a no-op on databases that already have them (all four
-- services share one database, so Flyway baselines each service at version 0
-- and then runs this script).

CREATE TABLE IF NOT EXISTS drugs
(
    drug_id             VARCHAR(255)   NOT NULL PRIMARY KEY,
    drug_name           VARCHAR(255)   NOT NULL,
    manufacturer        VARCHAR(255)   NOT NULL,
    manufactured_date   DATE           NOT NULL,
    expiry_date         DATE           NOT NULL,
    units_per_package   INTEGER        NOT NULL,
    cost_per_package    NUMERIC(10, 2) NOT NULL,
    medical_composition TEXT           NOT NULL
);

CREATE TABLE IF NOT EXISTS drug_locations
(
    drug_id            VARCHAR(255) NOT NULL REFERENCES drugs (drug_id),
    location           VARCHAR(255) NOT NULL,
    quantity_available INTEGER      NOT NULL,
    PRIMARY KEY (drug_id, location)
);
