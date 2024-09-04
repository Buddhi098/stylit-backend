package com.stylit.online.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "variant_boxes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String colorVariant;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "variant_box_id")
    private List<SizeQuantityChart> sizeQuantityChart;

    private String status;

}

