package ru.course.bosssemester.patterns.proxy;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import ru.course.bosssemester.entity.RateLimitPolicy;
import ru.course.bosssemester.entity.User;
import ru.course.bosssemester.repository.RateLimitPolicyRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RateLimitServiceTest {
    @Test
    void createsPolicyAndIncrementsCounter() {
        RateLimitPolicyRepository repo = mock(RateLimitPolicyRepository.class);
        User user = User.builder().id(1L).username("m").role("USER").build();
        when(repo.findByUserAndDay(eq(user), any(LocalDate.class))).thenReturn(Optional.empty());
        when(repo.save(any(RateLimitPolicy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RateLimitService service = new RateLimitService(repo);
        ReflectionTestUtils.setField(service, "defaultLimit", 5);

        service.checkAndIncrement(user);

        verify(repo).save(argThat(policy -> policy.getUsedCount() == 1 && policy.getDailyLimit() == 5));
    }

    @Test
    void rejectsWhenDailyLimitIsExceeded() {
        RateLimitPolicyRepository repo = mock(RateLimitPolicyRepository.class);
        User user = User.builder().id(1L).username("m").role("USER").build();
        RateLimitPolicy policy = RateLimitPolicy.builder()
                .user(user)
                .day(LocalDate.now())
                .usedCount(5)
                .dailyLimit(5)
                .build();
        when(repo.findByUserAndDay(eq(user), any(LocalDate.class))).thenReturn(Optional.of(policy));

        RateLimitService service = new RateLimitService(repo);
        ReflectionTestUtils.setField(service, "defaultLimit", 5);

        assertThrows(ResponseStatusException.class, () -> service.checkAndIncrement(user));
        verify(repo, never()).save(any());
    }
}