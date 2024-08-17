package com.stylit.online.dto.otp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyRequest {
    @NotBlank(message = "email is required")
    @Email(message = "enter valid email")
    private String email;
    @NotBlank(message = "otp code is required")
    private String otp;
}
