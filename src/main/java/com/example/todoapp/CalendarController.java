package com.example.todoapp;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarController {

    @GetMapping("/calendar")
    public String calendar(@RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(defaultValue = "month") String view,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        YearMonth currentMonth = YearMonth.now();
        YearMonth displayedMonth = YearMonth.of(
                year == null ? currentMonth.getYear() : year,
                month == null ? currentMonth.getMonthValue() : month);
        boolean weekView = "week".equals(view);
        LocalDate selectedDate = date == null
                ? (weekView ? LocalDate.now() : displayedMonth.atDay(1))
                : date;

        LocalDate firstDay;
        LocalDate lastDay;
        List<LocalDate> calendarDays = new ArrayList<>();

        if (weekView) {
            firstDay = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() - 1);
            lastDay = firstDay.plusDays(6);
            for (int day = 0; day < 7; day++) {
                calendarDays.add(firstDay.plusDays(day));
            }
            displayedMonth = YearMonth.from(selectedDate);
        } else {
            firstDay = displayedMonth.atDay(1);
            lastDay = displayedMonth.atEndOfMonth();
            int daysBeforeMonth = firstDay.getDayOfWeek().getValue() - 1;
            for (int i = 0; i < daysBeforeMonth; i++) {
                calendarDays.add(null);
            }
            for (int day = 1; day <= displayedMonth.lengthOfMonth(); day++) {
                calendarDays.add(displayedMonth.atDay(day));
            }
            while (calendarDays.size() % 7 != 0) {
                calendarDays.add(null);
            }
        }

        YearMonth previousMonth = displayedMonth.minusMonths(1);
        YearMonth nextMonth = displayedMonth.plusMonths(1);
        model.addAttribute("weekView", weekView);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("previousWeekDate", selectedDate.minusWeeks(1));
        model.addAttribute("nextWeekDate", selectedDate.plusWeeks(1));
        model.addAttribute("weekStart", firstDay);
        model.addAttribute("weekEnd", lastDay);
        model.addAttribute("displayTitle", weekView
                ? firstDay + " - " + lastDay
                : displayedMonth.toString());
        model.addAttribute("displayedMonth", displayedMonth);
        model.addAttribute("firstDay", firstDay);
        model.addAttribute("lastDay", lastDay);
        model.addAttribute("calendarDays", calendarDays);
        model.addAttribute("previousMonth", previousMonth);
        model.addAttribute("nextMonth", nextMonth);
        return "calendar";
    }
}
