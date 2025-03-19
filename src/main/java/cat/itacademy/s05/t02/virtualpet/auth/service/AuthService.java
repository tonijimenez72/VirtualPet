package cat.itacademy.s05.t02.virtualpet.auth.service;

import cat.itacademy.s05.t02.virtualpet.auth.dto.AuthResponse;
import cat.itacademy.s05.t02.virtualpet.auth.dto.LoginRequest;
import cat.itacademy.s05.t02.virtualpet.auth.dto.RegisterRequest;
import cat.itacademy.s05.t02.virtualpet.auth.model.User;

import java.util.List;
import java.util.Optional;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    List<User> getAllUsers();
    Optional<User> getUserByEmail(String email);
}