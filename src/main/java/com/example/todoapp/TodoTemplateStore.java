package com.example.todoapp;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TodoTemplateStore {
    private final JdbcTemplate jdbc;

    public TodoTemplateStore(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<TemplateView> findAll() {
        List<TemplateView> templates = jdbc.query("SELECT id, name, description, category FROM todo_templates ORDER BY id DESC",
                (rs, row) -> new TemplateView(rs.getLong("id"), rs.getString("name"), rs.getString("description"), rs.getString("category"), List.of()));
        return templates.stream().map(template -> new TemplateView(template.id(), template.name(),
                template.description(), template.category(), findItems(template.id()))).toList();
    }

    public List<String> findItems(long id) {
        return jdbc.query("SELECT title FROM todo_template_items WHERE template_id = ? ORDER BY item_order, id",
                (rs, row) -> rs.getString("title"), id);
    }

    public long create(String name, String description, String category, List<String> titles) {
        jdbc.update("INSERT INTO todo_templates (name, description, category) VALUES (?, ?, ?)", name, description, category);
        Long id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        for (int i = 0; i < titles.size(); i++) {
            jdbc.update("INSERT INTO todo_template_items (template_id, title, item_order) VALUES (?, ?, ?)", id, titles.get(i), i);
        }
        return id;
    }

    public void delete(long id) { jdbc.update("DELETE FROM todo_templates WHERE id = ?", id); }

    public record TemplateView(long id, String name, String description, String category, List<String> items) { }
}
