package com.example.todoapp.api;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.nio.charset.StandardCharsets;

import com.example.todoapp.Todo;
import com.example.todoapp.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoApiController {

    private final TodoService todoService;

    public TodoApiController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/api/todos")
    public List<TodoDto> getTodos(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "order", required = false) String order,
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<Todo> todos = todoService.search(keyword, category, order, from, to);
        List<Todo> orderedTodos = Stream.concat(
                todos.stream().filter(todo -> todo.getDueDate() == null),
                todos.stream().filter(todo -> todo.getDueDate() != null))
                .toList();
        return orderedTodos.stream()
                .map(todo -> todoService.findById(todo.getId()))
                .map(TodoDto::from)
                .toList();
    }

    @GetMapping(value = "/api/todos.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportTodosCsv(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "order", defaultValue = "asc") String order,
            @RequestParam(name = "showCompleted", defaultValue = "false") boolean showCompleted) {
        String sortOrder = "desc".equals(order) ? "desc" : "asc";
        StringBuilder csv = new StringBuilder("\uFEFF");
        csv.append("やること,印,ジャンル,優先度,期限,状態,メモ\r\n");
        for (Todo todo : todoService.searchForExport(keyword, category, sortOrder, showCompleted)) {
            appendCsvRow(csv, todo.getTitle(), Boolean.TRUE.equals(todo.getPinned()) ? "あり" : "なし",
                    todo.getCategory(), todo.getPriority() == null ? "" : todo.getPriority().toString(),
                    todo.getDueDate() == null ? "" : todo.getDueDate().toString(),
                    Boolean.TRUE.equals(todo.getCompleted()) ? "完了" : "未完了", todo.getDetail());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"todos.csv\"");
        return ResponseEntity.ok().headers(headers).body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void appendCsvRow(StringBuilder csv, String... values) {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) csv.append(',');
            String value = values[i] == null ? "" : values[i];
            if (value.matches("^[\\s]*[=+@-].*")) value = "'" + value;
            csv.append('"').append(value.replace("\"", "\"\"")).append('"');
        }
        csv.append("\r\n");
    }

    @GetMapping("/api/todos/{id}")
    public ResponseEntity<?> getTodo(@PathVariable("id") Long id) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound(id));
        }
        return ResponseEntity.ok(TodoDto.from(todo));
    }

    @PostMapping("/api/todos")
    public ResponseEntity<?> createTodo(@Valid @RequestBody TodoRequest request) {
        Todo todo = request.toTodo();
        todoService.create(todo);
        Todo createdTodo = todoService.findById(todo.getId());
        URI location = URI.create("/api/todos/" + todo.getId());
        return ResponseEntity.created(location).body(TodoDto.from(createdTodo));
    }

    @PutMapping("/api/todos/{id}")
    public ResponseEntity<?> replaceTodo(@PathVariable("id") Long id,
            @Valid @RequestBody TodoRequest request) {
        Todo existingTodo = todoService.findById(id);
        if (existingTodo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound(id));
        }

        Todo todo = request.toTodo();
        todo.setId(id);
        todoService.update(todo);
        Todo updatedTodo = todoService.findById(id);
        return ResponseEntity.ok(TodoDto.from(updatedTodo));
    }

    @DeleteMapping("/api/todos/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable("id") Long id) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound(id));
        }

        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ProblemDetail notFound(Long id) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, "指定されたやることが見つかりません。");
        problem.setType(URI.create("about:blank"));
        problem.setTitle("Not Found");
        problem.setInstance(URI.create("/api/todos/" + id));
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleInvalidRequest(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<ValidationError> errors = new ArrayList<>();
        Set<String> fields = new HashSet<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            if (fields.add(fieldError.getField())) {
                errors.add(new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()));
            }
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "入力に誤りがあります");
        problem.setType(URI.create("about:blank"));
        problem.setTitle("Bad Request");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }

    private record ValidationError(String field, String message) {
    }
}
