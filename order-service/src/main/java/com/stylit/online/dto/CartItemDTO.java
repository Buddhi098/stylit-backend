package com.stylit.online.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemDTO {
    private Long productId;
    private Long variantId;
    private String productName;
    private String color;
    private String size;
    private Double price;
    private Integer quantity;
}
