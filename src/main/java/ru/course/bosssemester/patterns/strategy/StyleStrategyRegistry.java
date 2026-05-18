package ru.course.bosssemester.patterns.strategy;
import org.springframework.stereotype.Component;
import ru.course.bosssemester.entity.VisualizationStyle;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Component
public class StyleStrategyRegistry {
    private final Map<String, StyleStrategy> strategies;
    public StyleStrategyRegistry(List<StyleStrategy> list){ this.strategies = list.stream().collect(Collectors.toMap(StyleStrategy::styleName, Function.identity())); }
    public StyleStrategy get(VisualizationStyle style){ return Optional.ofNullable(strategies.get(style.name())).orElseThrow(); }
}
