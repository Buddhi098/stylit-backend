package com.stylit.online.dto.shopper;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    private String firstName;

    private String lastName;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Password is required")
    @Size(min = 6, message = "Password should have at least 6 characters")
    private String password;

    @NotEmpty(message = "Password is required")
    @Size(min = 6, message = "Password should have at least 6 characters")
    private String confirmPassword;

    @NotEmpty(message = "Style preference is required")
    @Enumerated(EnumType.STRING)
    @Valid
    private StylePreference stylePreference;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<AddressDTO> addresses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<PaymentMethodDTO> paymentMethods;


    public enum StylePreference {
        MENSWEAR, WOMENSWEAR
    }
}