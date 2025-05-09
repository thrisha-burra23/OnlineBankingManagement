package com.bank.servlet;

import com.bank.util.DatabaseMigration;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/update-database")
public class DatabaseUpdateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Update the loan table
        DatabaseMigration.updateLoanTable();
        
        // Redirect back to admin dashboard
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
} 