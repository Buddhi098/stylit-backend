package com.stylit.online.service;

import com.stylit.online.ApiResponse.ApiErrorResponse;
import com.stylit.online.ApiResponse.ApiSuccessResponse;
import com.stylit.online.dto.otp.OtpRequest;
import com.stylit.online.model.otp.Otp;
import com.stylit.online.repository.otp.OtpRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Builder
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private static final int EXPIRATION_MINUTES = 5;

    public ResponseEntity generateOtp(OtpRequest otpRequest) {
        try{
            String otp = String.valueOf(new Random().nextInt(999999));
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

            Otp otpEntity = Otp.builder()
                    .email(otpRequest.getEmail())
                    .otp(otp)
                    .expirationTime(expirationTime)
                    .build();

            otpRepository.save(otpEntity);

            Map<String , String> data = new HashMap<>();
            data.put("otp" , otp);
            // Send OTP via email
            emailService.sendOtpMessage(otpRequest.getEmail(), "Your OTP Code", "Your OTP code is: " + otp);
            ApiSuccessResponse apiSuccessResponse = ApiSuccessResponse.builder()
                    .message("success")
                    .data(data)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(apiSuccessResponse);
        }catch(Exception e){
            Map<String , String> errors = new HashMap<>();
            errors.put("exception" , e.getMessage());
            ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                    .message("fail")
                    .errors(errors).build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
        }

    }

//    public boolean verifyOtp(String username, String otp) {
//        Otp otpEntity = otpRepository.findByUsername(username);
//        if (otpEntity != null && otpEntity.getOtp().equals(otp) && otpEntity.getExpirationTime().isAfter(LocalDateTime.now())) {
//            otpRepository.delete(otpEntity);
//            return true;
//        }
//        return false;
//    }
}
