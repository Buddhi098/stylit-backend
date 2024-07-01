package com.stylit.online.model.shop;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shop_business_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Business type is required")
    private String businessType;

    @NotBlank(message = "Bussiness Document is required")
    private String businessDocumentPath;

    @NotBlank(message = "Tax identification number is required")
    private String taxIdentificationNumber;

    private String operatingHours;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
