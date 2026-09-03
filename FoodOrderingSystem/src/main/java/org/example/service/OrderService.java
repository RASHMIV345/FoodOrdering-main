package org.example.service;

import org.example.model.Cart;
import org.example.model.CartItem;
import org.example.model.Order;
import org.example.model.OrderItem;
import org.example.model.OrderStatus;
import org.example.model.User;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            CartService cartService) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    // =========================
    // PLACE ORDER
    // =========================

    @Transactional
    public Order placeOrder(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        int userId = user.getId();

        Cart cart = cartService.getOrCreateCart(userId);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException(
                    "Cannot place order. Cart is empty."
            );
        }

        double totalAmount = 0;

        Order order = new Order(user, 0);

        for (CartItem cartItem : cart.getItems()) {

            double itemPrice =
                    cartItem.getFoodItem().getPrice();

            int quantity =
                    cartItem.getQuantity();

            totalAmount += itemPrice * quantity;

            OrderItem orderItem =
                    new OrderItem(
                            order,
                            cartItem.getFoodItem(),
                            quantity,
                            itemPrice
                    );

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder =
                orderRepository.save(order);

        // Clear cart only after successful order
        cartService.clearCart(userId);

        return savedOrder;
    }

    // =========================
    // CUSTOMER ORDERS
    // =========================

    public List<Order> getOrdersByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return orderRepository
                .findByUserIdOrderByOrderDateDesc(user.getId());
    }

    // =========================
    // GET SINGLE ORDER
    // =========================

    public Order getOrderById(int orderId) {

        return orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));
    }

    // =========================
    // ADMIN - ALL ORDERS
    // =========================

    public List<Order> getAllOrders() {

        return orderRepository.findAll();
    }

    // =========================
    // ADMIN - UPDATE STATUS
    // =========================

    @Transactional
    public Order updateOrderStatus(
            int orderId,
            OrderStatus status) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        ));

        order.setStatus(status);

        return orderRepository.save(order);
    }
    @Transactional
    public Order cancelOrder(int orderId, int userId) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        // Make sure the customer owns this order
        if (order.getUser().getId() != userId) {
            throw new RuntimeException(
                    "You are not authorized to cancel this order"
            );
        }

        // Check current status
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException(
                    "Delivered orders cannot be cancelled"
            );
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException(
                    "Order is already cancelled"
            );
        }

        order.setStatus(OrderStatus.CANCELLED);

        return orderRepository.save(order);
    }
}