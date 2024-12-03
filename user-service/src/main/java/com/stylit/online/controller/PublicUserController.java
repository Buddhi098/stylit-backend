package com.stylit.online.controller;

import com.stylit.online.ApiResponse.ApiErrorResponse;
import com.stylit.online.dto.RequestCreateDTO;
import com.stylit.online.dto.RequestUpdateDTO;
import com.stylit.online.dto.auth.RefreshToken;
import com.stylit.online.dto.courier.CourierDTO;
import com.stylit.online.dto.IsEmailExistDTO;
import com.stylit.online.dto.otp.OtpRequest;
import com.stylit.online.dto.otp.OtpVerifyRequest;
import com.stylit.online.dto.auth.LoginRequest;
import com.stylit.online.dto.auth.LogoutRequest;
import com.stylit.online.dto.shop.ShopDTO;
import com.stylit.online.dto.shopper.UserDTO;
import com.stylit.online.dto.shopper.UserRegisterDTO;
import com.stylit.online.model.Request;
import com.stylit.online.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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

    @Autowired final RequestService requestService;

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

    @PostMapping("/getAccessToken")
    public ResponseEntity getAccessToken(@Valid @RequestBody RefreshToken refreshToken) {
        try{
            Map response = authService.getAccessTokenUsingRefreshToken(refreshToken);
            if(response.containsKey("error")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            Map<String , Object> response = new HashMap<>();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse());
        }
    }

    @GetMapping("/getShopsNameById/{id}")
    public ResponseEntity getShopsNameById(@PathVariable Long id){
        return shopService.getShopNameById(id);
    }

    @GetMapping("/getAllshop")
    public ResponseEntity getAllShops(){
        return shopService.getAllShops();
    }

    @GetMapping("/getAllcourier")
    public ResponseEntity getAllCourier(){
        return courierService.getAllCourier();
    }

    @PostMapping("createRequest")
    public ResponseEntity<?> createRequest(@Valid @RequestBody RequestCreateDTO requestCreateDTO) {
            return requestService.createRequest(requestCreateDTO);

    }

    @PutMapping("/updateRequestStatus/{id}")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody RequestUpdateDTO requestUpdateDTO) {
            return requestService.updateRequestStatus(id, requestUpdateDTO);
    }

    @GetMapping("/getRequestSentByUser/{id}")
    public ResponseEntity getRequestSentByUser(@PathVariable Long id){
        return requestService.getRequestsSentByUser(id);
    }

    @GetMapping("/getRequestReceivedByUser/{id}")
    public ResponseEntity getRequestReceivedByUser(@PathVariable Long id){
        return requestService.getRequestsReceivedByUser(id);
    }

    @GetMapping("/logo")
    public ResponseEntity<Resource> getLogo(@RequestParam String path) {
        try {
            Path filePath = Paths.get(path).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }





}
