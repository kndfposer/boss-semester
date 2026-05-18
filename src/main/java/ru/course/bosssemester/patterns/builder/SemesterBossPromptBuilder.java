package ru.course.bosssemester.patterns.builder;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class SemesterBossPromptBuilder implements BossPromptBuilder {
    private final StringBuilder user = new StringBuilder();
    private String system;

    public void reset(){
        user.setLength(0);
        system = "Ты создаёшь одно изображение финального босса семестра. Не добавляй текст на картинку.";
    }

    public void setSubjects(List<String> subjects){
        user.append("Создай одного финального босса, объединяющего предметы: ")
                .append(String.join(", ", subjects))
                .append(". ");
    }

    public void setSubjectDifficultyDetails(List<String> subjects, List<Integer> difficulties){
        if (subjects == null || difficulties == null || subjects.size() != difficulties.size()) {
            return;
        }
        List<String> pairs = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            pairs.add(subjects.get(i) + " — " + difficulties.get(i) + "/10");
        }
        user.append("Сложность по отдельным предметам: ")
                .append(String.join("; ", pairs))
                .append(". Эти значения должны влиять на детали образа: более сложные предметы выглядят более опасными и доминирующими. ");
    }

    public void setDifficulty(int difficulty){
        user.append("Средняя субъективная сложность семестра: ")
                .append(difficulty)
                .append(" из 10. Чем выше средняя сложность, тем более грозный образ. ");
    }

    public void setEmotion(String emotion){
        user.append("Эмоциональный фон студента: ").append(emotion).append(". ");
    }

    public void setStyleText(String styleText){
        user.append("Визуальный стиль: ").append(styleText).append(". ");
    }

    public void addSafetyAndFormatRules(){
        user.append("Должен быть ровно один центральный персонаж/сущность, без логотипов, без водяных знаков, без текста, качественная иллюстрация.");
    }

    public PromptProduct getResult(){
        return new PromptProduct(system, "Нарисуй изображение: " + user);
    }
}
