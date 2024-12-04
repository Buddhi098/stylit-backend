package com.stylit.online.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderDTO {

    private Long userId;
    private ShippingAddressDTO shippingAddress;
    private Double totalCost;
    private List<OrderItemDTO> orderItems;
}
