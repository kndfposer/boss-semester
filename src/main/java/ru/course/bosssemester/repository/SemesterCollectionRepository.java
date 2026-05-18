package ru.course.bosssemester.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.course.bosssemester.entity.*;
import java.util.List;
public interface SemesterCollectionRepository extends JpaRepository<SemesterCollection, Long> { List<SemesterCollection> findByUserOrderByCreatedAtDesc(User user); }
