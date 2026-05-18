package ru.course.bosssemester.patterns.state;
import ru.course.bosssemester.entity.*;
public class ErrorState implements BossRequestState { private final String message; public ErrorState(String message){this.message=message;} public void apply(BossRequest r){ r.setStatus(RequestStatus.ERROR); r.setErrorMessage(message); } }
