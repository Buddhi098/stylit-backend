package com.stylit.online.model.courier;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courier_service_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
