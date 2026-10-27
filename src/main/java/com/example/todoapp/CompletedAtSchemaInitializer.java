package com.example.todoapp;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.InitializingBean;


/** Adds timestamp columns to existing databases before requests are served. */
@Component
public class CompletedAtSchemaInitializer implements InitializingBean {

    private final JdbcTemplate jdbcTemplate;

    public CompletedAtSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        addColumnIfMissing("completed_at");
        addColumnIfMissing("deleted_at");
        addPinnedColumnIfMissing();
    }

    private void addColumnIfMissing(String columnName) {
        Integer columnCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS "
                        + "WHERE TABLE_SCHEMA = DATABASE() "
                        + "AND TABLE_NAME = 'todos' "
                        + "AND COLUMN_NAME = ?",
                Integer.class, columnName);
        if (columnCount != null && columnCount == 0) {
            jdbcTemplate.execute("ALTER TABLE todos ADD COLUMN " + columnName + " DATETIME NULL");
        }
    }

    private void addPinnedColumnIfMissing() {
        Integer columnCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS "
                        + "WHERE TABLE_SCHEMA = DATABASE() "
                        + "AND TABLE_NAME = 'todos' "
                        + "AND COLUMN_NAME = 'pinned'",
                Integer.class);
        if (columnCount != null && columnCount == 0) {
            jdbcTemplate.execute("ALTER TABLE todos ADD COLUMN pinned BOOLEAN NOT NULL DEFAULT FALSE");
        }
    }
}
