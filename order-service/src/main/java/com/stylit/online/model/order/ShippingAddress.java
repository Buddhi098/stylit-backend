package com.stylit.online.model.order;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {
    private String firstName;
    private String lastName;
    private String addressLine1;
    private String addressLine2;
    private String zipCode;
    private String province;
    private String city;
    private String country;
}
