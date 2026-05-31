package ru.course.bosssemester.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "rate_limit_policies",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "limit_day"})
)
public class RateLimitPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(name = "limit_day", nullable = false)
    private LocalDate day;

    @Column(name = "used_count", nullable = false)
    private int usedCount;

    @Column(name = "daily_limit", nullable = false)
    private int dailyLimit;

    public RateLimitPolicy() {
    }

    public RateLimitPolicy(Long id, User user, LocalDate day, int usedCount, int dailyLimit) {
        this.id = id;
        this.user = user;
        this.day = day;
        this.usedCount = usedCount;
        this.dailyLimit = dailyLimit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void increment() {
        this.usedCount++;
    }

    public boolean isLimitExceeded() {
        return usedCount >= dailyLimit;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getDay() {
        return day;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public int getDailyLimit() {
        return dailyLimit;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public void setDailyLimit(int dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public static class Builder {
        private Long id;
        private User user;
        private LocalDate day;
        private int usedCount;
        private int dailyLimit;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder day(LocalDate day) {
            this.day = day;
            return this;
        }

        public Builder usedCount(int usedCount) {
            this.usedCount = usedCount;
            return this;
        }

        public Builder dailyLimit(int dailyLimit) {
            this.dailyLimit = dailyLimit;
            return this;
        }

        public RateLimitPolicy build() {
            return new RateLimitPolicy(id, user, day, usedCount, dailyLimit);
        }
    }
}