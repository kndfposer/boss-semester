package ru.course.bosssemester.patterns.memento;

import org.junit.jupiter.api.Test;
import ru.course.bosssemester.entity.BossRequest;
import ru.course.bosssemester.entity.VisualizationStyle;

import static org.junit.jupiter.api.Assertions.*;

class RequestOriginatorTest {
    @Test
    void savesRequestStateToMemento() {
        BossRequest request = BossRequest.builder()
                .subjectsCsv("Java, Базы данных")
                .difficulty(8)
                .emotionalBackground("стресс")
                .style(VisualizationStyle.COMIC)
                .build();

        RequestMemento memento = new RequestOriginator(request).save();

        assertEquals(2, memento.subjects().size());
        assertEquals("Java", memento.subjects().get(0));
        assertEquals(8, memento.difficulty());
        assertEquals("стресс", memento.emotion());
        assertEquals(VisualizationStyle.COMIC, memento.style());
    }
}