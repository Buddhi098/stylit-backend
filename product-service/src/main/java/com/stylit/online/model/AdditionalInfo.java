package com.stylit.online.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdditionalInfo {
    @ElementCollection
    private List<String> occasions;
    private String season;
    private String ageGroup;

}

