package ru.course.bosssemester.patterns.builder;


import org.springframework.stereotype.Component;
import ru.course.bosssemester.dto.BossDtos.CreateBossRequest;
import ru.course.bosssemester.patterns.strategy.StyleStrategyRegistry;

@Component
public class PromptDirector {
    private final SemesterBossPromptBuilder builder;
    private final StyleStrategyRegistry styles;

    public PromptDirector(SemesterBossPromptBuilder builder, StyleStrategyRegistry styles) {
        this.builder = builder;
        this.styles = styles;
    }

    public PromptProduct construct(CreateBossRequest request) {
        builder.reset();
        builder.setSubjects(request.subjects());
        builder.setSubjectDifficultyDetails(request.subjects(), request.subjectDifficulties());
        builder.setDifficulty(request.difficulty());
        builder.setEmotion(request.emotionalBackground());
        builder.setStyleText(styles.get(request.style()).promptPart());
        builder.addSafetyAndFormatRules();
        return builder.getResult();
    }
}