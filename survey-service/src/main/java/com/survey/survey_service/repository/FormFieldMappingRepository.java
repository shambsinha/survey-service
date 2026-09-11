package com.survey.survey_service.repository;

import com.survey.survey_service.entity.FormFieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormFieldMappingRepository extends JpaRepository<FormFieldMapping, Long> {
    List<FormFieldMapping> findByFormId(Long formId);

    @Query("SELECT f.fieldId FROM FormFieldMapping f WHERE f.formId IN :ids")
    List<Long> findByIdsIn(List<Long> ids);

}
