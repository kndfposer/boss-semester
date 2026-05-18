package ru.course.bosssemester.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "public_showcase_items")
public class PublicShowcaseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private BossRequest request;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShowcaseStatus status;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String moderationComment;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant moderatedAt;

    public PublicShowcaseItem() {}

    public PublicShowcaseItem(Long id, BossRequest request, ShowcaseStatus status, String title,
                              String moderationComment, Instant createdAt, Instant moderatedAt) {
        this.id = id;
        this.request = request;
        this.status = status;
        this.title = title;
        this.moderationComment = moderationComment;
        this.createdAt = createdAt;
        this.moderatedAt = moderatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BossRequest getRequest() { return request; }
    public void setRequest(BossRequest request) { this.request = request; }
    public ShowcaseStatus getStatus() { return status; }
    public void setStatus(ShowcaseStatus status) { this.status = status; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getModerationComment() { return moderationComment; }
    public void setModerationComment(String moderationComment) { this.moderationComment = moderationComment; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getModeratedAt() { return moderatedAt; }
    public void setModeratedAt(Instant moderatedAt) { this.moderatedAt = moderatedAt; }

    public static class Builder {
        private Long id;
        private BossRequest request;
        private ShowcaseStatus status;
        private String title;
        private String moderationComment;
        private Instant createdAt;
        private Instant moderatedAt;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder request(BossRequest request) { this.request = request; return this; }
        public Builder status(ShowcaseStatus status) { this.status = status; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder moderationComment(String moderationComment) { this.moderationComment = moderationComment; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder moderatedAt(Instant moderatedAt) { this.moderatedAt = moderatedAt; return this; }
        public PublicShowcaseItem build() { return new PublicShowcaseItem(id, request, status, title, moderationComment, createdAt, moderatedAt); }
    }
}