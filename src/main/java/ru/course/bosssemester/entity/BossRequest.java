package ru.course.bosssemester.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boss_requests")
public class BossRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false, length = 4000)
    private String subjectsCsv;

    @Column(nullable = false)
    private Integer difficulty;

    @Column(length = 4000)
    private String subjectDifficultiesCsv;

    @Column(nullable = false, length = 200)
    private String emotionalBackground;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisualizationStyle style;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(length = 8000)
    private String finalPrompt;

    @Column(length = 4000)
    private String errorMessage;

    private boolean favorite;
    private boolean saved;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @OneToOne(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private BossImageArtifact artifact;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApiInteractionLog> apiLogs = new ArrayList<>();

    public BossRequest() {}

    public BossRequest(Long id, User user, String subjectsCsv, Integer difficulty, String subjectDifficultiesCsv,
                       String emotionalBackground, VisualizationStyle style, RequestStatus status, String finalPrompt,
                       String errorMessage, boolean favorite, boolean saved, Instant createdAt, Instant updatedAt,
                       BossImageArtifact artifact, List<ApiInteractionLog> apiLogs) {
        this.id = id;
        this.user = user;
        this.subjectsCsv = subjectsCsv;
        this.difficulty = difficulty;
        this.subjectDifficultiesCsv = subjectDifficultiesCsv;
        this.emotionalBackground = emotionalBackground;
        this.style = style;
        this.status = status;
        this.finalPrompt = finalPrompt;
        this.errorMessage = errorMessage;
        this.favorite = favorite;
        this.saved = saved;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.artifact = artifact;
        this.apiLogs = apiLogs == null ? new ArrayList<>() : apiLogs;
    }

    public static Builder builder() { return new Builder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getSubjectsCsv() { return subjectsCsv; }
    public void setSubjectsCsv(String subjectsCsv) { this.subjectsCsv = subjectsCsv; }
    public Integer getDifficulty() { return difficulty; }
    public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    public String getSubjectDifficultiesCsv() { return subjectDifficultiesCsv; }
    public void setSubjectDifficultiesCsv(String subjectDifficultiesCsv) { this.subjectDifficultiesCsv = subjectDifficultiesCsv; }
    public String getEmotionalBackground() { return emotionalBackground; }
    public void setEmotionalBackground(String emotionalBackground) { this.emotionalBackground = emotionalBackground; }
    public VisualizationStyle getStyle() { return style; }
    public void setStyle(VisualizationStyle style) { this.style = style; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public String getFinalPrompt() { return finalPrompt; }
    public void setFinalPrompt(String finalPrompt) { this.finalPrompt = finalPrompt; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
    public boolean isSaved() { return saved; }
    public void setSaved(boolean saved) { this.saved = saved; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public BossImageArtifact getArtifact() { return artifact; }
    public void setArtifact(BossImageArtifact artifact) {
        this.artifact = artifact;
        if (artifact != null) {
            artifact.setRequest(this);
        }
    }
    public List<ApiInteractionLog> getApiLogs() { return apiLogs; }
    public void setApiLogs(List<ApiInteractionLog> apiLogs) { this.apiLogs = apiLogs == null ? new ArrayList<>() : apiLogs; }

    public static class Builder {
        private Long id;
        private User user;
        private String subjectsCsv;
        private Integer difficulty;
        private String subjectDifficultiesCsv;
        private String emotionalBackground;
        private VisualizationStyle style;
        private RequestStatus status;
        private String finalPrompt;
        private String errorMessage;
        private boolean favorite;
        private boolean saved;
        private Instant createdAt;
        private Instant updatedAt;
        private BossImageArtifact artifact;
        private List<ApiInteractionLog> apiLogs = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder subjectsCsv(String subjectsCsv) { this.subjectsCsv = subjectsCsv; return this; }
        public Builder difficulty(Integer difficulty) { this.difficulty = difficulty; return this; }
        public Builder subjectDifficultiesCsv(String subjectDifficultiesCsv) { this.subjectDifficultiesCsv = subjectDifficultiesCsv; return this; }
        public Builder emotionalBackground(String emotionalBackground) { this.emotionalBackground = emotionalBackground; return this; }
        public Builder style(VisualizationStyle style) { this.style = style; return this; }
        public Builder status(RequestStatus status) { this.status = status; return this; }
        public Builder finalPrompt(String finalPrompt) { this.finalPrompt = finalPrompt; return this; }
        public Builder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }
        public Builder favorite(boolean favorite) { this.favorite = favorite; return this; }
        public Builder saved(boolean saved) { this.saved = saved; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder artifact(BossImageArtifact artifact) { this.artifact = artifact; return this; }
        public Builder apiLogs(List<ApiInteractionLog> apiLogs) { this.apiLogs = apiLogs; return this; }
        public BossRequest build() {
            return new BossRequest(id, user, subjectsCsv, difficulty, subjectDifficultiesCsv, emotionalBackground, style,
                    status, finalPrompt, errorMessage, favorite, saved, createdAt, updatedAt, artifact, apiLogs);
        }
    }
}