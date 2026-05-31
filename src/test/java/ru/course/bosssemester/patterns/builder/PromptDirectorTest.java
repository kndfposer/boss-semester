package ru.course.bosssemester.patterns.builder;

import org.junit.jupiter.api.Test;
import ru.course.bosssemester.dto.BossDtos.CreateBossRequest;
import ru.course.bosssemester.entity.VisualizationStyle;
import ru.course.bosssemester.patterns.strategy.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PromptDirectorTest {
    @Test
    void buildsPromptWithSubjectsDifficultiesAndStyle() {
        StyleStrategyRegistry registry = new StyleStrategyRegistry(List.of(
                new FantasyStyleStrategy(), new CyberpunkStyleStrategy(), new ComicStyleStrategy(), new AbstractArtStyleStrategy()
        ));
        PromptDirector director = new PromptDirector(new SemesterBossPromptBuilder(), registry);
        CreateBossRequest request = new CreateBossRequest(
                List.of("Java", "Базы данных"),
                List.of(8, 6),
                7,
                "стресс",
                VisualizationStyle.CYBERPUNK
        );

        PromptProduct product = director.construct(request);

        assertTrue(product.systemPrompt().contains("одно изображение"));
        assertTrue(product.userPrompt().contains("Java"));
        assertTrue(product.userPrompt().contains("Базы данных"));
        assertTrue(product.userPrompt().contains("8/10"));
        assertTrue(product.userPrompt().contains("киберпанк"));
    }
}