package com.stylit.online.dto.shop;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopDTO {

    @NotBlank(message = "Shop name is mandatory")
    private String shopName;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid contact number")
    private String contactNumber;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min=8 , message = "Password Must be Greater than 8 character")
    private String password;

    @Valid
    private ShopAddressDTO shopAddress;
    @Valid
    private OwnerDTO owner;
    @Valid
    private BusinessInformationDTO businessInformation;
    @Valid
    private PaymentDetailsDTO paymentDetails;
    @Valid
    private StoreFrontInformationDTO storefrontInformation;
    @Valid
    private Set<ClothCategoryDTO> clothCategories = new HashSet<>();

}
