package com.stylit.online.dto.courier;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDetailsDTO {

    @NotBlank(message = "Business owner name is required")
    @Size(max = 100, message = "Business owner name must be up to 100 characters")
    private String businessOwnerName;

    @NotBlank(message = "Business registration number is required")
    @Size(max = 50, message = "Business registration number must be up to 50 characters")
    private String businessRegistrationNumber;

    private String businessDocumentAsBase64;
}
