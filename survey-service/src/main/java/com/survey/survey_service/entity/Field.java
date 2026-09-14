package com.survey.survey_service.entity;

import java.time.Instant;

import com.survey.survey_service.enums.DataType;
import jakarta.persistence.*;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fields")
@Getter
@Setter
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false)
    private DataType dataType;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="label", nullable = false)
    private String label;
    
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;
    
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "options_json")
    private String optionsJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
