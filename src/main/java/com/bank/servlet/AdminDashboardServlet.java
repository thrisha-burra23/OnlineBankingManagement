package com.bank.servlet;

import com.bank.dao.LoanDAO;
import com.bank.dao.UserDAO;
import com.bank.dao.BankAccountDAO;
import com.bank.dao.TransferDAO;
import com.bank.model.Loan;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private UserDAO userDAO;
    private BankAccountDAO accountDAO;
    private LoanDAO loanDAO;
    private TransferDAO transferDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        accountDAO = new BankAccountDAO();
        loanDAO = new LoanDAO();
        transferDAO = new TransferDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.jsp");
            return;
        }

        // Load all data for the dashboard
        request.setAttribute("users", userDAO.getAllUsers());
        request.setAttribute("accounts", accountDAO.getAllAccounts());
        request.setAttribute("loans", loanDAO.getAllLoans()); // Get all loans instead of just pending ones
        request.setAttribute("transfers", transferDAO.getAllTransfers());

        // Forward to the dashboard JSP
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
} 