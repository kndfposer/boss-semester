package ru.course.bosssemester.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.course.bosssemester.entity.User;
import ru.course.bosssemester.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository users;

    public DataInitializer(UserRepository users) {
        this.users = users;
    }

    public void run(String... args) throws Exception {
        if (users.findByUsername("admin").isEmpty()) {
            users.save(User.builder()
                    .username("admin")
                    .passwordHash(hash("admin123"))
                    .role("ADMIN")
                    .createdAt(Instant.now())
                    .build());
        }
    }

    private String hash(String v) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(v.getBytes(StandardCharsets.UTF_8)));
    }
}