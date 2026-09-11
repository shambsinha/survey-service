package com.survey.survey_service.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "form_values")
@Getter
@Setter
public class FormValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "submission_id")
    private Long submissionId;
    
    @Column(name = "form_id")
    private Long formId;
    
    @Column(name = "field_id")
    private Long fieldId;
    
    private String value;
}
