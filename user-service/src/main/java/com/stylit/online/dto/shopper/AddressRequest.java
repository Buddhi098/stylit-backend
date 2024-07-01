package com.stylit.online.dto.shopper;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressRequest {

    @NotBlank(message = "Address is mandatory")
    private String addressLine1;
    private String addressLine2;
    @NotBlank(message = "City is mandatory")
    private String city;
    @NotBlank(message = "Province is mandatory")
    private String Province;
    @NotBlank(message = "postalCode is mandatory")
    private String postalCode;
    @NotBlank(message = "Country is mandatory")
    private String country;
}
