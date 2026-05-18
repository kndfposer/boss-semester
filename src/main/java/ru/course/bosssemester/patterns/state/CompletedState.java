package ru.course.bosssemester.patterns.state;
import ru.course.bosssemester.entity.*;
public class CompletedState implements BossRequestState { public void apply(BossRequest r){ r.setStatus(RequestStatus.COMPLETED); r.setErrorMessage(null); } }
