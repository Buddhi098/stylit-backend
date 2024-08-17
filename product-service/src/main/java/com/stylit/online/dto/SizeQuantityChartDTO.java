package com.stylit.online.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SizeQuantityChartDTO {
    private String size;
    private Integer quantity;
}
