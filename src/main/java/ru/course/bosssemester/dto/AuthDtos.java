package ru.course.bosssemester.dto;
import jakarta.validation.constraints.NotBlank;
public class AuthDtos {
    public record AuthRequest(@NotBlank String username, @NotBlank String password) {}
    public record AuthResponse(String token, String username, String role) {}
}
