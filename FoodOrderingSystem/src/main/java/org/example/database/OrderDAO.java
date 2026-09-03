package org.example.database;

import org.example.model.FoodItem;
import org.example.model.LegacyOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public boolean saveOrder(LegacyOrder order) {

        try (Connection conn = DatabaseConnection.getConnection()) {

            String insertOrderQuery =
                    "INSERT INTO orders (user_id, total_price) VALUES (?, ?)";

            PreparedStatement orderStmt =
                    conn.prepareStatement(
                            insertOrderQuery,
                            Statement.RETURN_GENERATED_KEYS
                    );

            orderStmt.setInt(1, order.getUserId());
            orderStmt.setDouble(2, order.getTotalPrice());

            orderStmt.executeUpdate();

            ResultSet rs = orderStmt.getGeneratedKeys();

            if (rs.next()) {

                int orderId = rs.getInt(1);

                saveOrderDetails(
                        orderId,
                        order.getFoodItems()
                );
            }

            return true;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }

    private void saveOrderDetails(
            int orderId,
            List<FoodItem> foodItems) {

        try (Connection conn =
                     DatabaseConnection.getConnection()) {

            String query =
                    "INSERT INTO order_details " +
                            "(order_id, food_item_id, quantity) " +
                            "VALUES (?, ?, ?)";

            PreparedStatement stmt =
                    conn.prepareStatement(query);

            for (FoodItem item : foodItems) {

                stmt.setInt(1, orderId);
                stmt.setInt(2, item.getId());
                stmt.setInt(3, 1);

                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public List<LegacyOrder> getOrdersByUserId(int userId) {

        List<LegacyOrder> orders =
                new ArrayList<>();

        try (Connection conn =
                     DatabaseConnection.getConnection()) {

            String query =
                    "SELECT * FROM orders WHERE user_id = ?";

            PreparedStatement pstmt =
                    conn.prepareStatement(query);

            pstmt.setInt(1, userId);

            ResultSet rs =
                    pstmt.executeQuery();

            while (rs.next()) {

                int orderId =
                        rs.getInt("id");

                double totalPrice =
                        rs.getDouble("total_price");

                List<FoodItem> foodItems =
                        getOrderDetails(orderId);

                orders.add(
                        new LegacyOrder(
                                orderId,
                                userId,
                                totalPrice,
                                foodItems
                        )
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return orders;
    }

    private List<FoodItem> getOrderDetails(
            int orderId) {

        List<FoodItem> foodItems =
                new ArrayList<>();

        try (Connection conn =
                     DatabaseConnection.getConnection()) {

            String query =
                    "SELECT fi.id, fi.name, fi.price " +
                            "FROM order_details od " +
                            "JOIN food_items fi " +
                            "ON od.food_item_id = fi.id " +
                            "WHERE od.order_id = ?";

            PreparedStatement pstmt =
                    conn.prepareStatement(query);

            pstmt.setInt(1, orderId);

            ResultSet rs =
                    pstmt.executeQuery();

            while (rs.next()) {

                foodItems.add(
                        new FoodItem(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getDouble("price")
                        )
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return foodItems;
    }

    public List<LegacyOrder> getAllOrders() {

        List<LegacyOrder> orders =
                new ArrayList<>();

        try (Connection conn =
                     DatabaseConnection.getConnection()) {

            String query =
                    "SELECT * FROM orders";

            Statement stmt =
                    conn.createStatement();

            ResultSet rs =
                    stmt.executeQuery(query);

            while (rs.next()) {

                int orderId =
                        rs.getInt("id");

                int userId =
                        rs.getInt("user_id");

                double totalPrice =
                        rs.getDouble("total_price");

                List<FoodItem> foodItems =
                        getOrderDetails(orderId);

                orders.add(
                        new LegacyOrder(
                                orderId,
                                userId,
                                totalPrice,
                                foodItems
                        )
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return orders;
    }
}