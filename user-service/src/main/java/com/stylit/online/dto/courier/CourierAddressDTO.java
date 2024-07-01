package com.stylit.online.dto.courier;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourierAddressDTO {

    @NotBlank(message = "Address Line 1 is mandatory")
    @Size(max = 255, message = "Address Line 1 cannot be longer than 255 characters")
    private String addressLine1;

    @Size(max = 255, message = "Address Line 2 cannot be longer than 255 characters")
    private String addressLine2;

    @NotBlank(message = "Province is mandatory")
    @Size(max = 100, message = "Province cannot be longer than 100 characters")
    private String province;

    @NotBlank(message = "City is mandatory")
    @Size(max = 100, message = "City cannot be longer than 100 characters")
    private String city;

    @NotBlank(message = "Postal Code is mandatory")
    private String postalCode;
}
