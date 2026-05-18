package ru.course.bosssemester.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.course.bosssemester.entity.AuthSession;
import java.util.Optional;
public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> { Optional<AuthSession> findByToken(String token); }
