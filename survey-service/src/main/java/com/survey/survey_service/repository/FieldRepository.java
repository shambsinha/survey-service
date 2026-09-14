package com.survey.survey_service.repository;

import com.survey.survey_service.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {
    List<Field> findByIsDefaultTrue();

    @Modifying
    @Query("DELETE FROM Field f WHERE f.id IN :ids AND f.isSystem = false")
    void deleteCustomFieldsByIds(@Param("ids") List<Long> ids);
}
