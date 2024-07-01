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
public class CourierPaymentDetailsDTO {

    @NotBlank(message = "Bank holder name is required")
    @Size(max = 100, message = "Bank holder name must be up to 100 characters")
    private String bankHolderName;

    @NotBlank(message = "Bank name is required")
    @Size(max = 100, message = "Bank name must be up to 100 characters")
    private String bankName;

    @NotBlank(message = "Bank code is required")
    @Size(max = 20, message = "Bank code must be up to 20 characters")
    private String bankCode;

    @NotBlank(message = "Branch name is required")
    @Size(max = 100, message = "Branch name must be up to 100 characters")
    private String branchName;

    private String bankPassBookAsBase64;

}
