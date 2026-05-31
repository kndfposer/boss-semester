package ru.course.bosssemester.patterns.chain;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.course.bosssemester.dto.BossDtos.CreateBossRequest;
import ru.course.bosssemester.entity.VisualizationStyle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DifficultyValidatorTest {
    @Test
    void rejectsAverageDifficultyOutsideRange() {
        DifficultyValidator validator = new DifficultyValidator();
        CreateBossRequest request = new CreateBossRequest(List.of("Физика"), List.of(5), 11, "паника", VisualizationStyle.FANTASY);
        assertThrows(ResponseStatusException.class, () -> validator.validate(new GenerationValidationContext(null, request)));
    }

    @Test
    void rejectsWrongNumberOfSubjectDifficulties() {
        DifficultyValidator validator = new DifficultyValidator();
        CreateBossRequest request = new CreateBossRequest(List.of("Физика", "Java"), List.of(8), 8, "стресс", VisualizationStyle.COMIC);
        assertThrows(ResponseStatusException.class, () -> validator.validate(new GenerationValidationContext(null, request)));
    }

    @Test
    void acceptsValidSubjectDifficulties() {
        DifficultyValidator validator = new DifficultyValidator();
        CreateBossRequest request = new CreateBossRequest(List.of("Физика", "Java"), List.of(8, 6), 7, "стресс", VisualizationStyle.COMIC);
        assertDoesNotThrow(() -> validator.validate(new GenerationValidationContext(null, request)));
    }
}