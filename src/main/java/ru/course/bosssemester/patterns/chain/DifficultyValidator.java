package ru.course.bosssemester.patterns.chain;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Component
public class DifficultyValidator extends BaseGenerationValidator {
    public void validate(GenerationValidationContext c){
        int d = c.request().difficulty();
        if(d < 1 || d > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Средняя сложность должна быть от 1 до 10");
        }

        List<Integer> perSubject = c.request().subjectDifficulties();
        if (perSubject != null && !perSubject.isEmpty()) {
            if (perSubject.size() != c.request().subjects().size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Для каждого предмета должна быть указана своя сложность");
            }
            for (Integer value : perSubject) {
                if (value == null || value < 1 || value > 10) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Сложность каждого предмета должна быть от 1 до 10");
                }
            }
        }

        validateNext(c);
    }
}

