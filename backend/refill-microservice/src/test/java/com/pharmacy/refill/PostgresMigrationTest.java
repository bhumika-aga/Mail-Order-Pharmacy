package com.pharmacy.refill;

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

/**
 * Starts the service with its real <b>prod</b> profile against a real PostgreSQL
 * (embedded, no install needed) and checks that the Flyway migrations both build a
 * fresh database and adopt the existing production schema (all services share one
 * database whose tables Hibernate created before Flyway) without losing data,
 * after which Hibernate's schema validation must pass.
 */
class PostgresMigrationTest {
    
    private static final String HISTORY_TABLE = "flyway_history_refill";
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
        return new SpringApplicationBuilder(RefillMicroserviceApplication.class).run(
            "--spring.profiles.active=prod",
            "--PORT=0",
            "--DATABASE_URL=" + jdbcUrl,
            "--DATABASE_USERNAME=postgres",
            "--DATABASE_PASSWORD=postgres",
            "--spring.datasource.hikari.data-source-properties.sslmode=disable",
            "--JWT_SECRET=test-secret-0123456789-0123456789-0123456789-0123456789-0123456789",
            "--APP_CORS_ALLOWED_ORIGINS=http://localhost:5173",
            "--DRUGS_SERVICE_URL=http://localhost:8081",
            "--SUBSCRIPTION_SERVICE_URL=http://localhost:8082");
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
            assertThat(tables(jdbc)).contains("refill_orders", "refill_order_line_items");
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
            create table refill_order_line_items (quantity integer not null check ((quantity>=1)), total_price numeric(10,2), unit_price numeric(10,2) not null, line_item_id bigint generated by default as identity, drug_code varchar(255) not null, drug_name varchar(255) not null, prescription_id varchar(255), refill_order_id varchar(255), primary key (line_item_id));
            create table refill_orders (delivery_date timestamp(6), order_date timestamp(6) not null, member_id varchar(255) not null, member_location varchar(255) not null, order_status VARCHAR(50) not null check ((order_status in ('PENDING','CONFIRMED','IN_PROGRESS','SHIPPED','DELIVERED','CANCELLED'))), order_type VARCHAR(50) not null check ((order_type in ('SCHEDULED','ADHOC'))), refill_order_id varchar(255) not null, subscription_id varchar(255), tracking_number varchar(255), primary key (refill_order_id));
            alter table if exists refill_order_line_items add constraint FKeni8ij60ka1t5wt1asdv58nmn foreign key (refill_order_id) references refill_orders;
            insert into refill_orders values (null, '2026-01-01 10:00:00', 'MEM001', 'New York', 'CONFIRMED', 'ADHOC', 'ROX', null, null);
            insert into refill_order_line_items (quantity, total_price, unit_price, drug_code, drug_name, prescription_id, refill_order_id) values (1, 9.99, 9.99, 'D001', 'Drug', 'PRES001', 'ROX');
            """;
        try (ConfigurableApplicationContext app = startProd(newDatabase(sql))) {
            JdbcTemplate jdbc = new JdbcTemplate(app.getBean(DataSource.class));
            assertThat(jdbc.queryForObject("SELECT count(*) FROM refill_order_line_items WHERE refill_order_id = 'ROX'", Integer.class)).isEqualTo(1);
            // baseline at 0 for the existing schema, then every migration runs (as a no-op where applied)
            assertThat(appliedVersions(jdbc)).containsExactly("0", "1", "2", "3");
            assertNoSeedData(jdbc);
            assertRowLevelSecurityOnAllTables(jdbc);
        }
    }
}
