package com.stylit.online.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantBoxDTO {
    private String colorVariant;
    private List<SizeQuantityChartDTO> sizeQuantityChart;
    private String status;
}
