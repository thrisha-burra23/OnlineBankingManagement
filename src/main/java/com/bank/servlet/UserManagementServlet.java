package com.bank.servlet;

import com.bank.dao.UserDAO;
import com.bank.model.Admin;
import com.bank.model.User;
import com.bank.util.EmailUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class UserManagementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(UserManagementServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("admin");

        if (admin == null) {
            response.sendRedirect("../admin/login.jsp");
            return;
        }

        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAllUsers();

        request.setAttribute("users", users);
        request.getRequestDispatcher("users.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("admin");

        if (admin == null) {
            response.sendRedirect("../admin/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        int userId = Integer.parseInt(request.getParameter("userId"));
        String status = request.getParameter("status");

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserById(userId);
        String previousStatus = user.getStatus();
        
        boolean success = userDAO.updateUserStatus(userId, status);

        if (success) {
            // If the user was just approved (status changed from pending to active)
            if ("active".equals(status) && "pending".equals(previousStatus)) {
                try {
                    // Send approval email to the user
                    EmailUtil.sendAccountApprovalEmail(user.getEmail(), user.getFullName());
                    logger.info("Account approval email sent to user: {}", user.getEmail());
                } catch (Exception e) {
                    logger.error("Failed to send approval email to user: {}", user.getEmail(), e);
                    // Continue with the success message even if email fails
                }
            }
            request.setAttribute("message", "User status updated successfully");
        } else {
            request.setAttribute("error", "Failed to update user status");
        }

        // Refresh the user list
        List<User> users = userDAO.getAllUsers();
        request.setAttribute("users", users);
        request.getRequestDispatcher("users.jsp").forward(request, response);
    }
}