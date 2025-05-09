package com.bank.servlet;

import com.bank.dao.UserDAO;
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

@WebServlet("/admin/activateUser")
public class ActivateUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(ActivateUserServlet.class);
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in and is an admin
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.jsp");
            return;
        }

        String userIdStr = request.getParameter("userId");
        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            try {
                int userId = Integer.parseInt(userIdStr);
                
                // Get user details before activation
                User user = userDAO.getUserById(userId);
                if (user == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=User not found");
                    return;
                }
                
                // Store previous status
                String previousStatus = user.getStatus();
                
                if (userDAO.activateUser(userId)) {
                    // If the user was just activated (status changed to active)
                    if ("active".equals(user.getStatus())) {
                        try {
                            // Send activation email to the user
                            EmailUtil.sendAccountApprovalEmail(user.getEmail(), user.getFullName());
                            logger.info("Account activation email sent to user: {}", user.getEmail());
                        } catch (Exception e) {
                            logger.error("Failed to send activation email to user: {}", user.getEmail(), e);
                            // Continue with the success message even if email fails
                        }
                    }
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard?message=User activated successfully");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=Failed to activate user");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=Invalid user ID");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=No user ID provided");
        }
    }
} 