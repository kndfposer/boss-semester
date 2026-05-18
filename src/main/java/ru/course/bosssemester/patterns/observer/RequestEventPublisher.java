package ru.course.bosssemester.patterns.observer;
import org.springframework.stereotype.Component;
import ru.course.bosssemester.entity.BossRequest;
import java.util.List;
@Component
public class RequestEventPublisher {
    private final List<RequestEventSubscriber> subscribers;
    public RequestEventPublisher(List<RequestEventSubscriber> subscribers){this.subscribers=subscribers;}
    public void notify(BossRequest r, String operation, boolean success, String message){ subscribers.forEach(s -> s.update(r, operation, success, message)); }
}
