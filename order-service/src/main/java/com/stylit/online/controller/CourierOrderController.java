package com.stylit.online.controller;

import com.stylit.online.repository.order.OrderItemRepository;
import com.stylit.online.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("courier/order")
@RequiredArgsConstructor
@Validated
public class CourierOrderController {

    @Autowired
    private final OrderService orderService;

    @GetMapping("getAllOrderByCourierId/{courierId}")
    public ResponseEntity<?> getAllOrderByCourierId(@PathVariable Long courierId){
        return orderService.getAllOrderByCourierId(courierId);
    }

    @GetMapping("changeCourierStatus/{orderItemId}/{status}")
    public ResponseEntity<?> changeCourierStatus(@PathVariable Long orderItemId , @PathVariable String status){
        return orderService.changeCourierStatus(orderItemId , status);
    }
}
