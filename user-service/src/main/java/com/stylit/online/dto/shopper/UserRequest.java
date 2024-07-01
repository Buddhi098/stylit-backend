package com.stylit.online.dto.shopper;

import com.stylit.online.model.shopper.User;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotNull(message = "Address is mandatory")
    private AddressRequest address;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should have at least 6 characters")
    private String password;

    @Past(message = "Birthday must be a past date")
    private Date birthday;

    private User.Gender gender;

    @NotBlank(message = "Mobile number is mandatory")
    @Size(min = 10, max = 15, message = "Mobile number should be between 10 and 15 characters")
    private String mobileNumber;
}
