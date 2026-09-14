package com.survey.survey_service.repository;

import com.survey.survey_service.entity.FormValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormValueRepository extends JpaRepository<FormValue, Long> {
    List<FormValue> findByFormIdAndFieldIdIn(Long formId, List<Long> fieldIds);

}
