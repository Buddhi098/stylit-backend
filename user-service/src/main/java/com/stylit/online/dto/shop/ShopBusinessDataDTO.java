package com.stylit.online.dto.shop;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ShopBusinessDataDTO {

    @NotBlank(message = "Business Registration Number is required")
    private String businessRegNo;

    @NotNull(message = "Business Registration Date is required")
    @Past(message = "Business Registration Date must be before today")
    private LocalDate businessRegDate;

    @NotBlank(message = "Business Type is required")
    private String businessType;

    @NotBlank(message = "Business Email is required")
    @Email(message = "Invalid email format")
    private String businessEmail;

    @NotBlank(message = "Business Document is required")
    private String businessDocument;
}

