package ru.course.bosssemester.config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Configuration
@EnableConfigurationProperties({GigaChatProperties.class, AppProperties.class})
public class PropertiesConfig {}
