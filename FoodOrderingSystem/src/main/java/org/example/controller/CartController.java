package org.example.controller;

import org.example.model.Cart;
import org.example.service.CartService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.example.dto.CartItemResponse;
import org.example.dto.CartResponse;
import org.example.model.CartItem;

import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/cart")
public class CartController {
    private CartResponse toCartResponse(Cart cart) {

        var items = cart.getItems()
                .stream()
                .map(item -> new CartItemResponse(
                        item.getFoodItem().getId(),
                        item.getFoodItem().getName(),
                        item.getFoodItem().getPrice(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        return new CartResponse(
                cart.getId(),
                items
        );
    }
    private final CartService cartService;
    private final UserService userService;

    public CartController(
            CartService cartService,
            UserService userService) {

        this.cartService = cartService;
        this.userService = userService;
    }

    private int getLoggedInUserId(Authentication authentication) {

        String username = authentication.getName();

        return userService.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"))
                .getId();
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication) {

        int userId = getLoggedInUserId(authentication);

        Cart cart = cartService.getOrCreateCart(userId);

        return ResponseEntity.ok(toCartResponse(cart));
    }


    @PostMapping("/add/{foodId}")
    public ResponseEntity<Cart> addToCart(
            @PathVariable int foodId,
            @RequestParam(defaultValue = "1") int quantity,
            Authentication authentication) {

        int userId = getLoggedInUserId(authentication);

        return ResponseEntity.ok(
                cartService.addToCart(
                        userId,
                        foodId,
                        quantity
                )
        );
    }

    @PutMapping("/update/{foodId}")
    public ResponseEntity<Cart> updateQuantity(
            @PathVariable int foodId,
            @RequestParam int quantity,
            Authentication authentication) {

        int userId = getLoggedInUserId(authentication);

        return ResponseEntity.ok(
                cartService.updateQuantity(
                        userId,
                        foodId,
                        quantity
                )
        );
    }

    @DeleteMapping("/remove/{foodId}")
    public ResponseEntity<Cart> removeFromCart(
            @PathVariable int foodId,
            Authentication authentication) {

        int userId = getLoggedInUserId(authentication);

        return ResponseEntity.ok(
                cartService.removeFromCart(
                        userId,
                        foodId
                )
        );
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(
            Authentication authentication) {

        int userId = getLoggedInUserId(authentication);

        cartService.clearCart(userId);

        return ResponseEntity.noContent().build();
    }
}