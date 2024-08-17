package com.stylit.online.dto.shopper;

import com.stylit.online.model.shopper.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDTO {

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Password is required")
    @Size(min = 6, message = "Password should have at least 6 characters")
    private String password;

    @NotEmpty(message = "Confirm Password is required")
    @Size(min = 6, message = "Confirm Password should have at least 6 characters")
    private String confirmPassword;

    @NotNull(message = "Style preference is required")
    private User.StylePreference stylePreference;

}
