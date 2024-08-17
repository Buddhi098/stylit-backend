package com.stylit.online.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private GeneralInformationDTO generalInformation;
    private PricingDTO pricing;
    private MaterialCareDTO materialCare;
    private AdditionalInfoDTO additionalInfo;
    private List<VariantBoxDTO> variantBoxes;
    private String shopId;
}

