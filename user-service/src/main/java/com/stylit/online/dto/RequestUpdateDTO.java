package com.stylit.online.dto;

import com.stylit.online.model.Request;
import lombok.Data;

@Data
public class RequestUpdateDTO {

    private Request.RequestStatus status; // ACCEPTED or REJECTED
    private Long requestId;

}
