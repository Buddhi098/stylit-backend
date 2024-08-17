package com.stylit.online.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IsEmailExistDTO {
    @NotBlank(message = "Email is required")
    private String email;
}
