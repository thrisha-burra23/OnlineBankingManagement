package com.bank.servlet;

import com.bank.dao.TransferDAO;
import com.bank.model.Transfer;
import com.bank.model.User;
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

@WebServlet("/user/transactions")
public class TransactionHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryServlet.class);
    private TransferDAO transferDAO;

    @Override
    public void init() throws ServletException {
        transferDAO = new TransferDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            logger.warn("User not found in session, redirecting to login");
            response.sendRedirect("login.jsp");
            return;
        }

        // Get filter parameters
        String accountId = request.getParameter("accountId");
        String type = request.getParameter("type");
        String status = request.getParameter("status");
        String date = request.getParameter("date");

        logger.info("Fetching transactions for user {} with filters - Account: {}, Type: {}, Status: {}, Date: {}",
                user.getUserId(), accountId, type, status, date);

        try {
            List<Transfer> transfers;
            if (accountId != null && !accountId.isEmpty()) {
                transfers = transferDAO.getTransfersByAccountId(Integer.parseInt(accountId));
            } else {
                // Get all transfers for user's accounts
                transfers = transferDAO.getTransfersByUserId(user.getUserId());
            }

            // Apply filters
            if (type != null && !type.isEmpty()) {
                transfers.removeIf(t -> !t.getTransferType().equals(type));
            }
            if (status != null && !status.isEmpty()) {
                transfers.removeIf(t -> !t.getStatus().equals(status));
            }
            if (date != null && !date.isEmpty()) {
                transfers.removeIf(t -> !t.getCreatedAt().toLocalDate().toString().equals(date));
            }

            logger.info("Found {} transactions for user {}", transfers.size(), user.getUserId());
            request.setAttribute("transfers", transfers);
            request.getRequestDispatcher("transactions.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            logger.error("Invalid account ID format", e);
            request.setAttribute("error", "Invalid account ID");
            request.getRequestDispatcher("transactions.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error fetching transactions", e);
            request.setAttribute("error", "An error occurred while fetching transactions");
            request.getRequestDispatcher("transactions.jsp").forward(request, response);
        }
    }
}