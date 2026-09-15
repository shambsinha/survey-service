package com.survey.survey_service.repository;

import com.survey.survey_service.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.Instant;
import org.springframework.data.repository.query.Param;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {



    @Query("SELECT f.id FROM Form f WHERE f.createdAt < :thirtyDaysAgo AND f.id NOT IN (SELECT v.formId FROM FormValue v)")
    List<Long> findUnansweredFormIdsCreatedBefore(@Param("thirtyDaysAgo") Instant thirtyDaysAgo);

    List<Form> findByIsTemplateTrue();
}
