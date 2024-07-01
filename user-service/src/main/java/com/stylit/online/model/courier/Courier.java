package com.stylit.online.model.courier;

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

@Entity
@Table(name ="courier")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Courier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String profilePhotoPath;

    @NotBlank(message = "Email is required")
    @Email(message = "provide valid email address")
    @Column(name="email" , unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8)
    private String password;

    @NotBlank(message = "Courier service name is required")
    @Size(max = 100, message = "Courier service name must be up to 100 characters")
    private String courierServiceName;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private CourierAddress address;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private ContactPersonDetails contactPersonDetails;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private ServiceDetails serviceDetails;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private CourierPaymentDetails courierPaymentDetails;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private BusinessDetails businessDetails;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
