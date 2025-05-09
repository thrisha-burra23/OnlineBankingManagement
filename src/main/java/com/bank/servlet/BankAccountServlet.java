package com.bank.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.bank.dao.BankAccountDAO;

@WebServlet("/bank-account")
public class BankAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BankAccountDAO accountDAO;

    @Override
    public void init() throws ServletException {
        accountDAO = new BankAccountDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Forward to the bank account view page
        request.getRequestDispatcher("/WEB-INF/views/bank-account.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handle any POST requests for bank account operations
        String action = request.getParameter("action");

        if ("create".equals(action)) {
            // Handle account creation
            String accountType = request.getParameter("accountType");
            String initialDeposit = request.getParameter("initialDeposit");

            if (accountType == null || accountType.trim().isEmpty()) {
                request.setAttribute("error", "Please select an account type");
            } else if (initialDeposit == null || initialDeposit.trim().isEmpty()) {
                request.setAttribute("error", "Please enter initial deposit amount");
            } else {
                try {
                    double deposit = Double.parseDouble(initialDeposit);
                    // Add account creation logic here
                    request.setAttribute("message", "Account created successfully");
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Invalid deposit amount");
                }
            }
        }

        // Forward back to the bank account view
        doGet(request, response);
    }
}