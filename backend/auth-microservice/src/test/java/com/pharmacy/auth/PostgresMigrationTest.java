package com.pharmacy.auth;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Starts the service with its real <b>prod</b> profile against a real PostgreSQL
 * (embedded, no install needed) and checks that the Flyway migrations both build a
 * fresh database and adopt the existing production schema (all services share one
 * database whose tables Hibernate created before Flyway) without losing data,
 * after which Hibernate's schema validation must pass.
 */
class PostgresMigrationTest {
    
    private static final String HISTORY_TABLE = "flyway_history_auth";
    private static EmbeddedPostgres postgres;
    private static int databaseCounter;
    
    @BeforeAll
    static void startPostgres() throws Exception {
        postgres = EmbeddedPostgres.start();
    }
    
    @AfterAll
    static void stopPostgres() throws Exception {
        postgres.close();
    }
    
    private static ConfigurableApplicationContext startProd(String jdbcUrl) {
        // Command-line arguments outrank the profile's YAML (e.g. sslmode=require).
        return new SpringApplicationBuilder(AuthMicroserviceApplication.class).run(
            "--spring.profiles.active=prod",
            "--PORT=0",
            "--DATABASE_URL=" + jdbcUrl,
            "--DATABASE_USERNAME=postgres",
            "--DATABASE_PASSWORD=postgres",
            "--spring.datasource.hikari.data-source-properties.sslmode=disable",
            "--JWT_SECRET=test-secret-0123456789-0123456789-0123456789-0123456789-0123456789",
            "--JWT_EXPIRATION=900000",
            "--APP_CORS_ALLOWED_ORIGINS=http://localhost:5173",
            "--SUBSCRIPTION_SERVICE_URL=http://localhost:8082",
            "--REFILL_SERVICE_URL=http://localhost:8083");
    }
    
    /**
     * A new, empty database on the embedded server, optionally pre-loaded with {@code sql}.
     */
    private static String newDatabase(String sql) throws Exception {
        String name = "db_" + (++databaseCounter);
        try (Connection c = postgres.getPostgresDatabase().getConnection(); Statement st = c.createStatement()) {
            st.execute("CREATE DATABASE " + name);
        }
        String url = postgres.getJdbcUrl("postgres", name);
        if (!sql.isBlank()) {
            try (Connection c = java.sql.DriverManager.getConnection(url, "postgres", "postgres");
                 Statement st = c.createStatement()) {
                st.execute(sql);
            }
        }
        return url;
    }
    
    private static List<String> tables(JdbcTemplate jdbc) {
        return jdbc.queryForList(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'", String.class);
    }
    
    private static List<String> appliedVersions(JdbcTemplate jdbc) {
        return jdbc.queryForList("SELECT version FROM " + HISTORY_TABLE
                                     + " WHERE success AND version IS NOT NULL ORDER BY installed_rank", String.class);
    }
    
    /**
     * Production must never receive the dev seed data (db/seed).
     */
    private static void assertNoSeedData(JdbcTemplate jdbc) {
        assertThat(jdbc.queryForObject("SELECT count(*) FROM " + HISTORY_TABLE
                                           + " WHERE script LIKE '%seed%'", Integer.class)).isZero();
    }
    
    /**
     * Supabase exposes public tables via its REST API unless RLS is on: no table may be left without it.
     */
    private static void assertRowLevelSecurityOnAllTables(JdbcTemplate jdbc) {
        assertThat(jdbc.queryForList(
            "SELECT tablename FROM pg_tables WHERE schemaname = 'public' AND NOT rowsecurity", String.class))
            .as("tables without row level security").isEmpty();
        assertThat(jdbc.queryForObject(
            "SELECT rowsecurity FROM pg_tables WHERE schemaname = 'public' AND tablename = '" + HISTORY_TABLE + "'",
            Boolean.class)).as("Flyway history table has RLS").isTrue();
    }
    
    @Test
    void migratesAFreshDatabase() throws Exception {
        try (ConfigurableApplicationContext app = startProd(newDatabase(""))) {
            JdbcTemplate jdbc = new JdbcTemplate(app.getBean(DataSource.class));
            assertThat(tables(jdbc)).contains("users");
            assertThat(appliedVersions(jdbc)).containsExactly("1", "2", "3");
            assertNoSeedData(jdbc);
            assertRowLevelSecurityOnAllTables(jdbc);
        }
    }
    
    @Test
    void adoptsTheExistingSharedProductionSchemaWithoutLosingData() throws Exception {
        String sql = """
            -- another service's table in the same (shared) database
            create table other_service_table (id integer primary key);
            create table users (created_at timestamp(6), id bigint generated by default as identity, updated_at timestamp(6), email varchar(255) not null unique, full_name varchar(255) not null, member_id varchar(255) unique, password varchar(255) not null, username varchar(255) not null unique, primary key (id));
            insert into users (username, email, password, member_id, full_name) values ('existing', 'existing@example.com', 'x', 'MEM123456', 'Existing User');
            """;
        try (ConfigurableApplicationContext app = startProd(newDatabase(sql))) {
            JdbcTemplate jdbc = new JdbcTemplate(app.getBean(DataSource.class));
            assertThat(jdbc.queryForObject("SELECT count(*) FROM users WHERE username = 'existing'", Integer.class)).isEqualTo(1);
            // baseline at 0 for the existing schema, then every migration runs (as a no-op where applied)
            assertThat(appliedVersions(jdbc)).containsExactly("0", "1", "2", "3");
            assertNoSeedData(jdbc);
            assertRowLevelSecurityOnAllTables(jdbc);
        }
    }
    
    @Test
    void keepsAUserRoleColumnThatHibernateAlreadyAdded() throws Exception {
        // A database where this release ran before Flyway (Hibernate added user_role itself).
        String sql = """
            create table users (created_at timestamp(6), id bigint generated by default as identity, updated_at timestamp(6), user_role varchar(20) check ((user_role in ('MEMBER','ADMIN'))), email varchar(255) not null unique, full_name varchar(255) not null, member_id varchar(255) unique, password varchar(255) not null, username varchar(255) not null unique, primary key (id));
            insert into users (username, email, password, member_id, full_name, user_role) values ('boss', 'boss@example.com', 'x', null, 'Boss', 'ADMIN');
            """;
        try (ConfigurableApplicationContext app = startProd(newDatabase(sql))) {
            JdbcTemplate jdbc = new JdbcTemplate(app.getBean(DataSource.class));
            assertThat(jdbc.queryForObject("SELECT user_role FROM users WHERE username = 'boss'", String.class))
                .isEqualTo("ADMIN");
        }
    }
    
    @Test
    void addsTheUserRoleColumnToThePreviousReleasesTable() throws Exception {
        try (ConfigurableApplicationContext app = startProd(newDatabase("""
            create table users (created_at timestamp(6), id bigint generated by default as identity, updated_at timestamp(6), email varchar(255) not null unique, full_name varchar(255) not null, member_id varchar(255) unique, password varchar(255) not null, username varchar(255) not null unique, primary key (id));
            insert into users (username, email, password, member_id, full_name) values ('existing', 'existing@example.com', 'x', 'MEM123456', 'Existing User');
            """))) {
            JdbcTemplate jdbc = new JdbcTemplate(app.getBean(DataSource.class));
            // existing users have no role yet, which the application treats as MEMBER
            assertThat(jdbc.queryForObject("SELECT user_role FROM users WHERE username = 'existing'", String.class))
                .isNull();
        }
    }
    
    @Test
    void addsCaseInsensitiveKeysEvenWhenCaseDuplicatesAlreadyExist() throws Exception {
        // Production data registered before uniqueness ignored case.
        String sql = """
            create table users (created_at timestamp(6), id bigint generated by default as identity, updated_at timestamp(6), email varchar(255) not null unique, full_name varchar(255) not null, member_id varchar(255) unique, password varchar(255) not null, username varchar(255) not null unique, primary key (id));
            insert into users (username, email, password, member_id, full_name) values ('Bob', 'Bob@Example.com', 'x', 'MEM000001', 'Bob One');
            insert into users (username, email, password, member_id, full_name) values ('bob', 'bob@example.com', 'x', 'MEM000002', 'Bob Two');
            insert into users (username, email, password, member_id, full_name) values ('carol', 'carol@example.com', 'x', 'MEM000003', 'Carol');
            """;
        try (ConfigurableApplicationContext app = startProd(newDatabase(sql))) {
            JdbcTemplate jdbc = new JdbcTemplate(app.getBean(DataSource.class));
            // Both Bobs keep their accounts; the oldest holds the keys.
            assertThat(jdbc.queryForList("SELECT username || ':' || COALESCE(username_key, '-') || ':' || COALESCE(email_key, '-') "
                                             + "FROM users ORDER BY id", String.class))
                .containsExactly("Bob:bob:bob@example.com", "bob:-:-", "carol:carol:carol@example.com");
            // The database itself now rejects another case-variant, whatever the application checked.
            assertThatThrownBy(() -> jdbc.update("INSERT INTO users (username, email, password, member_id, full_name, "
                                                     + "username_key, email_key) VALUES ('BOB', 'new@example.com', 'x', "
                                                     + "'MEM000004', 'Bob Three', 'bob', 'new@example.com')"))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
        }
    }
}
