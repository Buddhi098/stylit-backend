package com.stylit.online.dto.shop;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ShopDTO {

    @NotBlank(message = "Shop Name is required")
    private String shopName;

    @NotBlank(message = "Shop Email is required")
    @Email(message = "Invalid email format")
    private String shopEmail;

    @NotBlank(message = "Shop Contact Number is required")
    private String shopContactNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    @Valid
    private ShopLocationDTO shopLocation;

    @Valid
    private ShopBusinessDataDTO shopBusinessData;

    @Valid
    private ShopInformationDTO shopInformation;
}

