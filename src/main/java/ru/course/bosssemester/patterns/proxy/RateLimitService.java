package ru.course.bosssemester.patterns.proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.course.bosssemester.entity.RateLimitPolicy;
import ru.course.bosssemester.entity.User;
import ru.course.bosssemester.repository.RateLimitPolicyRepository;

import java.time.LocalDate;

@Service
public class RateLimitService {
    private final RateLimitPolicyRepository repo;

    @Value("${app.default-daily-limit:5}")
    private int defaultLimit;

    public RateLimitService(RateLimitPolicyRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void checkAndIncrement(User user) {
        LocalDate day = LocalDate.now();
        RateLimitPolicy p = repo.findByUserAndDay(user, day)
                .orElseGet(() -> RateLimitPolicy.builder()
                        .user(user)
                        .day(day)
                        .usedCount(0)
                        .dailyLimit(defaultLimit)
                        .build());
        if (p.getUsedCount() >= p.getDailyLimit()) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Дневной лимит генераций исчерпан: " + p.getDailyLimit());
        }
        p.setUsedCount(p.getUsedCount() + 1);
        repo.save(p);
    }
}