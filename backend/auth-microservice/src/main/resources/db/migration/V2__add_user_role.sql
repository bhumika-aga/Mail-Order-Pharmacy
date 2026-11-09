-- MEMBER / ADMIN roles. Existing users get NULL, which the application treats as
-- MEMBER. IF NOT EXISTS: databases already updated by Hibernate keep their column.
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS user_role VARCHAR(20) CHECK (user_role IN ('MEMBER', 'ADMIN'));
