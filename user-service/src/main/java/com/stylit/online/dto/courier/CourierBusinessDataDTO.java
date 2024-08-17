package com.stylit.online.dto.courier;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CourierBusinessDataDTO {
    @NotBlank(message = "Business registration number is required")
    @Size(max = 50, message = "Business registration number must be up to 50 characters")
    private String businessRegNo;

    @PastOrPresent(message = "Business registration date must be in the past or present")
    private LocalDate businessRegDate;

    @NotBlank(message = "Business type is required")
    @Size(max = 100, message = "Business type must be up to 100 characters")
    private String businessType;

    @NotBlank(message = "Business email is required")
    @Email(message = "Business email should be valid")
    private String businessEmail;

    @NotBlank(message = "Business document is required")
    @Lob
    private String businessDocument;
}
