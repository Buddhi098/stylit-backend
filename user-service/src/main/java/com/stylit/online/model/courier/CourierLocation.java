package com.stylit.online.model.courier;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "courier_location")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Address Line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "Province is required")
    private String province;

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Latitude is required")
    private Float latitude;

    @NotNull(message = "Longitude is required")
    private Float longitude;

    @NotBlank(message = "Postal Code is required")
    private String postalCode;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
