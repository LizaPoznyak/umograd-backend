package com.umograd.application.auth;

import com.umograd.domain.user.Role;
import com.umograd.domain.user.User;
import com.umograd.domain.user.UserRepository;
import com.umograd.presentation.auth.dto.AuthRequest;
import com.umograd.presentation.auth.dto.AuthResponse;
import com.umograd.presentation.auth.dto.RegisterRequest;
import com.umograd.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        Role role;

        // Первый пользователь в системе всегда модератор
        if (userRepository.count() == 0) {
            role = Role.ROLE_MODERATOR;
        } else {
            if (request.getRole() != null) {
                role = request.getRole();
            } else {
                role = Boolean.TRUE.equals(request.getIsParent()) ? Role.ROLE_PARENT : Role.ROLE_CHILD;
            }
        }

        // Проверка возраста для детей
        LocalDate birthDate = request.getBirthDate();
        if (role == Role.ROLE_CHILD) {
            if (birthDate == null) {
                throw new RuntimeException("Для регистрации ребёнка необходимо указать дату рождения");
            }
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            if (age < 1 || age > 18) {
                throw new RuntimeException("Возраст ребёнка должен быть от 1 до 18 лет");
            }
        }

        // Родительская связь (если задан parentUsername)
        User parent = null;
        if (role == Role.ROLE_CHILD && request.getParentUsername() != null) {
            parent = userRepository.findByUsername(request.getParentUsername())
                    .orElseThrow(() -> new RuntimeException("Родитель с таким username не найден"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(new HashSet<>(Set.of(role)))
                .birthDate(birthDate)
                .avatarUrl(request.getAvatarUrl())
                .parent(parent)
                .build();

        userRepository.save(user);
        return generateTokens(user);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return generateTokens(user);
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtTokenProvider.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!jwtTokenProvider.isTokenValid(refreshToken, toUserDetails(user))) {
            throw new RuntimeException("Невалидный refresh токен");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getBirthDate(),
                toUserDetails(user)
        );
        return new AuthResponse(newAccessToken, refreshToken, user.getId());
    }

    // ===================== Вспомогательные методы =====================

    private AuthResponse generateTokens(User user) {
        UserDetails userDetails = toUserDetails(user);

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getBirthDate(),
                userDetails
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(),
                user.getUsername(),
                user.getBirthDate(),
                userDetails
        );

        return new AuthResponse(accessToken, refreshToken, user.getId());
    }

    private UserDetails toUserDetails(User user) {
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
