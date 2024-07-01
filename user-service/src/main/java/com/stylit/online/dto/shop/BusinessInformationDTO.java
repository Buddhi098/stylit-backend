package com.stylit.online.dto.shop;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessInformationDTO {

    @NotBlank(message = "Business type is required")
    private String businessType;

    private String businessDocumentPath;

    @NotBlank(message = "Tax identification number is required")
    private String taxIdentificationNumber;

    private String operatingHours;
}
