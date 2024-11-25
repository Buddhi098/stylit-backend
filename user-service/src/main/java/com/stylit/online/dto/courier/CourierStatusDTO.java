package com.stylit.online.dto.courier;

import com.stylit.online.model.courier.Courier;
import lombok.Data;

@Data
public class CourierStatusDTO {
    private Long id;
    private Courier.Status status;
}
