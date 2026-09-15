package com.survey.survey_service.repository;

import com.survey.survey_service.entity.FormFieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface FormFieldMappingRepository extends JpaRepository<FormFieldMapping, Long> {
    List<FormFieldMapping> findByFormId(Long formId);
    Page<FormFieldMapping> findByFormId(Long formId, Pageable pageable);

    @Query("SELECT f.fieldId FROM FormFieldMapping f WHERE f.formId IN :ids")
    List<Long> findByIdsIn(List<Long> ids);

    void deleteByFormId(Long formId);
}
