package com.thena3ik.weatherbot.image;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum WeatherCondition {
    SUN(0L, "sun"),
    PARTLY_CLOUDY(Set.of(1L, 2L), "partly cloudy"),
    CLOUDS(3L, "clouds"),
    FOG(Set.of(45L, 48L), "fog"),
    DRIZZLE(Set.of(51L, 53L, 55L, 56L, 57L), "drizzle"),
    RAIN(Set.of(61L, 63L, 65L, 66L, 67L), "rain"),
    SNOW_FALL(Set.of(71L, 73L, 75L), "snow fall"),
    SNOW_GRAINS(77L, "snow grains"),
    RAIN_SHOWERS(Set.of(80L, 81L, 82L), "rain showers"),
    SNOW_SHOWERS(Set.of(85L, 86L), "snow showers"),
    THUNDERSTORM(Set.of(95L, 96L, 99L), "thunderstorm");

    private final Set<Long> codes;
    private final String imageName;

    WeatherCondition(long code, String imageName) {
        this(Set.of(code), imageName);
    }

    WeatherCondition(Set<Long> codes, String imageName) {
        this.codes = codes;
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    private static final Map<Long, String> LOOKUP = Stream.of(values())
            .flatMap(wc -> wc.codes.stream().map(code -> Map.entry(code, wc.imageName)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public static String getImageName(long code) {
        return LOOKUP.getOrDefault(code, null);
    }
}