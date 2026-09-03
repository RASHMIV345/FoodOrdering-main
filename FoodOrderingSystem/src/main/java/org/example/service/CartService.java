package org.example.service;

import org.example.model.Cart;
import org.example.model.CartItem;
import org.example.model.FoodItem;
import org.example.model.User;
import org.example.repository.CartItemRepository;
import org.example.repository.CartRepository;
import org.example.repository.FoodItemRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            FoodItemRepository foodItemRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
    }

    public Cart getOrCreateCart(int userId) {

        Optional<Cart> existingCart =
                cartRepository.findByUserId(userId);

        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Cart cart = new Cart(user);

        return cartRepository.save(cart);
    }

    public Cart addToCart(
            int userId,
            int foodId,
            int quantity) {

        if (quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        Cart cart = getOrCreateCart(userId);

        FoodItem foodItem = foodItemRepository
                .findById(foodId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Food item not found"
                        ));

        // Check if food already exists in cart
        for (CartItem item : cart.getItems()) {

            if (item.getFoodItem().getId() == foodId) {

                item.setQuantity(
                        item.getQuantity() + quantity
                );

                cartItemRepository.save(item);

                return cart;
            }
        }

        CartItem newItem =
                new CartItem(foodItem, quantity);

        cart.getItems().add(newItem);

        return cartRepository.save(cart);
    }

    public Cart updateQuantity(
            int userId,
            int foodId,
            int quantity) {

        if (quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        Cart cart = getOrCreateCart(userId);

        for (CartItem item : cart.getItems()) {

            if (item.getFoodItem().getId() == foodId) {

                item.setQuantity(quantity);

                cartRepository.save(cart);

                return cart;
            }
        }

        throw new RuntimeException(
                "Food item is not in the cart"
        );
    }

    public Cart removeFromCart(
            int userId,
            int foodId) {

        Cart cart = getOrCreateCart(userId);

        CartItem itemToRemove = null;

        for (CartItem item : cart.getItems()) {

            if (item.getFoodItem().getId() == foodId) {
                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove == null) {
            throw new RuntimeException(
                    "Food item is not in the cart"
            );
        }

        cart.getItems().remove(itemToRemove);

        return cartRepository.save(cart);
    }

    public void clearCart(int userId) {

        Cart cart = getOrCreateCart(userId);

        cart.getItems().clear();

        cartRepository.save(cart);
    }
}