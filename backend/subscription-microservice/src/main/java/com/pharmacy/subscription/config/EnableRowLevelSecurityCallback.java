package com.pharmacy.subscription.config;

import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * After every Flyway migration, enables Row Level Security on each table in the
 * current schema that this database user owns and that doesn't have it yet:
 * this service's tables, its Flyway history table, and any table added later.
 * <p>
 * Why: on Supabase, tables in the public schema are reachable through the
 * auto-generated REST API (PostgREST) with the public anon key unless RLS is on.
 * With RLS enabled and no policies, that API sees nothing, while this service,
 * connecting as the table owner, is unaffected (owners bypass RLS).
 * <p>
 * PostgreSQL only; a no-op on H2 (local dev). Idempotent, and tables are altered
 * in a fixed order so services starting at the same time can't deadlock.
 */
@Component
public class EnableRowLevelSecurityCallback implements Callback {
    
    private static final Logger log = LoggerFactory.getLogger(EnableRowLevelSecurityCallback.class);
    
    private static final String TABLES_WITHOUT_RLS = """
        SELECT tablename FROM pg_tables
        WHERE schemaname = current_schema() AND tableowner = current_user AND NOT rowsecurity
        ORDER BY tablename""";
    
    private static String quote(String identifier) {
        return '"' + identifier.replace("\"", "\"\"") + '"';
    }
    
    @Override
    public boolean supports(Event event, Context context) {
        return event == Event.AFTER_MIGRATE;
    }
    
    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return true;
    }
    
    @Override
    public void handle(Event event, Context context) {
        Connection connection = context.getConnection();
        try {
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
    
    @Override
    public String getCallbackName() {
        return "enable-row-level-security";
    }
}
