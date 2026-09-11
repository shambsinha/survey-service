package com.survey.survey_service.service;

import com.survey.survey_service.dto.*;
import java.util.List;

public interface FormService {

    FormResponse createForm(FormCreateRequest request);

    FormResponse getForm(Long formId);
    void submitForm(Long formId, FormSubmissionRequest request, Long uId);


    List<FieldAnswersResponse> getFormResults(Long formId);

    boolean validateField(String dataTypeName, String value);


}
