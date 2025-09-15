package com.umograd.presentation.auth.dto;

import com.umograd.domain.user.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
}
