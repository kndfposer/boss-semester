package ru.course.bosssemester.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.course.bosssemester.dto.AuthDtos.*;
import ru.course.bosssemester.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody AuthRequest r){
        return auth.register(r);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest r){
        return auth.login(r);
    }
}