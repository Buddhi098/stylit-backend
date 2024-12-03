package com.stylit.online.controller;

import com.stylit.online.dto.courier.CourierStatusDTO;
import com.stylit.online.dto.shop.ShopStatusDTO;
import com.stylit.online.service.AuthService;
import com.stylit.online.service.CourierService;
import com.stylit.online.service.ShopService;
import com.stylit.online.service.ShopperService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.stylit.online.service.UserStats;

import java.util.Map;


@RestController
@RequestMapping("admin/user")
@RequiredArgsConstructor
@Validated
public class AdminUserController {

    private final ShopService shopService;

    private  final CourierService courierService;

    private final ShopperService shopperService;

    @Autowired
    private UserStats userStats;

    @GetMapping("/userStats")
    public ResponseEntity getUserStatistics() {
        return userStats.getUserStatistics();
    }

    @PostMapping("/changeStatus")
    public ResponseEntity changeShopStatus(@RequestBody ShopStatusDTO shopStatusDTO){
        return shopService.updateShopStatus(shopStatusDTO.getId() , shopStatusDTO.getStatus());
    }

    @PostMapping("/changeCourierStatus")
    public ResponseEntity changeCourierStatus(@RequestBody CourierStatusDTO courierStatusDTO){
        return courierService.updateCourierStatus(courierStatusDTO.getId() , courierStatusDTO.getStatus());
    }

    @GetMapping("/getAllShopper")
    public ResponseEntity getAllshopper(){
        return shopperService.getAllShopper();
    }



}
