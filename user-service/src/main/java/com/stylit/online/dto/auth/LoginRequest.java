package com.stylit.online.dto.auth;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String userName;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private enum UserRole{
        shopper,
        shop,
        courier,
        admin
    }
}
