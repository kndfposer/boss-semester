package ru.course.bosssemester.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "boss_image_artifacts")
public class BossImageArtifact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private BossRequest request;

    private String gigaChatFileId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private String localPath;

    @Column(nullable = false)
    private Long sizeBytes;

    @Column(nullable = false)
    private Instant createdAt;

    public BossImageArtifact() {}

    public BossImageArtifact(Long id, BossRequest request, String gigaChatFileId, String fileName, String contentType, String localPath, Long sizeBytes, Instant createdAt) {
        this.id = id;
        this.request = request;
        this.gigaChatFileId = gigaChatFileId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.localPath = localPath;
        this.sizeBytes = sizeBytes;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BossRequest getRequest() { return request; }
    public void setRequest(BossRequest request) { this.request = request; }
    public String getGigaChatFileId() { return gigaChatFileId; }
    public void setGigaChatFileId(String gigaChatFileId) { this.gigaChatFileId = gigaChatFileId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getLocalPath() { return localPath; }
    public void setLocalPath(String localPath) { this.localPath = localPath; }
    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class Builder {
        private Long id;
        private BossRequest request;
        private String gigaChatFileId;
        private String fileName;
        private String contentType;
        private String localPath;
        private Long sizeBytes;
        private Instant createdAt;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder request(BossRequest request) { this.request = request; return this; }
        public Builder gigaChatFileId(String gigaChatFileId) { this.gigaChatFileId = gigaChatFileId; return this; }
        public Builder fileName(String fileName) { this.fileName = fileName; return this; }
        public Builder contentType(String contentType) { this.contentType = contentType; return this; }
        public Builder localPath(String localPath) { this.localPath = localPath; return this; }
        public Builder sizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public BossImageArtifact build() { return new BossImageArtifact(id, request, gigaChatFileId, fileName, contentType, localPath, sizeBytes, createdAt); }
    }
}