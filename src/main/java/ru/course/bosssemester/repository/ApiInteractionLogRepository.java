package ru.course.bosssemester.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.course.bosssemester.entity.ApiInteractionLog;
public interface ApiInteractionLogRepository extends JpaRepository<ApiInteractionLog, Long> {}
