package ru.course.bosssemester.dto;

import jakarta.validation.constraints.*;
import ru.course.bosssemester.entity.*;
import java.time.Instant;
import java.util.List;

public class BossDtos {
    public record CreateBossRequest(
            @NotEmpty List<@NotBlank String> subjects,
            List<@Min(1) @Max(10) Integer> subjectDifficulties,
            @Min(1) @Max(10) Integer difficulty,
            @NotBlank String emotionalBackground,
            @NotNull VisualizationStyle style) {}

    public record BossResponse(
            Long id,
            List<String> subjects,
            List<Integer> subjectDifficulties,
            Integer difficulty,
            String emotionalBackground,
            VisualizationStyle style,
            RequestStatus status,
            boolean favorite,
            boolean saved,
            String imageUrl,
            String errorMessage,
            Instant createdAt,
            String finalPrompt) {}

    public record CloneRequest(
            @NotEmpty List<@NotBlank String> subjects,
            List<@Min(1) @Max(10) Integer> subjectDifficulties,
            @Min(1) @Max(10) Integer difficulty,
            @NotBlank String emotionalBackground,
            @NotNull VisualizationStyle style) {}

    public record CollectionRequest(@NotBlank String title, String description) {}
    public record CollectionAddRequest(Long requestId) {}
    public record CollectionResponse(Long id, String title, String description, int size, Instant createdAt, List<BossResponse> items) {}
    public record ShowcaseSubmitRequest(String title) {}
    public record ShowcaseModerationRequest(boolean approve, String comment) {}
    public record ShowcaseResponse(
            Long id,
            Long requestId,
            String title,
            ShowcaseStatus status,
            String imageUrl,
            String owner,
            Instant createdAt,
            List<String> subjects,
            List<Integer> subjectDifficulties,
            Integer difficulty,
            String emotionalBackground,
            VisualizationStyle style) {}
}