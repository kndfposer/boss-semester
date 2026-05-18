package ru.course.bosssemester.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.course.bosssemester.entity.User;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> { Optional<User> findByUsername(String username); }
