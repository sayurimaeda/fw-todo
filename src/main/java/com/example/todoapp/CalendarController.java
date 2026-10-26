package com.example.todoapp;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarController {

    @GetMapping("/calendar")
    public String calendar(@RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month, Model model) {
        YearMonth currentMonth = YearMonth.now();
        YearMonth displayedMonth = YearMonth.of(
                year == null ? currentMonth.getYear() : year,
                month == null ? currentMonth.getMonthValue() : month);

        LocalDate firstDay = displayedMonth.atDay(1);
        LocalDate lastDay = displayedMonth.atEndOfMonth();
        List<LocalDate> calendarDays = new ArrayList<>();

        int daysBeforeMonth = firstDay.getDayOfWeek().getValue() % 7;
        for (int i = 0; i < daysBeforeMonth; i++) {
            calendarDays.add(null);
        }
        for (int day = 1; day <= displayedMonth.lengthOfMonth(); day++) {
            calendarDays.add(displayedMonth.atDay(day));
        }
        while (calendarDays.size() % 7 != 0) {
            calendarDays.add(null);
        }

        YearMonth previousMonth = displayedMonth.minusMonths(1);
        YearMonth nextMonth = displayedMonth.plusMonths(1);
        model.addAttribute("displayedMonth", displayedMonth);
        model.addAttribute("firstDay", firstDay);
        model.addAttribute("lastDay", lastDay);
        model.addAttribute("calendarDays", calendarDays);
        model.addAttribute("previousMonth", previousMonth);
        model.addAttribute("nextMonth", nextMonth);
        return "calendar";
    }
}
