package com.stylit.online.dto.courier;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CourierLocationDTO {
    @NotBlank(message = "Address Line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "Province is required")
    private String province;

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Latitude is required")
    private Float latitude;

    @NotNull(message = "Longitude is required")
    private Float longitude;

    @NotBlank(message = "Postal Code is required")
    private String postalCode;
}
