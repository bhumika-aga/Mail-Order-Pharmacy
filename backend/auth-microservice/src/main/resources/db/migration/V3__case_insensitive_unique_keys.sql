-- Usernames and emails are unique regardless of case ("Bob" = "bob"). The
-- application checks this before saving, but two sign-ups at the same moment can
-- both pass that check; these lower-cased key columns let the database enforce it.
--
-- A plain unique index on LOWER(...) would fail to build if existing data already
-- holds case-variants of one name, so each name and email is given to its oldest
-- account. Any newer case-variant keeps working but gets no key (NULL is allowed
-- more than once); no new case-variant can be created.
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS username_key VARCHAR(255);
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS email_key VARCHAR(255);

UPDATE users u
SET username_key = LOWER(u.username)
WHERE NOT EXISTS (SELECT 1 FROM users o WHERE LOWER(o.username) = LOWER(u.username) AND o.id < u.id);

UPDATE users u
SET email_key = LOWER(u.email)
WHERE NOT EXISTS (SELECT 1 FROM users o WHERE LOWER(o.email) = LOWER(u.email) AND o.id < u.id);

ALTER TABLE users
    ADD CONSTRAINT uq_users_username_key UNIQUE (username_key);
ALTER TABLE users
    ADD CONSTRAINT uq_users_email_key UNIQUE (email_key);
