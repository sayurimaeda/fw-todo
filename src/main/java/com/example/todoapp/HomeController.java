package com.example.todoapp;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final TodoService todoService;

    public HomeController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "やること管理");
        return "index";
    }

    @GetMapping("/todos")
    public String todos(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "false") boolean showCompleted,
            @RequestParam(defaultValue = "0") int trash, Model model) {
        String searchKeyword = keyword == null ? "" : keyword;
        String selectedCategory = category == null ? "" : category;
        String sortOrder = "desc".equals(order) ? "desc" : "asc";
        int pageSize = 10;
        boolean trashView = trash == 1;
        int totalCount = trashView
                ? todoService.countTrash()
                : todoService.countSearch(searchKeyword, selectedCategory, showCompleted);
        int totalPages = (totalCount + pageSize - 1) / pageSize;
        int currentPage = Math.max(1, page);
        if (totalPages > 0) {
            currentPage = Math.min(currentPage, totalPages);
        }
        int offset = (currentPage - 1) * pageSize;
        model.addAttribute("todos", trashView
                ? todoService.searchTrashPage(pageSize, offset)
                : todoService.searchPage(
                        searchKeyword, selectedCategory, sortOrder, showCompleted, pageSize, offset));
        model.addAttribute("keyword", searchKeyword);
        model.addAttribute("category", selectedCategory);
        model.addAttribute("order", sortOrder);
        model.addAttribute("showCompleted", showCompleted);
        model.addAttribute("trash", trashView);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        return "todos";
    }

    @GetMapping("/todos/new")
    public String createForm(Model model) {
        model.addAttribute("todo", new Todo());
        return "create";
    }

    @PostMapping("/todos/confirm")
    public String confirmCreate(@Valid @ModelAttribute Todo todo, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "create";
        }
        model.addAttribute("todo", todo);
        return "create-confirm";
    }

    @PostMapping("/todos/new")
    public String rewriteCreate(@ModelAttribute Todo todo, Model model) {
        model.addAttribute("todo", todo);
        return "create";
    }

    @PostMapping("/todos")
    public String createTodo(@ModelAttribute Todo todo, RedirectAttributes redirectAttributes) {
        todoService.create(todo);
        redirectAttributes.addFlashAttribute("message", "登録しました");
        return "redirect:/todos";
    }

    @GetMapping("/todos/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            redirectAttributes.addFlashAttribute("message", "見つかりませんでした");
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);
        return "edit";
    }

    @PostMapping("/todos/{id}/confirm")
    public String confirmEdit(@PathVariable Long id, @Valid @ModelAttribute Todo todo,
            BindingResult bindingResult, Model model) {
        todo.setId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("todo", todo);
            return "edit";
        }
        model.addAttribute("todo", todo);
        return "edit-confirm";
    }

    @PostMapping("/todos/{id}/edit")
    public String rewriteEdit(@PathVariable Long id, @ModelAttribute Todo todo, Model model) {
        todo.setId(id);
        model.addAttribute("todo", todo);
        return "edit";
    }

    @PostMapping("/todos/{id}")
    public String updateTodo(@PathVariable Long id, @ModelAttribute Todo todo,
            RedirectAttributes redirectAttributes) {
        todo.setId(id);
        todoService.update(todo);
        redirectAttributes.addFlashAttribute("message", "保存しました");
        return "redirect:/todos";
    }

    @GetMapping("/todos/{id}/delete")
    public String deleteConfirm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            redirectAttributes.addFlashAttribute("message", "見つかりませんでした");
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);
        return "delete";
    }

    @PostMapping("/todos/{id}/delete")
    public String deleteTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        todoService.delete(id);
        redirectAttributes.addFlashAttribute("message", "削除しました");
        return "redirect:/todos";
    }

    @PostMapping("/todos/{id}/restore")
    public String restoreTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        todoService.restore(id);
        redirectAttributes.addFlashAttribute("message", "復元しました");
        return "redirect:/todos?trash=1";
    }

    @PostMapping("/todos/{id}/pin")
    public String togglePinned(@PathVariable Long id,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "false") boolean showCompleted,
            @RequestParam(defaultValue = "1") int page,
            RedirectAttributes redirectAttributes) {
        todoService.togglePinned(id);
        if (keyword != null) redirectAttributes.addAttribute("keyword", keyword);
        if (category != null) redirectAttributes.addAttribute("category", category);
        redirectAttributes.addAttribute("order", order);
        redirectAttributes.addAttribute("showCompleted", showCompleted);
        redirectAttributes.addAttribute("page", page);
        return "redirect:/todos";
    }

}
