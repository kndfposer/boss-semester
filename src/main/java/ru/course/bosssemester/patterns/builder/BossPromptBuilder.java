package ru.course.bosssemester.patterns.builder;

import java.util.List;

public interface BossPromptBuilder {
    void reset();
    void setSubjects(List<String> subjects);
    void setSubjectDifficultyDetails(List<String> subjects, List<Integer> difficulties);
    void setDifficulty(int difficulty);
    void setEmotion(String emotion);
    void setStyleText(String styleText);
    void addSafetyAndFormatRules();
    PromptProduct getResult();
}