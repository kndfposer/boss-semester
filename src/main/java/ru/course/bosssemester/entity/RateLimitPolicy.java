package ru.course.bosssemester.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "rate_limit_policies", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "day"}))
public class RateLimitPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private LocalDate day;

    @Column(nullable = false)
    private Integer usedCount;

    @Column(nullable = false)
    private Integer dailyLimit;

    public RateLimitPolicy() {}

    public RateLimitPolicy(Long id, User user, LocalDate day, Integer usedCount, Integer dailyLimit) {
        this.id = id;
        this.user = user;
        this.day = day;
        this.usedCount = usedCount;
        this.dailyLimit = dailyLimit;
    }

    public static Builder builder() { return new Builder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getDay() { return day; }
    public void setDay(LocalDate day) { this.day = day; }
    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    public Integer getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(Integer dailyLimit) { this.dailyLimit = dailyLimit; }

    public static class Builder {
        private Long id;
        private User user;
        private LocalDate day;
        private Integer usedCount;
        private Integer dailyLimit;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder day(LocalDate day) { this.day = day; return this; }
        public Builder usedCount(Integer usedCount) { this.usedCount = usedCount; return this; }
        public Builder dailyLimit(Integer dailyLimit) { this.dailyLimit = dailyLimit; return this; }
        public RateLimitPolicy build() { return new RateLimitPolicy(id, user, day, usedCount, dailyLimit); }
    }
}