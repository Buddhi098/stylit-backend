package com.stylit.online.dto.shopper;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class AddressDTO {

    @NotEmpty(message = "Street address is required")
    private String streetAddress;

    private String apartment;

    @NotEmpty(message = "City is required")
    private String city;

    @NotEmpty(message = "Postal code is required")
    @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Postal code should be valid")
    private String postalCode;

    @NotEmpty(message = "Province is required")
    private String province;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDTO user;
}
