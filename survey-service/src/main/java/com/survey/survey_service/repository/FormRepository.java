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



    @Query("SELECT f.id FROM Form f WHERE f.createdAt < :cutoffDate")
    List<Long> findFormIdsCreatedBefore(@Param("cutoffDate") Instant cutoffDate);

}
