package com.stylit.online.dto.courier;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourierDTO {

    @NotBlank(message = "Courier service name is required")
    @Size(max = 100, message = "Courier service name must be up to 100 characters")
    private String courierName;

    @NotBlank(message = "Email is required")
    @Email(message = "provide valid email address")
    private String courierEmail;

    @NotBlank(message = "contact number is required")
    private String courierContactNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6)
    private String password;

    @NotBlank(message = "Confirm Password is required")
    @Size(min = 6)
    private String confirm_password;

    @Valid
    private CourierLocationDTO courierLocation;

    @Valid
    private CourierBusinessDataDTO courierBusinessData;
}
