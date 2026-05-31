package ru.course.bosssemester.patterns.chain;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.course.bosssemester.dto.BossDtos.CreateBossRequest;
import ru.course.bosssemester.entity.VisualizationStyle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubjectsValidatorTest {
    @Test
    void rejectsEmptySubjects() {
        SubjectsValidator validator = new SubjectsValidator();
        CreateBossRequest request = new CreateBossRequest(List.of(), List.of(), 5, "стресс", VisualizationStyle.FANTASY);
        assertThrows(ResponseStatusException.class, () -> validator.validate(new GenerationValidationContext(null, request)));
    }

    @Test
    void acceptsNonEmptySubjects() {
        SubjectsValidator validator = new SubjectsValidator();
        CreateBossRequest request = new CreateBossRequest(List.of("Java"), List.of(7), 7, "стресс", VisualizationStyle.CYBERPUNK);
        assertDoesNotThrow(() -> validator.validate(new GenerationValidationContext(null, request)));
    }
}