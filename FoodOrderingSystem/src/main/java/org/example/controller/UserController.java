package org.example.controller;


import org.example.database.UserDAO;

public class UserController {
    private static UserDAO userDAO = new UserDAO();

    public static String authenticateUser(String username, String password) {
        String role = userDAO.getUserRole(username, password);
        if (role == null) {
            return "invalid"; // Return "invalid" if the user is not found
        }
        return role; // Return "admin" or "customer"
    }

}
