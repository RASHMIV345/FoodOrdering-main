package org.example.view;

import org.example.model.FoodItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class AdminPanel extends JFrame {

    private JTable foodTable;

    private static final String BASE_URL =
            "http://localhost:8080/api/foods";

    public AdminPanel() {

        setTitle("Admin Panel - Food Management");
        setSize(950, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            for (UIManager.LookAndFeelInfo info :
                    UIManager.getInstalledLookAndFeels()) {

                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(
                            info.getClassName()
                    );
                    break;
                }
            }

        } catch (Exception ignored) {
        }

        setLayout(new BorderLayout(10, 10));

        ((JComponent) getContentPane())
                .setBorder(
                        new EmptyBorder(
                                15, 15, 15, 15
                        )
                );

        // ---------------- HEADER ----------------

        JLabel headerLabel =
                new JLabel(
                        "Admin Panel - Food Management",
                        SwingConstants.CENTER
                );

        headerLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24
                )
        );

        headerLabel.setBorder(
                new EmptyBorder(
                        10, 0, 20, 0
                )
        );

        add(
                headerLabel,
                BorderLayout.NORTH
        );

        // ---------------- TABLE ----------------

        foodTable = new JTable() {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column) {

                return false;
            }
        };

        foodTable.setRowHeight(30);

        foodTable.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        16
                )
        );

        foodTable.setFillsViewportHeight(true);

        foodTable.setAutoCreateRowSorter(true);

        JTableHeader tableHeader =
                foodTable.getTableHeader();

        tableHeader.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        tableHeader.setBackground(
                new Color(32, 136, 203)
        );

        tableHeader.setForeground(Color.WHITE);

        tableHeader.setReorderingAllowed(false);

        JScrollPane scrollPane =
                new JScrollPane(foodTable);

        scrollPane.getViewport()
                .setBackground(Color.WHITE);

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // Load food from REST API
        updateFoodTable();

        // ---------------- BUTTONS ----------------

        JButton addFoodButton =
                new JButton("Add Food");

        JButton editFoodButton =
                new JButton("Edit Food");

        JButton deleteFoodButton =
                new JButton("Delete Food");

        JButton refreshButton =
                new JButton("Refresh");

        JButton viewOrdersButton =
                new JButton("View Orders");

        JButton logoutButton =
                new JButton("Logout");

        Font buttonFont =
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                );

        addFoodButton.setFont(buttonFont);
        editFoodButton.setFont(buttonFont);
        deleteFoodButton.setFont(buttonFont);
        refreshButton.setFont(buttonFont);
        viewOrdersButton.setFont(buttonFont);
        logoutButton.setFont(buttonFont);

        // ---------------- BUTTON ACTIONS ----------------

        addFoodButton.addActionListener(
                e -> addFood()
        );

        editFoodButton.addActionListener(
                e -> editFood()
        );

        deleteFoodButton.addActionListener(
                e -> deleteFood()
        );

        refreshButton.addActionListener(
                e -> updateFoodTable()
        );

        viewOrdersButton.addActionListener(
                e -> viewOrders()
        );

        logoutButton.addActionListener(e -> {

            dispose();

            new LoginScreen();
        });

        // ---------------- BUTTON PANEL ----------------

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                12,
                                10
                        )
                );

        buttonPanel.add(addFoodButton);
        buttonPanel.add(editFoodButton);
        buttonPanel.add(deleteFoodButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewOrdersButton);
        buttonPanel.add(logoutButton);

        add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        // ---------------- ROW STRIPING ----------------

        foodTable.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {

                    Color evenColor =
                            new Color(
                                    240,
                                    248,
                                    255
                            );

                    @Override
                    public Component
                    getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column) {

                        Component c =
                                super
                                        .getTableCellRendererComponent(
                                                table,
                                                value,
                                                isSelected,
                                                hasFocus,
                                                row,
                                                column
                                        );

                        if (!isSelected) {

                            c.setBackground(
                                    row % 2 == 0
                                            ? evenColor
                                            : Color.WHITE
                            );
                        }

                        return c;
                    }
                }
        );

        setVisible(true);
    }

    // =====================================================
    // GET ALL FOOD
    // =====================================================

    private void updateFoodTable() {

        try {

            String token =
                    System.getProperty("jwtToken");

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(BASE_URL)
                            )
                            .header(
                                    "Authorization",
                                    "Bearer " + token
                            )
                            .GET()
                            .build();

            HttpClient client =
                    HttpClient.newHttpClient();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {

                showError(
                        "Failed to load food items.\nHTTP Status: "
                                + response.statusCode()
                );

                return;
            }

            JSONArray foods =
                    new JSONArray(
                            response.body()
                    );

            String[] columns = {
                    "ID",
                    "Name",
                    "Price"
            };

            DefaultTableModel model =
                    new DefaultTableModel(
                            columns,
                            0
                    );

            for (int i = 0;
                 i < foods.length();
                 i++) {

                JSONObject food =
                        foods.getJSONObject(i);

                model.addRow(
                        new Object[]{
                                food.getInt("id"),
                                food.getString("name"),
                                String.format(
                                        "₹%.2f",
                                        food.getDouble("price")
                                )
                        }
                );
            }

            foodTable.setModel(model);

            foodTable
                    .getColumnModel()
                    .getColumn(0)
                    .setPreferredWidth(60);

            foodTable
                    .getColumnModel()
                    .getColumn(1)
                    .setPreferredWidth(450);

            foodTable
                    .getColumnModel()
                    .getColumn(2)
                    .setPreferredWidth(120);

        } catch (Exception e) {

            showError(
                    "Unable to load food items.\n"
                            + e.getMessage()
            );
        }
    }

    // =====================================================
    // ADD FOOD
    // =====================================================

    private void addFood() {

        String name =
                JOptionPane.showInputDialog(
                        this,
                        "Enter food name:"
                );

        if (name == null ||
                name.trim().isEmpty()) {

            return;
        }

        String priceInput =
                JOptionPane.showInputDialog(
                        this,
                        "Enter food price:"
                );

        if (priceInput == null ||
                priceInput.trim().isEmpty()) {

            return;
        }

        try {

            double price =
                    Double.parseDouble(
                            priceInput
                    );

            if (price <= 0) {

                showError(
                        "Price must be greater than 0."
                );

                return;
            }

            JSONObject food =
                    new JSONObject();

            food.put(
                    "name",
                    name.trim()
            );

            food.put(
                    "price",
                    price
            );

            String token =
                    System.getProperty("jwtToken");

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(BASE_URL)
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Authorization",
                                    "Bearer " + token
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(
                                                    food.toString()
                                            )
                            )
                            .build();

            HttpClient client =
                    HttpClient.newHttpClient();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() == 200 ||
                    response.statusCode() == 201) {

                JOptionPane.showMessageDialog(
                        this,
                        "Food item added successfully!"
                );

                updateFoodTable();

            } else {

                showError(
                        "Failed to add food.\nHTTP Status: "
                                + response.statusCode()
                );
            }

        } catch (NumberFormatException e) {

            showError(
                    "Please enter a valid price."
            );

        } catch (Exception e) {

            showError(
                    "Error adding food.\n"
                            + e.getMessage()
            );
        }
    }

    // =====================================================
    // EDIT FOOD
    // =====================================================

    private void editFood() {

        int selectedRow =
                foodTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a food item first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int modelRow =
                foodTable.convertRowIndexToModel(
                        selectedRow
                );

        int id =
                Integer.parseInt(
                        foodTable
                                .getModel()
                                .getValueAt(
                                        modelRow,
                                        0
                                )
                                .toString()
                );

        String currentName =
                foodTable
                        .getModel()
                        .getValueAt(
                                modelRow,
                                1
                        )
                        .toString();

        String currentPrice =
                foodTable
                        .getModel()
                        .getValueAt(
                                modelRow,
                                2
                        )
                        .toString()
                        .replace("₹", "");

        String name =
                JOptionPane.showInputDialog(
                        this,
                        "Enter new food name:",
                        currentName
                );

        if (name == null ||
                name.trim().isEmpty()) {

            return;
        }

        String priceInput =
                JOptionPane.showInputDialog(
                        this,
                        "Enter new price:",
                        currentPrice
                );

        if (priceInput == null ||
                priceInput.trim().isEmpty()) {

            return;
        }

        try {

            double price =
                    Double.parseDouble(
                            priceInput
                    );

            if (price <= 0) {

                showError(
                        "Price must be greater than 0."
                );

                return;
            }

            JSONObject food =
                    new JSONObject();

            food.put(
                    "name",
                    name.trim()
            );

            food.put(
                    "price",
                    price
            );

            String token =
                    System.getProperty("jwtToken");

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            BASE_URL
                                                    + "/"
                                                    + id
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Authorization",
                                    "Bearer " + token
                            )
                            .PUT(
                                    HttpRequest.BodyPublishers
                                            .ofString(
                                                    food.toString()
                                            )
                            )
                            .build();

            HttpClient client =
                    HttpClient.newHttpClient();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() == 200) {

                JOptionPane.showMessageDialog(
                        this,
                        "Food item updated successfully!"
                );

                updateFoodTable();

            } else {

                showError(
                        "Failed to update food.\nHTTP Status: "
                                + response.statusCode()
                );
            }

        } catch (Exception e) {

            showError(
                    "Error updating food.\n"
                            + e.getMessage()
            );
        }
    }

    // =====================================================
    // DELETE FOOD
    // =====================================================

    private void deleteFood() {

        int selectedRow =
                foodTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a food item first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int modelRow =
                foodTable.convertRowIndexToModel(
                        selectedRow
                );

        int id =
                Integer.parseInt(
                        foodTable
                                .getModel()
                                .getValueAt(
                                        modelRow,
                                        0
                                )
                                .toString()
                );

        String name =
                foodTable
                        .getModel()
                        .getValueAt(
                                modelRow,
                                1
                        )
                        .toString();

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete \"" + name + "\"?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmation !=
                JOptionPane.YES_OPTION) {

            return;
        }

        try {

            String token =
                    System.getProperty("jwtToken");

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            BASE_URL
                                                    + "/"
                                                    + id
                                    )
                            )
                            .header(
                                    "Authorization",
                                    "Bearer " + token
                            )
                            .DELETE()
                            .build();

            HttpClient client =
                    HttpClient.newHttpClient();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() == 204) {

                JOptionPane.showMessageDialog(
                        this,
                        "Food item deleted successfully!"
                );

                updateFoodTable();

            } else {

                showError(
                        "Failed to delete food.\nHTTP Status: "
                                + response.statusCode()
                );
            }

        } catch (Exception e) {

            showError(
                    "Error deleting food.\n"
                            + e.getMessage()
            );
        }
    }

    // =====================================================
    // VIEW ORDERS
    // =====================================================

    private void viewOrders() {

        JOptionPane.showMessageDialog(
                this,
                "Order management is available through:\n\n"
                        + "GET /api/orders/all\n\n"
                        + "Order status can be updated through:\n"
                        + "PUT /api/orders/{id}/status",
                "Order Management",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // ERROR MESSAGE
    // =====================================================

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}