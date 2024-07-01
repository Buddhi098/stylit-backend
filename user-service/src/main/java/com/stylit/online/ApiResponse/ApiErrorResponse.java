package com.stylit.online.ApiResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private String status = "fail";
    private String message;
    private Map<String,String> errors;

    public ApiErrorResponse(String validationFailed, Map<String, String> errors) {
        this.message = validationFailed;
        this.errors = errors;
    }
}
