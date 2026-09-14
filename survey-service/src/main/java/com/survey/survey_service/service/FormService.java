package com.survey.survey_service.service;

import com.survey.survey_service.dto.*;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FormService {

    FormResponse createForm(FormCreateRequest request);

    FormResponse getForm(Long formId);

    void submitForm(Long formId, FormSubmissionRequest request, Long uId);

    Page<FieldAnswersResponse> getFormResults(Long formId, Long requestingUserId, Pageable pageable);

    void deleteUnansweredForms();
}
