package com.stylit.online.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ShopLocationDTO {

    @NotBlank(message = "Address Line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Province is required")
    private String province;

    @NotBlank(message = "Postal Code is required")
    private String postalCode;

    @NotBlank(message = "Latitude is required")
    private String latitude;

    @NotBlank(message = "Longitude is required")
    private String longitude;
}

