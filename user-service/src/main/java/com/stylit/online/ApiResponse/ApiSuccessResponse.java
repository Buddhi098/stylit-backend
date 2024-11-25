package com.stylit.online.ApiResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiSuccessResponse {
    private String status = "success";
    private String message;
    private Map<String ,Object> data;

    public ApiSuccessResponse(String message , Map<String ,Object> data){
        this.message = message;
        this.data = data;
    }

}

