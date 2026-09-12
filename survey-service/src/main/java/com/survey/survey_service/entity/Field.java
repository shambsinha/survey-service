package com.survey.survey_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
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
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "data_type_id")
    private Long dataTypeId;
    @Column(name="name")
    private String name;
    @Column(name="label")
    private String label;
    
    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "options_json")
    private String optionsJson;
}
