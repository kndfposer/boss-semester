package ru.course.bosssemester.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "api_interaction_logs")
public class ApiInteractionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private BossRequest request;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private boolean success;

    @Column(length = 2000)
    private String message;

    @Column(nullable = false)
    private Instant createdAt;

    public ApiInteractionLog() {}

    public ApiInteractionLog(Long id, BossRequest request, String operation, boolean success, String message, Instant createdAt) {
        this.id = id;
        this.request = request;
        this.operation = operation;
        this.success = success;
        this.message = message;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BossRequest getRequest() { return request; }
    public void setRequest(BossRequest request) { this.request = request; }
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class Builder {
        private Long id;
        private BossRequest request;
        private String operation;
        private boolean success;
        private String message;
        private Instant createdAt;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder request(BossRequest request) { this.request = request; return this; }
        public Builder operation(String operation) { this.operation = operation; return this; }
        public Builder success(boolean success) { this.success = success; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public ApiInteractionLog build() { return new ApiInteractionLog(id, request, operation, success, message, createdAt); }
    }
}