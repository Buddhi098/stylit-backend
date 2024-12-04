package com.stylit.online.controller;

import com.stylit.online.dto.CartDTO;
import com.stylit.online.dto.order.OrderDTO;
import com.stylit.online.service.CartService;
import com.stylit.online.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("shopper/order")
@RequiredArgsConstructor
@Validated
public class ShopperOrderController {

    @Autowired
    private final CartService cartService;

    @Autowired
    private final OrderService orderService;

    @PostMapping("addItemToCart")
    public ResponseEntity<?> addItemToCart(@RequestBody CartDTO cartDTO){
        return cartService.addItemToCart(cartDTO);
    }

    @GetMapping("getCart/{shopperId}")
    public ResponseEntity getCart(@PathVariable Long shopperId){
        return cartService.getCart(shopperId);
    }

    @GetMapping("isInCart/{productId}/{variantId}/{shopperId}")
    public ResponseEntity isInCart(@PathVariable Long productId , @PathVariable Long variantId , @PathVariable Long shopperId){
        return cartService.isInCart(productId ,variantId ,  shopperId);
    }

    @GetMapping("removeItemFromCart/{productId}/{variantId}/{shopperId}")
    public ResponseEntity removeItemFromCart(@PathVariable Long productId , @PathVariable Long variantId , @PathVariable Long shopperId){
        return cartService.removeItemFromCart(productId , variantId , shopperId);
    }

    @GetMapping("increment/{cartItemId}/{shopperId}")
    public ResponseEntity<String> incrementCartItemQuantity(
            @PathVariable Long cartItemId,
            @PathVariable Long shopperId) {
        return cartService.incrementCartItemQuantity(cartItemId, shopperId);
    }

    @GetMapping("decrement/{cartItemId}/{shopperId}")
    public ResponseEntity<String> decrementCartItemQuantity(
            @PathVariable Long cartItemId,
            @PathVariable Long shopperId) {
        return cartService.decrementCartItemQuantity(cartItemId, shopperId);
    }

    @DeleteMapping("/items/{cartItemId}/{userId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Long cartItemId, @PathVariable Long userId) {
        boolean isDeleted = cartService.deleteCartItem(cartItemId, userId);

        if (isDeleted) {
            return ResponseEntity.ok("Cart item deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Cart or item not found.");
        }
    }

    @PostMapping("createOrder")
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO){
        return orderService.createOrder(orderDTO);
    }

    @GetMapping("getAllOrdersByShopperId/{shopperId}")
    public ResponseEntity<?> getAllOrdersByShopperId(@PathVariable Long shopperId){
        return orderService.getAllOrdersByShopperId(shopperId);
    }


}
