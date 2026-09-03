package org.example.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import org.example.model.FoodItem;

public class OrderSummaryPanel extends JFrame {
    public OrderSummaryPanel(List<FoodItem> foodItems) {
        setTitle("Order Summary");
        setSize(600, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        if (foodItems == null || foodItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items in the order.");
            dispose();
            return;
        }

        // Use a panel with border layout
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPanel);

        // Build order summary text
        StringBuilder orderDetails = new StringBuilder();
        orderDetails.append("Order Summary:\n\n");

        double total = 0;
        for (FoodItem item : foodItems) {
            orderDetails.append(String.format("%-30s $%.2f\n", item.getName(), item.getPrice()));
            total += item.getPrice();
        }

        // Text area to show order summary
        JTextArea orderSummaryArea = new JTextArea(orderDetails.toString());
        orderSummaryArea.setEditable(false);
        orderSummaryArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        orderSummaryArea.setLineWrap(true);
        orderSummaryArea.setWrapStyleWord(true);
        orderSummaryArea.setBackground(new Color(245, 245, 245));
        orderSummaryArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(orderSummaryArea);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Total label (bold and bigger font)
        JLabel totalLabel = new JLabel(String.format("Total: $%.2f", total));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalLabel.setBorder(new EmptyBorder(10, 0, 10, 10));
        contentPanel.add(totalLabel, BorderLayout.SOUTH);

        // Close button panel
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(closeButton);
        contentPanel.add(buttonPanel, BorderLayout.PAGE_END);

        setVisible(true);
    }
}
