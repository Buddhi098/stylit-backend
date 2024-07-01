package com.stylit.online.controller;

import com.stylit.online.dto.courier.CourierDTO;
import com.stylit.online.dto.shop.ShopDTO;
import com.stylit.online.dto.auth.LoginRequest;
import com.stylit.online.dto.auth.LogoutRequest;
import com.stylit.online.dto.shopper.UserRequest;
import com.stylit.online.service.AuthService;
import com.stylit.online.service.CourierService;
import com.stylit.online.service.ShopService;
import com.stylit.online.service.ShopperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
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

    @Value("${storage.pdfs.shop.business-document}")
    private String businessDocPath;

    @Value("${storage.images.shop.profile-photo}")
    private String profilePhotoPath;

    @Value("${storage.images.shop.cover-photo}")
    private String coverPhotoPath;

    @Value("${storage.images.courier.profile-photo}")
    private String courierProfilePath;

    @PostMapping("/addUser")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createShopper(@Valid @RequestBody UserRequest userRequest){
        return shopperService.saveUser(userRequest);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity login(@NotNull @RequestBody LoginRequest loginRequest){
        Map token = authService.getAccessToken(loginRequest.getUserName() , loginRequest.getPassword() , String.valueOf(loginRequest.getUserRole()));
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity logout(@NotNull @RequestBody LogoutRequest logoutRequest){
        Map response = authService.destroyLoginSession(logoutRequest.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addShop")
    public ResponseEntity addShop(@Valid @ModelAttribute ShopDTO shopDTO ,
                                  @RequestParam("businessInformation.businessDocument") MultipartFile businessDocument,
                                  @RequestParam("paymentDetails.bankPassBookImage") MultipartFile bankPassBookImage,
                                  @RequestParam("storefrontInformation.logo") MultipartFile logo,
                                  @RequestParam("storefrontInformation.bannerImage") MultipartFile bannerImage) throws IOException {

            String email = shopDTO.getEmail();
            String fileName = email.split("@")[0];

            String businessDocPathResponse = shopService.saveFile(businessDocument , fileName , businessDocPath);
            String logoPath = shopService.saveFile(logo , fileName , profilePhotoPath);
            String bannerImagePath = shopService.saveFile(bannerImage , fileName , coverPhotoPath);

            byte[] bankPassBookAsByte = bankPassBookImage.getBytes();
            String bankPassBookAsBase64 = Base64.getEncoder().encodeToString(bankPassBookAsByte);

            shopDTO.getBusinessInformation().setBusinessDocumentPath(businessDocPathResponse);
            shopDTO.getPaymentDetails().setBankPassBookImageBase64("bankPassBookAsBase64");
            shopDTO.getStorefrontInformation().setLogoPath(logoPath);
            shopDTO.getStorefrontInformation().setBannerImagePath(bannerImagePath);

            return shopService.addShop(shopDTO);

    }

    @PostMapping("/addCourier")
    public ResponseEntity createNewCourier(@Valid @ModelAttribute CourierDTO courierDTO,
                                           @RequestParam("profile.logo") MultipartFile logo,
                                           @RequestParam("business.certificate") MultipartFile certificate,
                                           @RequestParam("payment.bankPassBook") MultipartFile bankPassBook) throws IOException {

            String email = courierDTO.getEmail();

            String profilePathResponse = courierService.saveFile(logo , email , profilePhotoPath);

            byte[] certificateAsByte = certificate.getBytes();
            String certificateAsBase64String = Base64.getEncoder().encodeToString(certificateAsByte);

            byte[] bankPassBookByte = bankPassBook.getBytes();
            String bankPassBookBase64String = Base64.getEncoder().encodeToString(bankPassBookByte);

            courierDTO.setProfilePhotoPath(profilePathResponse);
            courierDTO.getBusinessDetails().setBusinessDocumentAsBase64(certificateAsBase64String);
            courierDTO.getCourierPaymentDetails().setBankPassBookAsBase64(bankPassBookBase64String);

            return courierService.createCourier(courierDTO);

    }
}
