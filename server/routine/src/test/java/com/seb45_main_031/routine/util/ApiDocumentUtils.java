package com.seb45_main_031.routine.util;

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

public interface ApiDocumentUtils {
    static OperationRequestPreprocessor getRequestPreprocessor(){
        return preprocessRequest(prettyPrint());
    }
    static OperationResponsePreprocessor getResponsePreprocessor(){
        return preprocessResponse(prettyPrint());
    }
}
