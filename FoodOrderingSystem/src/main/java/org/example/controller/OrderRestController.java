package org.example.controller;

import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.service.OrderService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {
    private final UserService userService;
    private final OrderService orderService;

    public OrderRestController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    // =========================
    // CUSTOMER - PLACE ORDER
    // =========================

    @PostMapping
    public ResponseEntity<Order> placeOrder(
            Authentication authentication) {

        String username = authentication.getName();

        Order order = orderService.placeOrder(username);

        return ResponseEntity.ok(order);
    }

    // =========================
    // CUSTOMER - MY ORDERS
    // =========================

    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                orderService.getOrdersByUsername(username)
        );
    }

    // =========================
    // GET SINGLE ORDER
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(
            @PathVariable int id) {

        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }

    // =========================
    // ADMIN - ALL ORDERS
    // =========================

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    // =========================
    // ADMIN - UPDATE STATUS
    // =========================

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable int id,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        id,
                        status
                )
        );
    }
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable int id,
            Authentication authentication) {

        String username = authentication.getName();

        int userId = userService.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"))
                .getId();

        return ResponseEntity.ok(
                orderService.cancelOrder(id, userId)
        );
    }
}