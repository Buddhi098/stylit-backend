package com.stylit.online.dto.shopper;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
public class PaymentMethodDTO {

    @NotEmpty(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{16}$", message = "Card number should be 16 digits")
    private String cardNumber;

    @NotEmpty(message = "Expiry date is required")
    private LocalDate expiry;

    @NotEmpty(message = "CVC is required")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVC should be 3 or 4 digits")
    private String cvc;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDTO user;
}