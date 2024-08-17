package com.stylit.online.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "size_quantity_charts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SizeQuantityChart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String size;
    private Integer quantity;
}
