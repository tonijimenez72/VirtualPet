package cat.itacademy.s05.t02.virtualpet.auth.service.impl;

import cat.itacademy.s05.t02.virtualpet.auth.config.jwt.JwtTokenUtil;
import cat.itacademy.s05.t02.virtualpet.auth.dto.AuthResponse;
import cat.itacademy.s05.t02.virtualpet.auth.dto.LoginRequest;
import cat.itacademy.s05.t02.virtualpet.auth.dto.RegisterRequest;
import cat.itacademy.s05.t02.virtualpet.enums.UserRole;
import cat.itacademy.s05.t02.virtualpet.auth.service.AuthService;
import cat.itacademy.s05.t02.virtualpet.model.User;
import cat.itacademy.s05.t02.virtualpet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole() != null ? registerRequest.getRole() : UserRole.PLAYER)
                .build();

        userRepository.save(user);

        String token = jwtTokenUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        String token = jwtTokenUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }
}
