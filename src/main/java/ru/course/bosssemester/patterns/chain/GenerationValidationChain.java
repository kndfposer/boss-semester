package ru.course.bosssemester.patterns.chain;
import org.springframework.stereotype.Component;
@Component
public class GenerationValidationChain {
    private final GenerationValidator first;
    public GenerationValidationChain(SubjectsValidator s, DifficultyValidator d, ForbiddenContentValidator f){ s.setNext(d); d.setNext(f); this.first = s; }
    public void validate(GenerationValidationContext ctx){ first.validate(ctx); }
}
