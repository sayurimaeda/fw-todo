package com.example.todoapp.api;

import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class HolidayClient {

    private static final String HOLIDAYS_URL = "https://holidays-jp.github.invalid/api/v1/date.json";

    private final RestClient restClient = createRestClient();

    private static RestClient createRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        return RestClient.create(new RestTemplate(requestFactory));
    }

    public HolidayResult getHolidays(LocalDate from, LocalDate to) {
        try {
            Map<String, String> holidays = restClient.get()
                    .uri(HOLIDAYS_URL)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, String>>() {
                    });
            if (holidays == null) {
                return new HolidayResult(Map.of(), true);
            }
            return new HolidayResult(filterHolidays(holidays, from, to), false);
        } catch (RestClientException exception) {
            return new HolidayResult(Map.of(), true);
        }
    }

    private Map<String, String> filterHolidays(Map<String, String> holidays, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return holidays;
        }
        Map<String, String> filteredHolidays = new LinkedHashMap<>();
        for (Map.Entry<String, String> holiday : holidays.entrySet()) {
            LocalDate date = LocalDate.parse(holiday.getKey());
            if ((from == null || !date.isBefore(from))
                    && (to == null || !date.isAfter(to))) {
                filteredHolidays.put(holiday.getKey(), holiday.getValue());
            }
        }
        return filteredHolidays;
    }

    public record HolidayResult(Map<String, String> holidays, boolean unavailable) {
    }
}
