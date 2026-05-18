package ru.course.bosssemester.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.course.bosssemester.entity.*;
import java.time.LocalDate;
import java.util.Optional;
public interface RateLimitPolicyRepository extends JpaRepository<RateLimitPolicy, Long> { Optional<RateLimitPolicy> findByUserAndDay(User user, LocalDate day); }
