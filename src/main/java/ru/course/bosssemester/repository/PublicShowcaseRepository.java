package ru.course.bosssemester.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.course.bosssemester.entity.*;
import java.util.List;
import java.util.Optional;
public interface PublicShowcaseRepository extends JpaRepository<PublicShowcaseItem, Long> {
    List<PublicShowcaseItem> findByStatusOrderByCreatedAtDesc(ShowcaseStatus status);
    List<PublicShowcaseItem> findAllByOrderByCreatedAtDesc();
    Optional<PublicShowcaseItem> findByRequest(BossRequest request);
}
