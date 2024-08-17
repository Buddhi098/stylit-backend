package com.stylit.online.model.courier;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "courier_business_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierBusinessData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Business registration number is required")
    @Size(max = 50, message = "Business registration number must be up to 50 characters")
    private String businessRegNo;

    @PastOrPresent(message = "Business registration date must be in the past or present")
    private LocalDate businessRegDate;

    @NotBlank(message = "Business type is required")
    @Size(max = 100, message = "Business type must be up to 100 characters")
    private String businessType;

    @NotBlank(message = "Business email is required")
    @Email(message = "Business email should be valid")
    private String businessEmail;

    @NotBlank(message = "Business document is required")
    @Lob
    @Column(name = "business_document_as_base64", columnDefinition = "LONGTEXT")
    private String businessDocument;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
