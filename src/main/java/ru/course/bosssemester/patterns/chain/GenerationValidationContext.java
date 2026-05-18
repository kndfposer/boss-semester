package ru.course.bosssemester.patterns.chain;
import ru.course.bosssemester.dto.BossDtos.CreateBossRequest;
import ru.course.bosssemester.entity.User;
public record GenerationValidationContext(User user, CreateBossRequest request) {}
