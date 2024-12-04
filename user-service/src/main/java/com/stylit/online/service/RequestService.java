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
    public ResponseEntity<?> createRequest(RequestCreateDTO requestCreateDTO) {
        try {
            // Check if a request with the given senderId and receiverId already exists
            Optional<Request> existingRequest = requestRepo.findBySenderIdAndReceiverId(
                    requestCreateDTO.getSenderId(),
                    requestCreateDTO.getReceiverId()
            );

            if (existingRequest.isPresent()) {
                // Update the status of the existing request to PENDING
                Request request = existingRequest.get();
                if (request.getStatus() != Request.RequestStatus.PENDING) {
                    request.setStatus(Request.RequestStatus.PENDING);
                    request.setUpdatedAt(LocalDateTime.now()); // Update the timestamp
                    requestRepo.save(request); // Save the updated request
                }

                Map<String, Object> data = new HashMap<>();
                data.put("request", request);
                return ResponseEntity.status(HttpStatus.OK).body(new ApiSuccessResponse("Request status updated to PENDING", data));
            } else {
                // Create a new request if it does not exist
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
                Map<String, Object> data = new HashMap<>();
                data.put("request", response);
                return ResponseEntity.status(HttpStatus.CREATED).body(new ApiSuccessResponse("Request Created", data));
            }
        } catch (Exception e) {
            // Handle exceptions gracefully
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Request not created", error));
        }
    }

    @Transactional
    public ResponseEntity<?> updateRequestStatusById(RequestUpdateDTO requestUpdateDTO) {
        try {
            // Fetch the request based on senderId and receiverId
            Optional<Request> optionalRequest = requestRepo.findById(requestUpdateDTO.getRequestId());

            if (optionalRequest.isEmpty()) {
                throw new RuntimeException("Request not found with requestId: " + requestUpdateDTO.getRequestId());
            }

            Request request = optionalRequest.get();

            // Update the request status
            try {
                Request.RequestStatus status = Request.RequestStatus.valueOf(String.valueOf(requestUpdateDTO.getStatus()));
                request.setStatus(status);
                request.setUpdatedAt(LocalDateTime.now());
                requestRepo.save(request); // Save changes to the database
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status. Allowed values: ACCEPTED, REJECTED");
            }
            // Prepare the response
            Map<String, Object> data = new HashMap<>();
            data.put("data", request);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (RuntimeException e) {
            // Handle errors gracefully
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

//    For Shops

    public ResponseEntity getAllConnectableAndConnectedCouriers(Long shopId) {
        try {
            List<Courier> allCouriers = courierRepo.findByStatus(Courier.Status.active);
            System.out.println("Number of couriers found: " + allCouriers.size());

            List<Map<String, Object>> responseList = new ArrayList<>();

            for (Courier courier : allCouriers) {
                Map<String, Object> courierData = new HashMap<>();
                Optional<Request> sendRequest = requestRepo.findBySenderIdAndReceiverId(shopId, courier.getId());
                Optional<Request> receivedAcceptedRequest = requestRepo.findBySenderIdAndReceiverIdAndStatus(courier.getId(), shopId, Request.RequestStatus.ACCEPTED);
                Optional<Request> receivedPendingRequest = requestRepo.findBySenderIdAndReceiverIdAndStatus(courier.getId() , shopId , Request.RequestStatus.PENDING);
                if(receivedPendingRequest.isPresent()){
                    continue;
                }
                courierData.put("courierId", courier.getId());
                courierData.put("courierName", courier.getCourierName());
                courierData.put("branches", courier.getCourierBusinessData().getAvailableLocations());

                if (sendRequest.isPresent()) {
                    Request request = sendRequest.get();
                    courierData.put("requestId", request.getId());
                    courierData.put("status", request.getStatus());
                    courierData.put("date", request.getUpdatedAt());
                } else if (receivedAcceptedRequest.isPresent()) {
                    Request request = receivedAcceptedRequest.get();
                    courierData.put("requestId", request.getId());
                    courierData.put("status", request.getStatus());
                    courierData.put("date", request.getUpdatedAt());
                } else {
                    courierData.put("requestId", null);
                    courierData.put("status", "None");
                    courierData.put("date", null);
                }

                responseList.add(courierData);
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseList);

        } catch (Exception e) {

            // Provide more informative error responses
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while processing the request.");
            errorResponse.put("details", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(errorResponse));
        }
    }

    public ResponseEntity getAllPendingReceiveRequestToShop(Long shopId) {
        try {
            // Fetch all pending requests where the shop is the receiver
            List<Request> pendingRequests = requestRepo.findByReceiverIdAndStatus(
                    shopId, Request.RequestStatus.PENDING);

            // Prepare the response data
            List<Map<String, Object>> responseData = pendingRequests.stream()
                    .map(request -> {
                        Map<String, Object> data = new HashMap<>();
                        try {
                            // Fetch courier details by sender ID
                            Courier courier = courierRepo.findById(request.getSenderId())
                                    .orElseThrow(() -> new IllegalArgumentException("Courier not found for ID: " + request.getSenderId()));

                            data.put("courierId", courier.getId());
                            data.put("courierName", courier.getCourierName());
                            data.put("branches", courier.getCourierBusinessData() != null
                                    ? courier.getCourierBusinessData().getAvailableLocations()
                                    : Collections.emptyList());
                            data.put("requestId", request.getId());
                            data.put("status", request.getStatus());
                            data.put("date", request.getUpdatedAt());
                        } catch (Exception e) {
                            // Log and return error details for this specific request
                            data.put("error", "Failed to fetch courier details: " + e.getMessage());
                        }
                        return data;
                    })
                    .collect(Collectors.toList());

            // Return the successful response
            return ResponseEntity.status(HttpStatus.OK).body(responseData);
        } catch (Exception e) {
            // Handle unexpected exceptions gracefully
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while processing the request.");
            errorResponse.put("details", e.getMessage());
            errorResponse.put("timestamp", new Date());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(errorResponse));
        }
    }


    //    For Courier

    public ResponseEntity getAllConnectableAndConnectedShop(Long courierId) {
        try {
            List<Shop> allShops = shopRepo.findByStatus(Shop.Status.active);
            System.out.println("Number of shops found: " + allShops.size());

            List<Map<String, Object>> responseList = new ArrayList<>();

            for (Shop shop : allShops) {
                Map<String, Object> shopData = new HashMap<>();
                Optional<Request> sendRequest = requestRepo.findBySenderIdAndReceiverId(courierId, shop.getId());
                Optional<Request> receivedAcceptedRequest = requestRepo.findBySenderIdAndReceiverIdAndStatus(shop.getId(), courierId, Request.RequestStatus.ACCEPTED);
                Optional<Request> receivedPendingRequest = requestRepo.findBySenderIdAndReceiverIdAndStatus(shop.getId() , courierId , Request.RequestStatus.PENDING);
                if(receivedPendingRequest.isPresent()){
                    continue;
                }
                shopData.put("shopId", shop.getId());
                shopData.put("shopName", shop.getShopName());

                if (sendRequest.isPresent()) {
                    Request request = sendRequest.get();
                    shopData.put("requestId", request.getId());
                    shopData.put("status", request.getStatus());
                    shopData.put("date", request.getUpdatedAt());
                } else if (receivedAcceptedRequest.isPresent()) {
                    Request request = receivedAcceptedRequest.get();
                    shopData.put("requestId", request.getId());
                    shopData.put("status", request.getStatus());
                    shopData.put("date", request.getUpdatedAt());
                } else {
                    shopData.put("requestId", null);
                    shopData.put("status", "None");
                    shopData.put("date", null);
                }

                responseList.add(shopData);
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseList);

        } catch (Exception e) {

            // Provide more informative error responses
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while processing the request.");
            errorResponse.put("details", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(errorResponse));
        }
    }

    public ResponseEntity getAllPendingReceiveRequestToCourier(Long courierId) {
        try {
            // Fetch all pending requests where the shop is the receiver
            List<Request> pendingRequests = requestRepo.findByReceiverIdAndStatus(
                    courierId, Request.RequestStatus.PENDING);

            // Prepare the response data
            List<Map<String, Object>> responseData = pendingRequests.stream()
                    .map(request -> {
                        Map<String, Object> data = new HashMap<>();
                        try {
                            // Fetch courier details by sender ID
                            Shop shop = shopRepo.findById(request.getSenderId())
                                    .orElseThrow(() -> new IllegalArgumentException("Courier not found for ID: " + request.getSenderId()));

                            data.put("shopId", shop.getId());
                            data.put("courierName", shop.getShopName());
                            data.put("requestId", request.getId());
                            data.put("status", request.getStatus());
                            data.put("date", request.getUpdatedAt());
                        } catch (Exception e) {
                            // Log and return error details for this specific request
                            data.put("error", "Failed to fetch shop details: " + e.getMessage());
                        }
                        return data;
                    })
                    .collect(Collectors.toList());

            // Return the successful response
            return ResponseEntity.status(HttpStatus.OK).body(responseData);
        } catch (Exception e) {
            // Handle unexpected exceptions gracefully
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while processing the request.");
            errorResponse.put("details", e.getMessage());
            errorResponse.put("timestamp", new Date());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(errorResponse));
        }
    }



}
