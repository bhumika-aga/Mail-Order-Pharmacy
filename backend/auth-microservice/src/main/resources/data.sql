-- Development seed data (H2 only; production uses sql.init.mode: never).
-- All seed accounts use the password "password".
INSERT INTO users (username, email, password, member_id, full_name, user_role, created_at, updated_at)
VALUES ('member1', 'member1@pharmacy.com', '$2a$10$USgSbpLWz3gno1KkblHbl.cmaeRBHFxv3cX/6dCYKkfEFaaG8BHyC', 'MEM001',
        'Member One', 'MEMBER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('member2', 'member2@pharmacy.com', '$2a$10$USgSbpLWz3gno1KkblHbl.cmaeRBHFxv3cX/6dCYKkfEFaaG8BHyC', 'MEM002',
        'Member Two', 'MEMBER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('member3', 'member3@pharmacy.com', '$2a$10$USgSbpLWz3gno1KkblHbl.cmaeRBHFxv3cX/6dCYKkfEFaaG8BHyC', 'MEM003',
        'Member Three', 'MEMBER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('admin', 'admin@pharmacy.com', '$2a$10$USgSbpLWz3gno1KkblHbl.cmaeRBHFxv3cX/6dCYKkfEFaaG8BHyC', NULL,
        'Pharmacy Admin', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
