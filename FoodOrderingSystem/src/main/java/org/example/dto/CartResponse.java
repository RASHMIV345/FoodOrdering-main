package org.example.dto;

import java.util.List;

public class CartResponse {

    private int cartId;
    private List<CartItemResponse> items;
    private double total;

    public CartResponse() {
    }

    public CartResponse(
            int cartId,
            List<CartItemResponse> items) {

        this.cartId = cartId;
        this.items = items;

        this.total = items.stream()
                .mapToDouble(CartItemResponse::getSubtotal)
                .sum();
    }

    public int getCartId() {
        return cartId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }
}