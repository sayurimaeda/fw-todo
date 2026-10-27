package com.example.todoapp.mcp;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.todoapp.Todo;
import com.example.todoapp.TodoService;
import com.example.todoapp.api.TodoDto;
import com.example.todoapp.api.HolidayClient;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class TodoTools {

    private static final Set<String> ALLOWED_CATEGORIES = Set.of(
            "デザイン", "マーケティング", "プログラミング", "資格", "就職活動");

    private final TodoService todoService;
    private final HolidayClient holidayClient;

    public TodoTools(TodoService todoService, HolidayClient holidayClient) {
        this.todoService = todoService;
        this.holidayClient = holidayClient;
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

    @McpTool(name = "add_todo", description = "やることを1件足す")
    public TodoDto addTodo(
            @McpToolParam(description = "やることの名前", required = true) String title,
            @McpToolParam(description = "ジャンル（デザイン・マーケティング・プログラミング・資格・就職活動）", required = true) String category,
            @McpToolParam(description = "優先度", required = true) Integer priority,
            @McpToolParam(description = "メモ", required = false) String detail,
            @McpToolParam(description = "期限（yyyy-MM-dd）", required = false) String dueDate) {
        validateCategory(category);
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setCategory(category);
        todo.setPriority(priority);
        todo.setDetail(detail);
        todo.setDueDate(dueDate == null ? null : LocalDate.parse(dueDate));
        todoService.create(todo);
        return TodoDto.from(todoService.findById(todo.getId()));
    }

    @McpTool(name = "update_todo", description = "やることを1件直す（期限を変えるのもこれ）")
    public TodoDto updateTodo(
            @McpToolParam(description = "やることの番号", required = true) Long id,
            @McpToolParam(description = "やることの名前", required = false) String title,
            @McpToolParam(description = "ジャンル（デザイン・マーケティング・プログラミング・資格・就職活動）", required = false) String category,
            @McpToolParam(description = "優先度", required = false) Integer priority,
            @McpToolParam(description = "メモ", required = false) String detail,
            @McpToolParam(description = "期限（yyyy-MM-dd）", required = false) String dueDate) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return null;
        }
        if (title != null) todo.setTitle(title);
        if (category != null) {
            validateCategory(category);
            todo.setCategory(category);
        }
        if (priority != null) todo.setPriority(priority);
        if (detail != null) todo.setDetail(detail);
        if (dueDate != null) todo.setDueDate(LocalDate.parse(dueDate));
        todoService.update(todo);
        return TodoDto.from(todoService.findById(id));
    }

    @McpTool(name = "complete_todo", description = "やることを完了にする")
    public TodoDto completeTodo(
            @McpToolParam(description = "やることの番号", required = true) Long id) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return null;
        }
        todo.setCompleted(true);
        todoService.update(todo);
        return TodoDto.from(todoService.findById(id));
    }

    @McpTool(name = "delete_todo", description = "やることを1件消す")
    public boolean deleteTodo(
            @McpToolParam(description = "やることの番号", required = true) Long id) {
        return todoService.delete(id) > 0;
    }

    @McpTool(name = "find_free_days", description = "期間の中で、期限のやることが無く、土日でも祝日でもない「空いている日」を返す。やることの期限を動かす先を決めるのに使う")
    public List<String> findFreeDays(
            @McpToolParam(description = "期間の開始日（yyyy-MM-dd）", required = true) String from,
            @McpToolParam(description = "期間の終了日（yyyy-MM-dd）", required = true) String to) {
        LocalDate start = LocalDate.parse(from);
        LocalDate end = LocalDate.parse(to);
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("終了日は開始日以降の日付にしてください");
        }
        Set<LocalDate> busyDates = todoService.search(null, null, "asc", start, end).stream()
                .map(Todo::getDueDate)
                .filter(date -> date != null)
                .collect(Collectors.toSet());
        Set<String> holidays = holidayClient.getHolidays(start, end).holidays().keySet();
        List<String> freeDays = new java.util.ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            DayOfWeek day = date.getDayOfWeek();
            if (!busyDates.contains(date)
                    && day != DayOfWeek.SATURDAY
                    && day != DayOfWeek.SUNDAY
                    && !holidays.contains(date.toString())) {
                freeDays.add(date.toString());
            }
        }
        return freeDays;
    }

    private void validateCategory(String category) {
        if (!ALLOWED_CATEGORIES.contains(category)) {
            throw new IllegalArgumentException("ジャンルは指定された5種類から選んでください");
        }
    }
}
