package com.stylit.online.controller;

import com.stylit.online.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("shop/order")
@RequiredArgsConstructor
@Validated
public class ShopOrderController {

    @Autowired
    private final OrderService orderService;

    @GetMapping("getOrderByShopId/{shopId}")
    public ResponseEntity<?> getOrderByShopId(@PathVariable Long shopId){
        return  orderService.getOrderByShopId(shopId);
    }

    @GetMapping("getOrderDetailsByOrderId/{orderId}")
    public ResponseEntity<?> getOrderByOrderId(@PathVariable Long orderId){
        return orderService.getOrderDetailsByOrderId(orderId);
    }

    @GetMapping("changeStatusByOrderItemId/{orderItemId}/{status}")
    public ResponseEntity<?> changeStatusByOrderItemId(@PathVariable Long orderItemId , @PathVariable String status){
        return orderService.changeStatusByOrderItemId(orderItemId , status);
    }

    @GetMapping("selectCourierForOrder/{orderItemId}/{courierId}/{courierName}")
    public ResponseEntity<?> selectCourierForOrder(@PathVariable Long orderItemId , @PathVariable Long courierId , @PathVariable String courierName){
        return orderService.selectCourierForOrder(orderItemId , courierId , courierName);
    }

}
