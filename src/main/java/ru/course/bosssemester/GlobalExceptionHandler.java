package ru.course.bosssemester;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(ResponseStatusException.class) ResponseEntity<?> rse(ResponseStatusException e){ return ResponseEntity.status(e.getStatusCode()).body(Map.of("time", Instant.now().toString(), "error", e.getReason()==null?"Ошибка":e.getReason())); }
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> val(MethodArgumentNotValidException e){ return ResponseEntity.badRequest().body(Map.of("time", Instant.now().toString(), "error", "Проверьте поля формы")); }
 @ExceptionHandler(Exception.class) ResponseEntity<?> ex(Exception e){ return ResponseEntity.status(500).body(Map.of("time", Instant.now().toString(), "error", e.getMessage()==null?"Внутренняя ошибка":e.getMessage())); }
}
