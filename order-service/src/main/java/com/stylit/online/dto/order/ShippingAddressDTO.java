package com.stylit.online.dto.order;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShippingAddressDTO {
    private String firstName;
    private String lastName;
    private String addressLine1;
    private String addressLine2;
    private String zipCode;
    private String province;
    private String city;
    private String country;
}