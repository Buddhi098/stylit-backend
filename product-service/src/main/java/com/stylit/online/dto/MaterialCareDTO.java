package com.stylit.online.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialCareDTO {
    private String material;
    private String pattern;
    private String careInstructions;
}
