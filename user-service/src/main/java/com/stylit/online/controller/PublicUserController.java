package com.stylit.online.controller;

import com.stylit.online.dto.courier.CourierDTO;
import com.stylit.online.dto.IsEmailExistDTO;
import com.stylit.online.dto.otp.OtpRequest;
import com.stylit.online.dto.otp.OtpVerifyRequest;
import com.stylit.online.dto.auth.LoginRequest;
import com.stylit.online.dto.auth.LogoutRequest;
import com.stylit.online.dto.shop.ShopDTO;
import com.stylit.online.dto.shopper.UserDTO;
import com.stylit.online.dto.shopper.UserRegisterDTO;
import com.stylit.online.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@RestController
@RequestMapping("public/user")
@RequiredArgsConstructor
@Validated
public class PublicUserController {

    @Autowired
    private final ShopperService shopperService;

    @Autowired
    private final AuthService authService;

    @Autowired
    private final ShopService shopService;

    @Autowired
    private final CourierService courierService;

    @Autowired
    private final OtpService otpService;

    @PostMapping("/addUser")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createShopper(@Valid @RequestBody UserRegisterDTO userRegisterDTO){
        return shopperService.saveUser(userRegisterDTO);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity login(@NotNull @RequestBody LoginRequest loginRequest){
        return authService.getAccessToken(loginRequest);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity logout(@NotNull @RequestBody LogoutRequest logoutRequest){
        Map response = authService.destroyLoginSession(logoutRequest.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addShop")
    public ResponseEntity addShop(@Valid @RequestBody ShopDTO shopDTO) {
        return shopService.addShop(shopDTO);
    }

    @PostMapping("/isExistCourier")
    public ResponseEntity isExistCourier(@Valid @RequestBody IsEmailExistDTO isEmailExistDTO){
        return courierService.isEmailExist(isEmailExistDTO);
    }

    @PostMapping("/isExistShop")
    public ResponseEntity isExistShop(@Valid @RequestBody IsEmailExistDTO isEmailExistDTO){
        return shopService.isEmailExist(isEmailExistDTO);
    }

    @PostMapping("/addCourier")
    public ResponseEntity createNewCourier(@Valid @RequestBody CourierDTO courierDTO) {
            return courierService.createCourier(courierDTO);
    }

    @PostMapping("/generateOtp")
    public ResponseEntity generateOtp(@Valid @RequestBody OtpRequest otpRequest) {
        return otpService.generateOtp(otpRequest);

    }

    @PostMapping("/verifyOtp")
    public ResponseEntity verifyOtp(@Valid @RequestBody OtpVerifyRequest otpVerifyRequest) {
        return otpService.verifyOtp(otpVerifyRequest);
    }
}
