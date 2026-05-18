package ru.course.bosssemester.patterns.state;
import ru.course.bosssemester.entity.*;
public class PendingState implements BossRequestState { public void apply(BossRequest r){ r.setStatus(RequestStatus.PENDING); } }
