package ru.course.bosssemester.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.course.bosssemester.dto.AuthDtos.AuthRequest;
import ru.course.bosssemester.dto.AuthDtos.AuthResponse;
import ru.course.bosssemester.entity.AuthSession;
import ru.course.bosssemester.entity.User;
import ru.course.bosssemester.repository.AuthSessionRepository;
import ru.course.bosssemester.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository users;
    private final AuthSessionRepository sessions;

    public AuthService(UserRepository users, AuthSessionRepository sessions) {
        this.users = users;
        this.sessions = sessions;
    }

    public AuthResponse register(AuthRequest req) {
        users.findByUsername(req.username()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь уже существует");
        });
        User user = User.builder()
                .username(req.username())
                .passwordHash(hash(req.password()))
                .role("USER")
                .createdAt(Instant.now())
                .build();
        users.save(user);
        return login(req);
    }

    public AuthResponse login(AuthRequest req) {
        User user = users.findByUsername(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный логин или пароль"));
        if (!user.getPasswordHash().equals(hash(req.password()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный логин или пароль");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        sessions.save(AuthSession.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plusSeconds(60L * 60 * 24 * 14))
                .build());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }

    public User requireUser(String authHeader) {
        String token = authHeader == null ? "" : authHeader.replace("Bearer", "").trim();
        AuthSession s = sessions.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Нужно войти в систему"));
        if (s.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Сессия истекла");
        }
        return s.getUser();
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}