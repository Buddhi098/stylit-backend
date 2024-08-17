package com.stylit.online.service;

import com.stylit.online.ApiResponse.ApiErrorResponse;
import com.stylit.online.ApiResponse.ApiSuccessResponse;
import com.stylit.online.dto.otp.OtpRequest;
import com.stylit.online.dto.otp.OtpVerifyRequest;
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
            Otp previousOtpWithSameEmail = otpRepository.findByEmail(otpRequest.getEmail());
            if (previousOtpWithSameEmail != null) {
                otpRepository.delete(previousOtpWithSameEmail);
            }

            String otp = String.format("%06d", new Random().nextInt(1000000));
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

            Otp otpEntity = Otp.builder()
                    .email(otpRequest.getEmail())
                    .otp(otp)
                    .expirationTime(expirationTime)
                    .build();

            otpRepository.save(otpEntity);

            Map<String , Object> data = new HashMap<>();
            data.put("otp" , "otp");
            // Send OTP via email
            emailService.sendOtpMessage(otpRequest.getEmail(), "Your OTP Code", "Your OTP code is: " + otp);
            ApiSuccessResponse apiSuccessResponse = ApiSuccessResponse.builder()
                    .status("success")
                    .message("Succefully Send OTP")
                    .data(data)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(apiSuccessResponse);
        }catch(Exception e){
            Map<String , Object> errors = new HashMap<>();
            errors.put("exception" , e.getMessage());
            ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                    .status("fail")
                    .message("OTP generation fail")
                    .errors(errors).build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
        }

    }

    public ResponseEntity verifyOtp(OtpVerifyRequest otpVerifyRequest) {
        try {
            String email = otpVerifyRequest.getEmail();
            String otp = otpVerifyRequest.getOtp();
            Otp otpEntity = otpRepository.findByEmail(email);
            ApiSuccessResponse apiSuccessResponse;

            if (otpEntity != null && otpEntity.getOtp().equals(otp) && otpEntity.getExpirationTime().isAfter(LocalDateTime.now())) {
                otpRepository.delete(otpEntity);
                Map<String , Object> data = new HashMap<>();
                data.put("otpStatus" , "true");
                apiSuccessResponse = ApiSuccessResponse.builder()
                        .status("success")
                        .message("OTP verified")
                        .data(data).build();
            }else {
                String message;
                if(otpEntity.getExpirationTime().isBefore(LocalDateTime.now())){
                    message = "OTP is Expired";
                }else{
                    message = "The OTP you entered is incorrect. Please try again.";
                }
                Map<String , Object> data = new HashMap<>();
                data.put("otpStatus" , false);
                apiSuccessResponse = ApiSuccessResponse.builder()
                        .status("success")
                        .message(message)
                        .data(data).build();

            }

            return ResponseEntity.status(HttpStatus.OK).body(apiSuccessResponse);

        }catch(Exception e){
            Map<String , Object> errors = new HashMap<>();
            errors.put("exception" , e.getMessage());
            ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                    .status("fail")
                    .message("Verification failed . something went Wrong!!!")
                    .errors(errors).build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
        }
    }
}
