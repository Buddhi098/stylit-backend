package com.stylit.online.dto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartDTO {
    private Long userId;
    private List<CartItemDTO> cartItemsDto;
}
