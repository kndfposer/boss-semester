package ru.course.bosssemester.patterns.chain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
@Component
public class SubjectsValidator extends BaseGenerationValidator {
    public void validate(GenerationValidationContext c){
        if(c.request().subjects() == null || c.request().subjects().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Введите хотя бы один предмет");
        if(c.request().subjects().size() > 20) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Слишком много предметов: максимум 20");
        validateNext(c);
    }
}
