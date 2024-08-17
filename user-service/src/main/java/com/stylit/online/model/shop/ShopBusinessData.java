package com.stylit.online.model.shop;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity()
@Table(name="shop_business_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopBusinessData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @NotBlank(message = "Business Registration Number is required")
    private String businessRegNo;

    @NotNull(message = "Business Registration Date is required")
    @Past(message = "Business Registration Date must be before today")
    private LocalDate businessRegDate; // Consider using LocalDate

    @NotBlank(message = "Business Type is required")
    private String businessType;

    @NotBlank(message = "Business Email is required")
    @Email(message = "Invalid email format")
    private String businessEmail;

    @NotBlank(message = "Business Document is required")
    @Lob
    @Column(name = "business_document_as_base64", columnDefinition = "LONGTEXT")
    private String businessDocument;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
}

