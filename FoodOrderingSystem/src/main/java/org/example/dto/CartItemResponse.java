package org.example.dto;

public class CartItemResponse {

    private int foodId;
    private String foodName;
    private double price;
    private int quantity;
    private double subtotal;

    public CartItemResponse() {
    }

    public CartItemResponse(
            int foodId,
            String foodName,
            double price,
            int quantity) {

        this.foodId = foodId;
        this.foodName = foodName;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = price * quantity;
    }

    public int getFoodId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }
}