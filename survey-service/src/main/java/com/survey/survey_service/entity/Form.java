package com.survey.survey_service.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "forms")
@Getter
@Setter
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
}
