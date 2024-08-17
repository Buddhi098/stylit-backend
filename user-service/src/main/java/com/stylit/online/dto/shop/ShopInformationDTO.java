package com.stylit.online.dto.shop;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Data
public class ShopInformationDTO {

    @NotBlank(message = "Shop Description is required")
    private String shopDescription;

    private List<CategoryDTO> categories;

    private String facebookLink;

    private String instagramLink;

    @NotBlank(message = "Storefront Image is required")
    private String logo;
}

