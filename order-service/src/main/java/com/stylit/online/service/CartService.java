package com.stylit.online.service;

import com.stylit.online.dto.CartDTO;
import com.stylit.online.dto.CartItemDTO;
import com.stylit.online.model.Cart;
import com.stylit.online.model.CartItem;
import com.stylit.online.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    @Autowired
    private final CartRepository cartRepository;

    public ResponseEntity<?> addItemToCart(CartDTO cartDTO) {
        try {
            // Convert CartDTO items to CartItem entities
            List<CartItem> cartItemsList = cartDTO.getCartItemsDto().stream()
                    .map(dto -> CartItem.builder()
                            .productId(dto.getProductId())
                            .variantId(dto.getVariantId())
                            .productName(dto.getProductName())
                            .color(dto.getColor())
                            .size(dto.getSize())
                            .price(dto.getPrice())
                            .quantity(dto.getQuantity())
                            .build())
                    .collect(Collectors.toList());

            // Check if a cart already exists for the given user ID
            Optional<Cart> existingCart = cartRepository.findByUserId(cartDTO.getUserId());
            Cart cart;

            if (existingCart.isPresent()) {
                // If the cart exists, update its cart items by adding new items
                cart = existingCart.get();
                List<CartItem> cartItems = cart.getCartItems();
                cartItems.addAll(cartItemsList); // Add all new items to the existing list
                cart.setCartItems(cartItems);
            } else {
                // If the cart does not exist, create a new cart
                cart = Cart.builder()
                        .userId(cartDTO.getUserId())
                        .cartItems(cartItemsList)
                        .build();
            }

            // Save the cart (either updated or new)
            cart = cartRepository.save(cart);

            return ResponseEntity.status(HttpStatus.CREATED).body(cart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }



    public ResponseEntity<?> getCart(Long shopperId) {
        try {
            Cart cart = cartRepository.findByUserId(shopperId)
                    .orElseThrow(() -> new RuntimeException("Cart not found for this user"));

            return ResponseEntity.status(HttpStatus.OK).body(cart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    public ResponseEntity<String> isInCart(Long productId, Long variantId, Long shopperId) {
        try {
            // Find the cart for the given shopperId
            Optional<Cart> userCartOptional = cartRepository.findByUserId(shopperId);

            // Check if the cart exists
            if (userCartOptional.isPresent()) {
                Cart userCart = userCartOptional.get();
                List<CartItem> cartItems = userCart.getCartItems();

                // Check if any cart item matches the given productId and variantId
                boolean itemExists = cartItems.stream().anyMatch(item ->
                        item.getProductId().equals(productId) && item.getVariantId().equals(variantId)
                );

                if (itemExists) {
                    return ResponseEntity.ok("yes");
                } else {
                    return ResponseEntity.ok("Item is not in the cart.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found for the given user.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    public ResponseEntity<String> removeItemFromCart(Long productId, Long variantId, Long userId) {
        try {
            // Find the cart for the given userId
            Optional<Cart> userCartOptional = cartRepository.findByUserId(userId);

            // Check if the cart exists
            if (userCartOptional.isPresent()) {
                Cart userCart = userCartOptional.get();
                // Get the list of cart items
                List<CartItem> cartItems = userCart.getCartItems();

                // Find the matching item and remove it
                boolean itemRemoved = cartItems.removeIf(item -> item.getProductId().equals(productId) && item.getVariantId().equals(variantId));

                if (itemRemoved) {
                    // Save the updated cart
                    cartRepository.save(userCart);
                    return ResponseEntity.ok("Item removed successfully.");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item not found in the cart.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cart not found for the given user.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    public ResponseEntity<String> incrementCartItemQuantity(Long cartItemId, Long shopperId) {
        try {
            // Find the cart for the given shopperId
            Optional<Cart> userCartOptional = cartRepository.findByUserId(shopperId);

            if (userCartOptional.isPresent()) {
                Cart userCart = userCartOptional.get();
                // Find the specific CartItem in the cart by cartItemId
                Optional<CartItem> cartItemOptional = userCart.getCartItems().stream()
                        .filter(item -> item.getId().equals(cartItemId))
                        .findFirst();

                if (cartItemOptional.isPresent()) {
                    CartItem cartItem = cartItemOptional.get();
                    // Increment the quantity by 1
                    cartItem.setQuantity(cartItem.getQuantity() + 1);
                    cartRepository.save(userCart);
                    return ResponseEntity.ok("CartItem quantity incremented successfully.");
                } else {
                    return ResponseEntity.status(404).body("CartItem not found.");
                }
            } else {
                return ResponseEntity.status(404).body("Cart not found for the given user.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Decrement the quantity of a specific CartItem by 1 using cartItemId
    public ResponseEntity<String> decrementCartItemQuantity(Long cartItemId, Long shopperId) {
        try {
            // Find the cart for the given shopperId
            Optional<Cart> userCartOptional = cartRepository.findByUserId(shopperId);

            if (userCartOptional.isPresent()) {
                Cart userCart = userCartOptional.get();
                // Find the specific CartItem in the cart by cartItemId
                Optional<CartItem> cartItemOptional = userCart.getCartItems().stream()
                        .filter(item -> item.getId().equals(cartItemId))
                        .findFirst();

                if (cartItemOptional.isPresent()) {
                    CartItem cartItem = cartItemOptional.get();
                    // Decrement the quantity by 1, ensuring it doesn't go below 1
                    if (cartItem.getQuantity() > 1) {
                        cartItem.setQuantity(cartItem.getQuantity() - 1);
                        cartRepository.save(userCart);
                        return ResponseEntity.ok("CartItem quantity decremented successfully.");
                    } else {
                        return ResponseEntity.status(400).body("Quantity cannot be less than 1.");
                    }
                } else {
                    return ResponseEntity.status(404).body("CartItem not found.");
                }
            } else {
                return ResponseEntity.status(404).body("Cart not found for the given user.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    public boolean deleteCartItem(Long cartItemId, Long userId) {
        Optional<Cart> userCartOptional = cartRepository.findByUserId(userId);

        if (userCartOptional.isPresent()) {
            Cart userCart = userCartOptional.get();
            // Find and remove the CartItem by its ID
            userCart.getCartItems().removeIf(cartItem -> cartItem.getId().equals(cartItemId));

            // Save the updated cart back to the database
            cartRepository.save(userCart);
            return true;
        }
        return false; // Cart not found or item not found
    }

}
