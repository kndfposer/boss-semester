package ru.course.bosssemester.patterns.proxy;

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


    private int defaultLimit = 10;

    private final RateLimitPolicyRepository policies;

    public RateLimitService(RateLimitPolicyRepository policies) {
        this.policies = policies;
    }

    @Transactional
    public void checkAndIncrement(User user) {
        if (user == null || user.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Пользователь не найден для проверки лимита генераций"
            );
        }

        LocalDate today = LocalDate.now();

        RateLimitPolicy policy = policies
                .findByUserAndDay(user, today)
                .orElseGet(() -> RateLimitPolicy.builder()
                        .user(user)
                        .day(today)
                        .usedCount(0)
                        .dailyLimit(defaultLimit)
                        .build()
                );

        if (policy.getUsedCount() >= policy.getDailyLimit()) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Превышен дневной лимит генераций. Лимит: "
                            + policy.getDailyLimit()
                            + " запросов в день."
            );
        }

        policy.setUsedCount(policy.getUsedCount() + 1);

    
        policies.save(policy);
    }

    @Transactional(readOnly = true)
    public int getRemainingToday(User user) {
        if (user == null || user.getId() == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();

        return policies
                .findByUserAndDay(user, today)
                .map(policy -> Math.max(0, policy.getDailyLimit() - policy.getUsedCount()))
                .orElse(defaultLimit);
    }

    @Transactional(readOnly = true)
    public RateLimitPolicy getTodayPolicy(User user) {
        if (user == null || user.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Пользователь не найден для получения лимита"
            );
        }

        LocalDate today = LocalDate.now();

        return policies
                .findByUserAndDay(user, today)
                .orElseGet(() -> RateLimitPolicy.builder()
                        .user(user)
                        .day(today)
                        .usedCount(0)
                        .dailyLimit(defaultLimit)
                        .build()
                );
    }
}


