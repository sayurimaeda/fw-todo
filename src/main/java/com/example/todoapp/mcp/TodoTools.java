package com.example.todoapp.mcp;

import java.time.LocalDate;
import java.util.List;

import com.example.todoapp.Todo;
import com.example.todoapp.TodoService;
import com.example.todoapp.api.TodoDto;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class TodoTools {

    private final TodoService todoService;

    public TodoTools(TodoService todoService) {
        this.todoService = todoService;
    }

    @McpTool(name = "list_todos", description = "やることの一覧を返す（期間・ジャンルで絞れる）")
    public List<TodoDto> listTodos(
            @McpToolParam(description = "やることの名前で絞り込むキーワード", required = false) String keyword,
            @McpToolParam(description = "ジャンルで絞り込む条件", required = false) String category,
            @McpToolParam(description = "期間の開始日（yyyy-MM-dd）", required = false) String from,
            @McpToolParam(description = "期間の終了日（yyyy-MM-dd）", required = false) String to) {
        LocalDate fromDate = from == null ? null : LocalDate.parse(from);
        LocalDate toDate = to == null ? null : LocalDate.parse(to);
        List<Todo> todos = todoService.search(keyword, category, "asc", fromDate, toDate);
        return todos.stream()
                .map(TodoDto::from)
                .toList();
    }

    @McpTool(name = "get_todo", description = "やることを1件返す")
    public TodoDto getTodo(
            @McpToolParam(description = "やることの番号", required = true) Long id) {
        Todo todo = todoService.findById(id);
        return todo == null ? null : TodoDto.from(todo);
    }
}
