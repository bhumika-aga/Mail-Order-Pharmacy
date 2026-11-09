-- Baseline schema: the tables as Hibernate previously generated them.
-- IF NOT EXISTS makes this a no-op on databases that already have them (all four
-- services share one database, so Flyway baselines each service at version 0
-- and then runs this script).

CREATE TABLE IF NOT EXISTS member_prescriptions
(
    prescription_id         VARCHAR(255) NOT NULL PRIMARY KEY,
    member_id               VARCHAR(255) NOT NULL,
    insurance_policy_number VARCHAR(255) NOT NULL,
    insurance_provider      VARCHAR(255) NOT NULL,
    prescription_date       DATE         NOT NULL,
    drug_id                 VARCHAR(255) NOT NULL,
    dosage_per_day          VARCHAR(255) NOT NULL,
    prescription_course     INTEGER      NOT NULL,
    doctor_details          TEXT         NOT NULL
);

CREATE TABLE IF NOT EXISTS member_subscriptions
(
    subscription_id     VARCHAR(255) NOT NULL PRIMARY KEY,
    member_id           VARCHAR(255) NOT NULL,
    subscription_date   DATE         NOT NULL,
    prescription_id     VARCHAR(255) NOT NULL REFERENCES member_prescriptions (prescription_id),
    refill_frequency    VARCHAR(50)  NOT NULL CHECK (refill_frequency IN ('WEEKLY', 'MONTHLY')),
    member_location     VARCHAR(255) NOT NULL,
    subscription_status VARCHAR(50)  NOT NULL CHECK (subscription_status IN ('ACTIVE', 'INACTIVE'))
);
