package ru.course.bosssemester.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix = "app")
public record AppProperties(String imagesDir, String publicBaseUrl, int defaultDailyLimit) {}
