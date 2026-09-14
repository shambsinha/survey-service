package com.survey.survey_service.entity;

import jakarta.persistence.*;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "form_values")
@Getter
@Setter
public class FormValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "submitted_by", nullable = false)
    private Long submittedBy;
    
    @Column(name = "form_id", nullable = false)
    private Long formId;
    
    @Column(name = "field_id", nullable = false)
    private Long fieldId;
    
    @Column(name = "value")
    private String value;

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
