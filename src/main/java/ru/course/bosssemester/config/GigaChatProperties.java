package ru.course.bosssemester.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix = "gigachat")
public record GigaChatProperties(String authKey, String scope, String oauthUrl, String apiBaseUrl, String model, boolean demoMode) {}
