package com.stylit.online.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OrderItemDTO {
    private Long productId;
    private Long variantId;
    private Long shopId;
    private String status;
    private String productName;
    private String color;
    private String size;
    private Double price;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}