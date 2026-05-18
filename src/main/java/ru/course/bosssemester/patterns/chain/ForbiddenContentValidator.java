package ru.course.bosssemester.patterns.chain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
@Component
public class ForbiddenContentValidator extends BaseGenerationValidator {
    public void validate(GenerationValidationContext c){
        String all = String.join(" ", c.request().subjects()) + " " + c.request().emotionalBackground();
        if(all.length() > 2000) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Описание слишком длинное");
        validateNext(c);
    }
}
