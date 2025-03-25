package cat.itacademy.s05.t02.virtualpet.auth.controller;

import cat.itacademy.s05.t02.virtualpet.auth.dto.AuthResponse;
import cat.itacademy.s05.t02.virtualpet.auth.dto.LoginRequest;
import cat.itacademy.s05.t02.virtualpet.auth.dto.RegisterRequest;
import cat.itacademy.s05.t02.virtualpet.auth.service.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user with an email, password and optional role. Returns a JWT token."
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "Authenticate user",
            description = "Logs in the user with email and password. Returns a JWT token if successful."
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('PLAYER', 'ADMIN')")
    @Operation(summary = "Logout user", description = "Invalidates the token client-side. Client should delete the token manually.")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful. Please remove the token on the client side.");
    }
}