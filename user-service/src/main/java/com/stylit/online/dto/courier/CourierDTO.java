package com.stylit.online.dto.courier;

import com.stylit.online.model.courier.ServiceDetails;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourierDTO {

    @NotBlank(message = "Courier service name is required")
    @Size(max = 100, message = "Courier service name must be up to 100 characters")
    private String courierServiceName;

    private String profilePhotoPath;

    @NotBlank(message = "Email is required")
    @Email(message = "provide valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8)
    private String password;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private CourierAddressDTO address;

    @Valid
    private ServiceDetailsDTO serviceDetails;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private ContactPersonDetailsDTO contactPersonDetails;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private CourierPaymentDetailsDTO courierPaymentDetails;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private BusinessDetailsDTO businessDetails;
}
