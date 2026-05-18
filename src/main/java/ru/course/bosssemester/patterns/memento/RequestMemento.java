package ru.course.bosssemester.patterns.memento;
import ru.course.bosssemester.entity.VisualizationStyle;
import java.util.List;
public record RequestMemento(List<String> subjects, Integer difficulty, String emotion, VisualizationStyle style) {}
