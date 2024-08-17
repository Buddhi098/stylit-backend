package com.stylit.online.model.shop;

import com.stylit.online.model.courier.Courier;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity()
@Table(name="shop")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @NotBlank(message = "Shop Name is required")
    private String shopName;

    @NotBlank(message = "Shop Email is required")
    @Email(message = "Invalid email format")
    private String shopEmail;

    @NotBlank(message = "Shop Contact Number is required")
    private String shopContactNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    private ShopLocation shopLocation;

    @Enumerated(EnumType.STRING)
    private Shop.Status status;

    @OneToOne(cascade = CascadeType.ALL)
    private ShopBusinessData shopBusinessData;

    @OneToOne(cascade = CascadeType.ALL)
    private ShopInformation shopInformation;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Status{
        pending , active , reject , disable , remove
    }

}

