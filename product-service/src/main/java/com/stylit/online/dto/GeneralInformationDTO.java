package com.stylit.online.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneralInformationDTO {
    private String sku;
    private String productName;
    private String gender;
    private String category;
    private String subcategory;
    private String brand;
    private String description;
}

