package ru.course.bosssemester.patterns.observer;
import ru.course.bosssemester.entity.BossRequest;
public interface RequestEventSubscriber { void update(BossRequest request, String operation, boolean success, String message); }
