package com.stylit.online.dto.shop;

import com.stylit.online.model.shop.Shop;
import lombok.Data;

@Data
public class ShopStatusDTO {
    private Long id;
    private Shop.Status status;
}
