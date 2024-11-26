package com.stylit.online.service;
import com.stylit.online.ApiResponse.ApiErrorResponse;
import com.stylit.online.ApiResponse.ApiSuccessResponse;
import com.stylit.online.dto.RequestCreateDTO;
import com.stylit.online.dto.RequestUpdateDTO;
import com.stylit.online.model.Request;
import com.stylit.online.model.courier.Courier;
import com.stylit.online.model.shop.Shop;
import com.stylit.online.repository.RequestRepo;
import com.stylit.online.repository.courier.CourierRepo;
import com.stylit.online.repository.shop.ShopRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepo requestRepo;

    @Autowired
    private final ShopRepo shopRepo;

    @Autowired
    private final CourierRepo courierRepo;


    @Transactional
    public ResponseEntity createRequest(RequestCreateDTO requestCreateDTO) {
        try {
            Request request = Request.builder()
                    .senderId(Long.valueOf(requestCreateDTO.getSenderId()))
                    .senderRole(requestCreateDTO.getSenderRole())
                    .receiverRole(requestCreateDTO.getReceiverRole())
                    .receiverId(Long.valueOf(requestCreateDTO.getReceiverId()))
                    .status(Request.RequestStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            Request response = requestRepo.save(request);
            Map<String , Object> data = new HashMap<>();
            data.put("request" , response);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiSuccessResponse("Request Created" , data));
        } catch (Exception e) {
            Map<String , Object> error = new HashMap<>();
            error.put("error" , e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Request doesn't created" , error));
        }
    }

    @Transactional
    public ResponseEntity updateRequestStatus(Long requestId, RequestUpdateDTO requestUpdateDTO) {
        try {
            Optional<Request> optionalRequest = requestRepo.findById(requestId);
            if (optionalRequest.isEmpty()) {
                throw new RuntimeException("Request not found with ID: " + requestId);
            }

            Request request = optionalRequest.get();
            if (request.getStatus() != Request.RequestStatus.PENDING) {
                throw new RuntimeException("Request already processed.");
            }

            Request.RequestStatus status;
            try {
                status = requestUpdateDTO.getStatus();
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status. Allowed values: ACCEPTED, REJECTED");
            }

            request.setStatus(status);
            request.setUpdatedAt(LocalDateTime.now());

            Map<String , Object> data = new HashMap<>();
            data.put("data" , request);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (RuntimeException e) {
            Map<String , Object> error = new HashMap<>();
            error.put("error" , e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    public ResponseEntity getRequestsSentByUser(Long senderId) {
        try {
            List<Request> sentRequests = requestRepo.findBySenderId(senderId);
            List<Map<String, Object>> data =  enrichRequestsWithUserDetails(sentRequests);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching sent requests for user ID: " + senderId, e);
        }
    }

    public ResponseEntity getRequestsReceivedByUser(Long receiverId) {
        try {
            List<Request> receivedRequests = requestRepo.findByReceiverId(receiverId);
            List<Map<String, Object>> data =  enrichRequestsWithUserDetails(receivedRequests);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching received requests for user ID: " + receiverId, e);
        }
    }

    private List<Map<String, Object>> enrichRequestsWithUserDetails(List<Request> requests) {
        return requests.stream()
                .map(request -> {
                    // Initialize sender and receiver as Optional
                    Optional<Shop> senderShop = Optional.empty();
                    Optional<Courier> senderCourier = Optional.empty();
                    Optional<Shop> receiverShop = Optional.empty();
                    Optional<Courier> receiverCourier = Optional.empty();

                    // Determine sender and receiver based on the roles
                    if (Objects.equals(request.getSenderRole(), "shop")) {
                        senderShop = shopRepo.findById(request.getSenderId());  // Sender is a Shop
                        receiverCourier = courierRepo.findById(request.getReceiverId());  // Receiver is a Courier
                    } else if (Objects.equals(request.getSenderRole(), "courier")) {
                        senderCourier = courierRepo.findById(request.getSenderId());  // Sender is a Courier
                        receiverShop = shopRepo.findById(request.getReceiverId());  // Receiver is a Shop
                    }

                    // Create a map to store the enriched data
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", request.getId());
                    data.put("status", request.getStatus());
                    data.put("date", request.getCreatedAt());

                    // Add sender details
                    if (senderShop.isPresent()) {
                        data.put("senderId", request.getSenderId());
                        data.put("senderName", senderShop.get().getShopName());
                        data.put("senderLocation", senderShop.get().getShopLocation());
                    } else if (senderCourier.isPresent()) {
                        data.put("senderId", request.getSenderId());
                        data.put("senderName", senderCourier.get().getCourierName());
                        data.put("senderLocation", senderCourier.get().getCourierLocation());
                    }

                    // Add receiver details
                    if (receiverShop.isPresent()) {
                        data.put("receiverId", request.getReceiverId());
                        data.put("receiverName", receiverShop.get().getShopName());
                        data.put("receiverLocation", receiverShop.get().getShopLocation());
                    } else if (receiverCourier.isPresent()) {
                        data.put("receiverId", request.getReceiverId());
                        data.put("receiverName", receiverCourier.get().getCourierName());
                        data.put("receiverLocation", receiverCourier.get().getCourierLocation());
                    }

                    return data;
                })
                .collect(Collectors.toList());
    }

}
