package com.example.todoapp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TodoService {

    private final TodoMapper todoMapper;

    public TodoService(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    public List<Todo> search(String keyword, String category, String order) {
        return search(keyword, category, order, null, null);
    }

    public List<Todo> search(String keyword, String category, String order, LocalDate from, LocalDate to) {
        return todoMapper.search(keyword, category, order, from, to);
    }

    public List<Todo> searchPage(String keyword, String category, String order,
            boolean showCompleted, int limit, int offset) {
        return todoMapper.searchPage(keyword, category, order, showCompleted, limit, offset);
    }

    public List<Todo> searchForExport(String keyword, String category, String order,
            boolean showCompleted) {
        return todoMapper.searchForExport(keyword, category, order, showCompleted);
    }

    public int countSearch(String keyword, String category, boolean showCompleted) {
        return todoMapper.countSearch(keyword, category, showCompleted);
    }

    public List<Todo> searchTrashPage(int limit, int offset) {
        return todoMapper.searchTrashPage(limit, offset);
    }

    public int countTrash() {
        return todoMapper.countTrash();
    }

    public Todo findById(Long id) {
        return todoMapper.findById(id);
    }

    public int create(Todo todo) {
        int result = todoMapper.insert(todo);
        log.info("登録 id={}", todo.getId());
        return result;
    }

    public int update(Todo todo) {
        Todo existing = todoMapper.findById(todo.getId());
        if (existing == null) {
            return 0;
        }
        boolean wasCompleted = Boolean.TRUE.equals(existing.getCompleted());
        boolean willBeCompleted = todo.getCompleted() == null
                ? wasCompleted : Boolean.TRUE.equals(todo.getCompleted());
        todo.setCompleted(willBeCompleted);
        if (!wasCompleted && willBeCompleted) {
            todo.setCompletedAt(LocalDateTime.now());
        } else if (wasCompleted && !willBeCompleted) {
            todo.setCompletedAt(null);
        } else {
            todo.setCompletedAt(existing.getCompletedAt());
        }
        int result = todoMapper.update(todo);
        log.info("編集 id={}", todo.getId());
        return result;
    }

    public int delete(Long id) {
        int result = todoMapper.markDeletedById(id);
        log.info("削除 id={}", id);
        return result;
    }

    public int restore(Long id) {
        return todoMapper.restoreById(id);
    }

    public int togglePinned(Long id) {
        Todo todo = todoMapper.findById(id);
        if (todo == null) {
            return 0;
        }
        return todoMapper.updatePinned(id, !Boolean.TRUE.equals(todo.getPinned()));
    }
}
