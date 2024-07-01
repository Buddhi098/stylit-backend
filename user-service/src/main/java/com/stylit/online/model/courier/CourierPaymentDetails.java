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

@Entity
@Table(name = "courier_payment_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierPaymentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @NotBlank(message = "Bank passbook image is required")
    @Lob
    @Column(name = "business_document_as_base64", columnDefinition = "LONGTEXT")
    private String bankPassBookAsBase64;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
