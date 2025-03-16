package com.thena3ik.weatherbot.image;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum SeasonTheme {
    WINTER(List.of(12L, 1L, 2L), "winter"),
    SPRING(List.of(3L, 4L, 5L), "spring"),
    SUMMER(List.of(6L, 7L, 8L), "summer"),
    AUTUMN(List.of(9L, 10L, 11L), "autumn");

    private final List<Long> months;
    private final String seasonName;

    SeasonTheme(List<Long> months, String seasonName) {
        this.months = months;
        this.seasonName = seasonName;
    }

    public String getSeasonName() {
        return seasonName;
    }

    private static final Map<Long, String> LOOKUP = Arrays.stream(values())
            .flatMap(season -> season.months.stream().map(month -> Map.entry(month, season.seasonName)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public static String getSeason(Long month) {
        return LOOKUP.getOrDefault(month, null);
    }
}