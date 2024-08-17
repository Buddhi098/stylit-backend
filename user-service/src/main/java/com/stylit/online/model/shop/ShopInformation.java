package com.stylit.online.model.shop;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity()
@Table(name="shop_information")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @NotBlank(message = "Shop Description is required")
    private String shopDescription;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Category> categories;

    @NotBlank(message = "Facebook Link is required")
    private String facebookLink;

    @NotBlank(message = "Instagram Link is required")
    private String instagramLink;

    @NotBlank(message = "Storefront Image is required")
    private String logo;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
}

