package com.pharmacy.auth.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs the Flyway migrations, then enables Row Level Security on each table in
 * the current schema that this database user owns and that doesn't have it yet:
 * this service's tables, its Flyway history table, and any table added later.
 * <p>
 * Why: on Supabase, tables in the public schema are reachable through the
 * auto-generated REST API (PostgREST) with the public anon key unless RLS is on.
 * With RLS enabled and no policies, that API sees nothing, while this service,
 * connecting as the table owner, is unaffected (owners bypass RLS).
 * <p>
 * This runs after Flyway has finished and released its connections, on a single
 * connection of its own. (As a Flyway callback it needed a third connection at
 * start-up, which a small shared connection pool such as Supabase's free session
 * pooler can refuse.) PostgreSQL only; a no-op on H2. Idempotent, and tables are
 * altered in a fixed order so services starting at the same time can't deadlock.
 */
@Component
public class RowLevelSecurityMigrationStrategy implements FlywayMigrationStrategy {
    
    private static final Logger log = LoggerFactory.getLogger(RowLevelSecurityMigrationStrategy.class);
    
    private static final String TABLES_WITHOUT_RLS = """
        SELECT tablename FROM pg_tables
        WHERE schemaname = current_schema() AND tableowner = current_user AND NOT rowsecurity
        ORDER BY tablename""";
    
    private final DataSource dataSource;
    
    public RowLevelSecurityMigrationStrategy(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public void migrate(Flyway flyway) {
        flyway.migrate();
        enableRowLevelSecurity();
    }
    
    private void enableRowLevelSecurity() {
        try (Connection connection = dataSource.getConnection()) {
            if (!"PostgreSQL".equals(connection.getMetaData().getDatabaseProductName())) {
                return;
            }
            List<String> tables = new ArrayList<>();
            try (PreparedStatement query = connection.prepareStatement(TABLES_WITHOUT_RLS);
                 ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    tables.add(rows.getString(1));
                }
            }
            try (Statement alter = connection.createStatement()) {
                for (String table : tables) {
                    alter.execute("ALTER TABLE " + quote(table) + " ENABLE ROW LEVEL SECURITY");
                }
            }
            if (!tables.isEmpty()) {
                log.info("Enabled row level security on {}", tables);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not enable row level security", e);
        }
    }
    
    private static String quote(String identifier) {
        return '"' + identifier.replace("\"", "\"\"") + '"';
    }
}
