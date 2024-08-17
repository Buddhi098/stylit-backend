package com.stylit.online.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdditionalInfoDTO {
    private List<String> occasions;
    private String season;
    private String ageGroup;
}
