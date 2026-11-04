package com.example.todoapp.api;

import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import tools.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class WeatherClient {

    private static final String WEATHER_URL = "https://api.open-meteo.com/v1/forecast"
            + "?latitude=35.6762&longitude=139.6503"
            + "&daily=weather_code,temperature_2m_max,temperature_2m_min"
            + "&timezone=Asia/Tokyo&forecast_days=14";

    private final RestClient restClient = createRestClient();

    private static RestClient createRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        return RestClient.create(new RestTemplate(requestFactory));
    }

    public WeatherResult getWeather(LocalDate from, LocalDate to) {
        try {
            JsonNode response = restClient.get().uri(WEATHER_URL).retrieve().body(JsonNode.class);
            JsonNode daily = response == null ? null : response.path("daily");
            JsonNode dates = daily == null ? null : daily.path("time");
            JsonNode codes = daily == null ? null : daily.path("weather_code");
            JsonNode maxTemperatures = daily == null ? null : daily.path("temperature_2m_max");
            JsonNode minTemperatures = daily == null ? null : daily.path("temperature_2m_min");
            if (dates == null || !dates.isArray() || codes == null || !codes.isArray()) {
                return new WeatherResult(Map.of(), true);
            }

            Map<String, DailyWeather> weather = new LinkedHashMap<>();
            for (int i = 0; i < dates.size(); i++) {
                LocalDate date = LocalDate.parse(dates.get(i).asString());
                if ((from != null && date.isBefore(from)) || (to != null && date.isAfter(to))) {
                    continue;
                }
                Integer code = i < codes.size() && codes.get(i).canConvertToInt()
                        ? codes.get(i).asInt() : null;
                Double max = numberAt(maxTemperatures, i);
                Double min = numberAt(minTemperatures, i);
                if (code != null) {
                    weather.put(date.toString(), new DailyWeather(description(code), code, max, min));
                }
            }
            return new WeatherResult(weather, false);
        } catch (RuntimeException exception) {
            log.warn("天気情報を取得できませんでした", exception);
            return new WeatherResult(Map.of(), true);
        }
    }

    private Double numberAt(JsonNode values, int index) {
        if (values == null || !values.isArray() || index >= values.size()
                || !values.get(index).isNumber()) {
            return null;
        }
        return values.get(index).asDouble();
    }

    private String description(int code) {
        return switch (code) {
            case 0 -> "快晴";
            case 1 -> "晴れ";
            case 2 -> "晴れ時々くもり";
            case 3 -> "くもり";
            case 45, 48 -> "霧";
            case 51, 53, 55, 56, 57 -> "霧雨";
            case 61, 63, 65, 66, 67, 80, 81, 82 -> "雨";
            case 71, 73, 75, 77, 85, 86 -> "雪";
            case 95, 96, 99 -> "雷雨";
            default -> "天気情報";
        };
    }

    public record DailyWeather(String description, Integer weatherCode,
            Double temperatureMax, Double temperatureMin) {
    }

    public record WeatherResult(Map<String, DailyWeather> weather, boolean unavailable) {
    }
}
