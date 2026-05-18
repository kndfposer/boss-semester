package ru.course.bosssemester.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "semester_collections")
public class SemesterCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Instant createdAt;

    @ManyToMany
    @JoinTable(name = "collection_requests",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "request_id"))
    private List<BossRequest> requests = new ArrayList<>();

    public SemesterCollection() {}

    public SemesterCollection(Long id, User user, String title, String description, Instant createdAt, List<BossRequest> requests) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.requests = requests == null ? new ArrayList<>() : requests;
    }

    public static Builder builder() { return new Builder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<BossRequest> getRequests() { return requests; }
    public void setRequests(List<BossRequest> requests) { this.requests = requests == null ? new ArrayList<>() : requests; }

    public static class Builder {
        private Long id;
        private User user;
        private String title;
        private String description;
        private Instant createdAt;
        private List<BossRequest> requests = new ArrayList<>();
        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder requests(List<BossRequest> requests) { this.requests = requests; return this; }
        public SemesterCollection build() { return new SemesterCollection(id, user, title, description, createdAt, requests); }
    }
}