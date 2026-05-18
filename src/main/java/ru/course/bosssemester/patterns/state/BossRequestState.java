package ru.course.bosssemester.patterns.state;
import ru.course.bosssemester.entity.BossRequest;
public interface BossRequestState { void apply(BossRequest request); }
