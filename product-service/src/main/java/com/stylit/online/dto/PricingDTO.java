package com.stylit.online.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingDTO {
    private String basePrice;
    private Integer discount;
    private String discountType;
}
