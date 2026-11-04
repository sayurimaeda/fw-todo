package com.example.todoapp;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.InitializingBean;

@Component
public class TodoTemplateSchemaInitializer implements InitializingBean {
    private final JdbcTemplate jdbc;

    public TodoTemplateSchemaInitializer(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Override
    public void afterPropertiesSet() {
        jdbc.execute("CREATE TABLE IF NOT EXISTS todo_templates (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, description TEXT NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        jdbc.execute("CREATE TABLE IF NOT EXISTS todo_template_items (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, template_id BIGINT NOT NULL, title VARCHAR(255) NOT NULL, item_order INT NOT NULL, CONSTRAINT fk_todo_template_items_template FOREIGN KEY (template_id) REFERENCES todo_templates(id) ON DELETE CASCADE)");
        Integer categoryCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'todo_templates' AND COLUMN_NAME = 'category'",
                Integer.class);
        if (categoryCount != null && categoryCount == 0) {
            jdbc.execute("ALTER TABLE todo_templates ADD COLUMN category VARCHAR(100) NULL");
        }
    }
}
