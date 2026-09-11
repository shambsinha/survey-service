package com.survey.survey_service.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "data_types")
@Getter
@Setter
public class DataType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
}
