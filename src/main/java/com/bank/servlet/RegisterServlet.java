package com.bank.servlet;

import com.bank.dao.UserDAO;
import com.bank.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        UserDAO userDAO = new UserDAO();

        // Check if username or email is already taken
        if (userDAO.isUsernameTaken(username)) {
            request.setAttribute("error", "Username is already taken");
            request.getRequestDispatcher("/user/register.jsp").forward(request, response);
            return;
        }

        if (userDAO.isEmailTaken(email)) {
            request.setAttribute("error", "Email is already registered");
            request.getRequestDispatcher("/user/register.jsp").forward(request, response);
            return;
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password); // In a real application, hash the password
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);

        // Register user
        if (userDAO.registerUser(user)) {
            // Redirect to login page with success message
            response.sendRedirect(request.getContextPath() + "/user/login.jsp?success=Registration successful! Please wait for admin approval.");
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/user/register.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/user/register.jsp").forward(request, response);
    }
}