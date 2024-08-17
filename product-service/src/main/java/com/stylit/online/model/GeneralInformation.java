package com.stylit.online.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneralInformation {
    private String sku;
    private String productName;
    private String gender;
    private String category;
    private String subcategory;
    private String brand;
    private String description;
}

