package org.example.dto;

public class AdminDashboardResponse {

    private long totalCustomers;
    private long totalFoodItems;
    private long totalOrders;
    private long pendingOrders;
    private long deliveredOrders;
    private long cancelledOrders;

    public AdminDashboardResponse() {
    }

    public AdminDashboardResponse(
            long totalCustomers,
            long totalFoodItems,
            long totalOrders,
            long pendingOrders,
            long deliveredOrders,
            long cancelledOrders) {

        this.totalCustomers = totalCustomers;
        this.totalFoodItems = totalFoodItems;
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.deliveredOrders = deliveredOrders;
        this.cancelledOrders = cancelledOrders;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public long getTotalFoodItems() {
        return totalFoodItems;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public long getPendingOrders() {
        return pendingOrders;
    }

    public long getDeliveredOrders() {
        return deliveredOrders;
    }

    public long getCancelledOrders() {
        return cancelledOrders;
    }
}