package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.FormField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormFieldRepository extends JpaRepository<FormField, Long> {
    
    @Query("SELECT ff FROM FormField ff WHERE ff.form.id = :formId ORDER BY ff.fieldOrder ASC")
    List<FormField> findByFormIdOrderByFieldOrder(@Param("formId") Long formId);
    
    @Query("SELECT ff FROM FormField ff WHERE ff.form.id = :formId AND ff.fieldRequired = true ORDER BY ff.fieldOrder ASC")
    List<FormField> findRequiredFieldsByFormId(@Param("formId") Long formId);
} 