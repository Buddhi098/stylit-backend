package com.stylit.online.dto.courier;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDetailsDTO {

    @ElementCollection
    @CollectionTable(name = "service_coverage_province", joinColumns = @JoinColumn(name = "service_details_id"))
    @Column(name = "coverage_province")
    private Set<String> coverageProvince = new HashSet<>();

    @NotBlank(message = "Average delivery time is required")
    @Size(max = 50, message = "Average delivery time must be up to 50 characters")
    private String averageDeliveryTime;

    @NotBlank(message = "Maximum parcel size is required")
    @Size(max = 50, message = "Maximum parcel size must be up to 50 characters")
    private String maximumParcelSize;

    @NotBlank(message = "Pricing information is required")
    private String pricing;

}
