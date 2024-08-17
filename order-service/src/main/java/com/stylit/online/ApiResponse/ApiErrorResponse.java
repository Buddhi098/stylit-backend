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
    private Map<String,Object> errors;

    public ApiErrorResponse(String validationFailed, Map<String, Object> errors) {
        this.message = validationFailed;
        this.errors = errors;
    }
}
