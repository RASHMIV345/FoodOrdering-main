package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

public class AdminDashboardPanel extends JPanel {

    private JLabel customersLabel;
    private JLabel foodItemsLabel;
    private JLabel ordersLabel;
    private JLabel pendingLabel;
    private JLabel deliveredLabel;
    private JLabel cancelledLabel;

    public AdminDashboardPanel() {

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(
                20, 20, 20, 20
        ));

        JLabel title = new JLabel("ADMIN DASHBOARD");
        title.setFont(new Font(
                "Segoe UI",
                Font.BOLD,
                24
        ));

        add(title, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(
                new GridLayout(2, 3, 15, 15)
        );

        customersLabel = new JLabel("0", SwingConstants.CENTER);
        foodItemsLabel = new JLabel("0", SwingConstants.CENTER);
        ordersLabel = new JLabel("0", SwingConstants.CENTER);
        pendingLabel = new JLabel("0", SwingConstants.CENTER);
        deliveredLabel = new JLabel("0", SwingConstants.CENTER);
        cancelledLabel = new JLabel("0", SwingConstants.CENTER);

        cardsPanel.add(
                createCard("Total Customers", customersLabel)
        );

        cardsPanel.add(
                createCard("Total Food Items", foodItemsLabel)
        );

        cardsPanel.add(
                createCard("Total Orders", ordersLabel)
        );

        cardsPanel.add(
                createCard("Pending Orders", pendingLabel)
        );

        cardsPanel.add(
                createCard("Delivered Orders", deliveredLabel)
        );

        cardsPanel.add(
                createCard("Cancelled Orders", cancelledLabel)
        );

        add(cardsPanel, BorderLayout.CENTER);

        JButton refreshButton =
                new JButton("Refresh Dashboard");

        refreshButton.addActionListener(e ->
                loadDashboard()
        );

        add(refreshButton, BorderLayout.SOUTH);

        loadDashboard();
    }

    private JPanel createCard(
            String title,
            JLabel valueLabel) {

        JPanel panel = new JPanel(
                new BorderLayout()
        );

        panel.setBorder(
                BorderFactory.createLineBorder(
                        Color.GRAY,
                        1
                )
        );

        JLabel titleLabel =
                new JLabel(
                        title,
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        valueLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        30
                )
        );

        panel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        panel.add(
                valueLabel,
                BorderLayout.CENTER
        );

        return panel;
    }

    private void loadDashboard() {

        try {

            String token =
                    System.getProperty("jwtToken");

            if (token == null || token.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Admin token not found.",
                        "Authentication Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            "http://localhost:8080/api/admin/dashboard"
                                    )
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

                JOptionPane.showMessageDialog(
                        this,
                        "Failed to load dashboard.\nHTTP Status: "
                                + response.statusCode(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            JSONObject json =
                    new JSONObject(response.body());

            customersLabel.setText(
                    String.valueOf(
                            json.getLong("totalCustomers")
                    )
            );

            foodItemsLabel.setText(
                    String.valueOf(
                            json.getLong("totalFoodItems")
                    )
            );

            ordersLabel.setText(
                    String.valueOf(
                            json.getLong("totalOrders")
                    )
            );

            pendingLabel.setText(
                    String.valueOf(
                            json.getLong("pendingOrders")
                    )
            );

            deliveredLabel.setText(
                    String.valueOf(
                            json.getLong("deliveredOrders")
                    )
            );

            cancelledLabel.setText(
                    String.valueOf(
                            json.getLong("cancelledOrders")
                    )
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to connect to server.\n"
                            + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
