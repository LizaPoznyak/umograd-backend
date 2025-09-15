package com.umograd.application.user.command;

import com.umograd.domain.user.Role;
import com.umograd.domain.user.User;
import com.umograd.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreateChildCommandHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User handle(CreateChildCommand command) {
        User parent = userRepository.findById(command.parentId())
                .orElseThrow(() -> new RuntimeException("Родитель не найден"));

        User child = User.builder()
                .username(command.username())
                .email(command.email())
                .password(passwordEncoder.encode(command.password()))
                .roles(Set.of(Role.ROLE_CHILD))
                .parent(parent)
                .build();

        return userRepository.save(child);
    }
}
