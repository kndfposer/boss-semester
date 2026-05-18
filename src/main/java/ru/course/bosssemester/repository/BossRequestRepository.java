package ru.course.bosssemester.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.course.bosssemester.entity.*;

import java.util.List;

public interface BossRequestRepository extends JpaRepository<BossRequest, Long> {
    List<BossRequest> findByUserOrderByCreatedAtDesc(User user);
    List<BossRequest> findByUserAndFavoriteTrueOrderByCreatedAtDesc(User user);
    List<BossRequest> findByUserAndSavedTrueOrderByCreatedAtDesc(User user);
}