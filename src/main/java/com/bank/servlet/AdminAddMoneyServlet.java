package com.bank.servlet;

import com.bank.dao.BankAccountDAO;
import com.bank.dao.TransferDAO;
import com.bank.model.Transfer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/admin/addMoney")
public class AdminAddMoneyServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminAddMoneyServlet.class);
    private BankAccountDAO accountDAO;
    private TransferDAO transferDAO;

    @Override
    public void init() throws ServletException {
        accountDAO = new BankAccountDAO();
        transferDAO = new TransferDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.jsp");
            return;
        }

        try {
            int accountId = Integer.parseInt(request.getParameter("accountId"));
            double amount = Double.parseDouble(request.getParameter("amount"));

            if (amount <= 0) {
                request.setAttribute("error", "Amount must be greater than zero");
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            }

            // Start transaction
            Connection conn = null;
            try {
                conn = com.bank.util.DatabaseUtil.getConnection();
                conn.setAutoCommit(false);

                // Update account balance
                boolean success = accountDAO.updateAccountBalance(accountId, amount);
                if (success) {
                    // Create transfer record for the deposit
                    Transfer transfer = new Transfer();
                    transfer.setFromAccountId(0); // 0 represents bank's internal account
                    transfer.setToAccountId(accountId);
                    transfer.setTransferType("DEPOSIT");
                    transfer.setAmount(amount);
                    transfer.setStatus("COMPLETED");
                    transfer.setCreatedAt(LocalDateTime.now());

                    boolean transferSuccess = transferDAO.createTransfer(transfer);
                    if (transferSuccess) {
                        conn.commit();
                        logger.info("Successfully added {} to account {} and recorded transfer", amount, accountId);
                        request.setAttribute("message", "Successfully added $" + amount + " to the account");
                    } else {
                        conn.rollback();
                        logger.error("Failed to record transfer for account {}", accountId);
                        request.setAttribute("error", "Failed to record the transfer");
                    }
                } else {
                    conn.rollback();
                    logger.error("Failed to add money to account {}", accountId);
                    request.setAttribute("error", "Failed to add money to the account");
                }
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        logger.error("Error rolling back transaction", ex);
                    }
                }
                logger.error("Error adding money to account", e);
                request.setAttribute("error", "Error processing the request: " + e.getMessage());
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        logger.error("Error closing connection", e);
                    }
                }
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid input format", e);
            request.setAttribute("error", "Invalid input format");
        }

        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
} 