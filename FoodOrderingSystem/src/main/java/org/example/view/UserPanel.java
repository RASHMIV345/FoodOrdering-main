package org.example.view;

import org.example.controller.OrderController;
import org.example.database.FoodItemDAO;
import org.example.model.FoodItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UserPanel extends JFrame {
    private List<FoodItem> cart = new ArrayList<>();
    private JLabel totalLabel = new JLabel("Total: $0.00");
    private JTable cartTable;

    public UserPanel(int userId) {
        setTitle("User Panel");
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the frame

        // Set Nimbus look and feel for modern UI
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // fallback
        }

        setLayout(new BorderLayout(15, 15));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header Label
        JLabel headerLabel = new JLabel("User Panel - Place Your Order", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Cart Table setup
        String[] columnNames = {"Food Item", "Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable cells
            }
        };
        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(28);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cartTable.setFillsViewportHeight(true);
        cartTable.setShowGrid(false);
        cartTable.setIntercellSpacing(new Dimension(0, 0));
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader tableHeader = cartTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableHeader.setBackground(new Color(32, 136, 203));
        tableHeader.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(cartTable);
        add(scrollPane, BorderLayout.CENTER);

        // Total label styling
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalLabel.setBorder(new EmptyBorder(10, 0, 10, 20));
        add(totalLabel, BorderLayout.EAST);

        // Buttons with layout and styling
        JButton viewMenuButton = new JButton("View Menu");
        JButton checkoutButton = new JButton("Checkout");
        JButton viewOrdersButton = new JButton("View Order Details");
        JButton logoutButton = new JButton("Logout");

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 16);
        Dimension buttonSize = new Dimension(160, 40);

        viewMenuButton.setFont(buttonFont);
        viewMenuButton.setPreferredSize(buttonSize);

        checkoutButton.setFont(buttonFont);
        checkoutButton.setPreferredSize(buttonSize);

        viewOrdersButton.setFont(buttonFont);
        viewOrdersButton.setPreferredSize(buttonSize);

        logoutButton.setFont(buttonFont);
        logoutButton.setPreferredSize(buttonSize);

        // Button actions
        viewMenuButton.addActionListener(e -> displayMenu());
        checkoutButton.addActionListener(e -> checkout(userId));
        viewOrdersButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Orders are now available through the REST API:\n\n" +
                            "GET /api/orders",
                    "My Orders",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });

        // Button panel with spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        buttonPanel.add(viewMenuButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(viewOrdersButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void displayMenu() {
        List<FoodItem> menu = new FoodItemDAO().getAllFoodItems();

        String[] menuItems = new String[menu.size()];
        for (int i = 0; i < menu.size(); i++) {
            FoodItem item = menu.get(i);
            menuItems[i] = item.getName() + " - $" + String.format("%.2f", item.getPrice());
        }

        String selectedItem = (String) JOptionPane.showInputDialog(
                this,
                "Select a food item:",
                "Menu",
                JOptionPane.PLAIN_MESSAGE,
                null,
                menuItems,
                menuItems.length > 0 ? menuItems[0] : null
        );

        if (selectedItem != null) {
            for (FoodItem item : menu) {
                if (selectedItem.startsWith(item.getName())) {
                    cart.add(item);
                    updateCartTable();
                    updateTotal();
                    break;
                }
            }
        }
    }

    private void updateCartTable() {
        DefaultTableModel model = (DefaultTableModel) cartTable.getModel();
        model.setRowCount(0);  // Clear existing rows

        for (FoodItem item : cart) {
            model.addRow(new Object[]{item.getName(), String.format("$%.2f", item.getPrice())});
        }
    }

    private void updateTotal() {
        double total = cart.stream().mapToDouble(FoodItem::getPrice).sum();
        totalLabel.setText("Total: $" + String.format("%.2f", total));
    }

    private void checkout(int userId) {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to place the order?",
                "Confirm Checkout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            new OrderSummaryPanel(cart);

            cart.clear();
            updateCartTable();
            updateTotal();

            JOptionPane.showMessageDialog(
                    this,
                    "Order summary generated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}