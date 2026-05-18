package ru.course.bosssemester.patterns.memento;
import ru.course.bosssemester.entity.BossRequest;
import java.util.Arrays;
public class RequestOriginator {
    private final BossRequest request;
    public RequestOriginator(BossRequest request){ this.request = request; }
    public RequestMemento save(){ return new RequestMemento(Arrays.stream(request.getSubjectsCsv().split(",")).map(String::trim).toList(), request.getDifficulty(), request.getEmotionalBackground(), request.getStyle()); }
}
