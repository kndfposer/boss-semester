package ru.course.bosssemester.patterns.state;
import ru.course.bosssemester.entity.*;
public class RunningState implements BossRequestState { public void apply(BossRequest r){ r.setStatus(RequestStatus.RUNNING); } }
