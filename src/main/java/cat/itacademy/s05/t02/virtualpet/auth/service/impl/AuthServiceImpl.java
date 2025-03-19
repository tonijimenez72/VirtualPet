package cat.itacademy.s05.t02.virtualpet.auth.service.impl;

import cat.itacademy.s05.t02.virtualpet.auth.config.jwt.JwtTokenUtil;
import cat.itacademy.s05.t02.virtualpet.auth.dto.AuthResponse;
import cat.itacademy.s05.t02.virtualpet.auth.dto.LoginRequest;
import cat.itacademy.s05.t02.virtualpet.auth.dto.RegisterRequest;
import cat.itacademy.s05.t02.virtualpet.auth.enums.UserRole;
import cat.itacademy.s05.t02.virtualpet.auth.model.User;
import cat.itacademy.s05.t02.virtualpet.auth.repository.UserRepository;
import cat.itacademy.s05.t02.virtualpet.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User newUser = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole() != null ? registerRequest.getRole() : UserRole.PLAYER)
                .build();

        userRepository.save(newUser);
        String token = jwtTokenUtil.generateToken(newUser.getEmail());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtTokenUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}