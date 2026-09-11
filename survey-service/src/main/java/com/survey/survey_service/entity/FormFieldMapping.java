package com.survey.survey_service.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "form_fields_mapping")
@Getter
@Setter
public class FormFieldMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "form_id")
    private Long formId;
    @Column(name = "field_id")
    private Long fieldId;
    @Column(name = "is_required")
    private Boolean isRequired;
    @Column(name = "display_order")
    private Integer displayOrder;

}
