package org.example.database;

import org.example.model.FoodItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodItemDAO {
    public List<FoodItem> getAllFoodItems() {
        List<FoodItem> foodItems = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM food_items";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                foodItems.add(new FoodItem(rs.getInt("id"), rs.getString("name"), rs.getDouble("price")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foodItems;
    }

    public void addFoodItem(FoodItem foodItem) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO food_items (name, price) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, foodItem.getName());
            pstmt.setDouble(2, foodItem.getPrice());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
}
}
}