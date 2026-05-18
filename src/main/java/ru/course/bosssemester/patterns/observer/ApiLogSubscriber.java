package ru.course.bosssemester.patterns.observer;

import org.springframework.stereotype.Component;
import ru.course.bosssemester.entity.ApiInteractionLog;
import ru.course.bosssemester.entity.BossRequest;
import ru.course.bosssemester.repository.ApiInteractionLogRepository;

import java.time.Instant;

@Component
public class ApiLogSubscriber implements RequestEventSubscriber {
    private final ApiInteractionLogRepository logs;

    public ApiLogSubscriber(ApiInteractionLogRepository logs) {
        this.logs = logs;
    }

    public void update(BossRequest request, String operation, boolean success, String message) {
        logs.save(ApiInteractionLog.builder()
                .request(request)
                .operation(operation)
                .success(success)
                .message(message)
                .createdAt(Instant.now())
                .build());
    }
}