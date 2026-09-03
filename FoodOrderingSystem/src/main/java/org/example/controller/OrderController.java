package org.example.controller;

import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Customer: place order from current cart
    @PostMapping
    public ResponseEntity<Order> placeOrder(
            org.springframework.security.core.Authentication authentication) {

        Order order = orderService.placeOrder(
                authentication.getName()
        );

        return ResponseEntity.ok(order);
    }

    // Customer: view their own orders
    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(
            org.springframework.security.core.Authentication authentication) {

        return ResponseEntity.ok(
                orderService.getOrdersByUsername(
                        authentication.getName()
                )
        );
    }

    // Customer/Admin: view a particular order
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(
            @PathVariable int id) {

        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }

    // Admin: view all orders
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    // Admin: update order status

}