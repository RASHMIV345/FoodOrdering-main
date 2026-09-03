package org.example.service;

import org.example.dto.AdminDashboardResponse;
import org.example.model.OrderStatus;
import org.example.repository.FoodItemRepository;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final FoodItemRepository foodItemRepository;
    private final OrderRepository orderRepository;

    public AdminDashboardService(
            UserRepository userRepository,
            FoodItemRepository foodItemRepository,
            OrderRepository orderRepository) {

        this.userRepository = userRepository;
        this.foodItemRepository = foodItemRepository;
        this.orderRepository = orderRepository;
    }

    public AdminDashboardResponse getDashboard() {

        long totalCustomers =
                userRepository.countByAdminFalse();

        long totalFoodItems =
                foodItemRepository.count();

        long totalOrders =
                orderRepository.count();

        long pendingOrders =
                orderRepository.countByStatus(
                        OrderStatus.PLACED
                )
                        + orderRepository.countByStatus(
                        OrderStatus.CONFIRMED
                )
                        + orderRepository.countByStatus(
                        OrderStatus.PREPARING
                )
                        + orderRepository.countByStatus(
                        OrderStatus.OUT_FOR_DELIVERY
                );

        long deliveredOrders =
                orderRepository.countByStatus(
                        OrderStatus.DELIVERED
                );

        long cancelledOrders =
                orderRepository.countByStatus(
                        OrderStatus.CANCELLED
                );

        return new AdminDashboardResponse(
                totalCustomers,
                totalFoodItems,
                totalOrders,
                pendingOrders,
                deliveredOrders,
                cancelledOrders
        );
    }
}
