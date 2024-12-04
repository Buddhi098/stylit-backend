package com.stylit.online.service;

import com.stylit.online.dto.order.OrderDTO;
import com.stylit.online.dto.order.OrderItemDTO;
import com.stylit.online.dto.order.ShippingAddressDTO;
import com.stylit.online.model.order.Order;
import com.stylit.online.model.order.OrderItem;
import com.stylit.online.model.order.ShippingAddress;
import com.stylit.online.repository.order.OrderItemRepository;
import com.stylit.online.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final OrderItemRepository orderItemRepository;

    public ResponseEntity<?> createOrder(OrderDTO orderDTO){
        try{

            List<OrderItemDTO> orderItemDTO = orderDTO.getOrderItems();

            List<OrderItem> orderItems = orderItemDTO.stream().map(orderItem -> {
                return OrderItem.builder()
                        .price(orderItem.getPrice())
                        .shopId(orderItem.getShopId())
                        .status(orderItem.getStatus())
                        .productId(orderItem.getProductId())
                        .variantId(orderItem.getVariantId())
                        .productName(orderItem.getProductName())
                        .color(orderItem.getColor())
                        .size(orderItem.getSize())
                        .quantity(orderItem.getQuantity())
                        .build();
            }).toList();

            ShippingAddressDTO shippingAddressDTO = orderDTO.getShippingAddress();

            ShippingAddress shippingAddress = ShippingAddress.builder()
                    .firstName(orderDTO.getShippingAddress().getFirstName())
                    .lastName(orderDTO.getShippingAddress().getLastName())
                    .addressLine1(orderDTO.getShippingAddress().getAddressLine1())
                    .addressLine2(orderDTO.getShippingAddress().getAddressLine2())
                    .zipCode(orderDTO.getShippingAddress().getZipCode())
                    .province(orderDTO.getShippingAddress().getProvince())
                    .city(orderDTO.getShippingAddress().getCity())
                    .country(orderDTO.getShippingAddress().getCountry())
                    .build();

            Order order = Order.builder()
                    .orderItems(orderItems)
                    .userId(orderDTO.getUserId())
                    .shippingAddress(shippingAddress)
                    .totalCost(orderDTO.getTotalCost())
                    .build();

            Order orderResponse = orderRepository.save(order);

            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);

        }catch (Exception e){
            Map<String , Object> response = new HashMap<>();

            response.put("error" , e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    public ResponseEntity<?> getAllOrdersByShopperId(Long shopperId) {
        try {
            // Fetch orders from the repository
            List<Order> orders = orderRepository.findByUserId(shopperId);

            // Check if orders exist for the given shopperId
            if (orders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No orders found for the given shop ID: " + shopperId);
            }

            // Return the orders in the response
            return ResponseEntity.ok(orders);

        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching orders: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getOrderByShopId(Long shopId){
        try{
            List<OrderItem> orderItems = orderItemRepository.findByShopId(shopId);

            Map<String, Object> response = new HashMap<>();
            response.put("data" , orderItems);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        }catch (Exception e){
            Map<String , Object> response = new HashMap<>();

            response.put("error" , e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    public ResponseEntity<?> getOrderDetailsByOrderId(Long orderID){
        try{
            Optional<Order> order= orderRepository.findById(orderID);

            return ResponseEntity.status(HttpStatus.OK).body(order);

        }catch(Exception e){
            Map<String , Object> response = new HashMap<>();

            response.put("error" , e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    public ResponseEntity<?> changeStatusByOrderItemId(Long id, String status) {
        try {
            Optional<OrderItem> orderItemOptional = orderItemRepository.findById(id);

            if (orderItemOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("OrderItem with ID " + id + " not found");
            }

            OrderItem orderItem = orderItemOptional.get();
            orderItem.setStatus(status);
            OrderItem updatedOrderItem = orderItemRepository.save(orderItem);

            return ResponseEntity.status(HttpStatus.OK).body(updatedOrderItem);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    public ResponseEntity<?> selectCourierForOrder(Long orderItemId, Long courierId, String courierName) {
        try {
            Optional<OrderItem> orderItemOptional = orderItemRepository.findById(orderItemId);

            if (orderItemOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("OrderItem with ID " + orderItemId + " not found");
            }

            OrderItem orderItem = orderItemOptional.get();
            orderItem.setCourierId(courierId);
            orderItem.setCourierName(courierName);
            orderItem.setCourierStatus("pending");

            OrderItem updatedOrderItem = orderItemRepository.save(orderItem);

            return ResponseEntity.status(HttpStatus.OK).body(updatedOrderItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    public ResponseEntity getAllOrderByCourierId(Long courierId){

        try{
            List<OrderItem> orderItem = orderItemRepository.findByCourierId(courierId);
            return ResponseEntity.status(HttpStatus.OK).body(orderItem);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public ResponseEntity<?> changeCourierStatus(Long orderItemId, String status) {
        try {
            Optional<OrderItem> orderItemOptional = orderItemRepository.findById(orderItemId);

            if (orderItemOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("OrderItem with ID " + orderItemId + " not found");
            }

            OrderItem orderItem = orderItemOptional.get();
            orderItem.setCourierStatus(status);

            OrderItem updatedOrderItem = orderItemRepository.save(orderItem);

            return ResponseEntity.status(HttpStatus.OK).body(updatedOrderItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }





}
