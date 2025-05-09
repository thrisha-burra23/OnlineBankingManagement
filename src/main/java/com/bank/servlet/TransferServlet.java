package com.bank.servlet;

import com.bank.dao.TransferDAO;
import com.bank.dao.BankAccountDAO;
import com.bank.model.Transfer;
import com.bank.model.User;
import com.bank.model.BankAccount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/user/transfer")
public class TransferServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(TransferServlet.class);
    private TransferDAO transferDAO;
    private BankAccountDAO bankAccountDAO;

    @Override
    public void init() throws ServletException {
        transferDAO = new TransferDAO();
        bankAccountDAO = new BankAccountDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            logger.warn("User not found in session, redirecting to login");
            response.sendRedirect("login.jsp");
            return;
        }

        String fromAccountId = request.getParameter("fromAccountId");
        String toAccountId = request.getParameter("toAccountId");
        String amount = request.getParameter("amount");
        String transferType = request.getParameter("transferType");

        logger.info("Processing transfer request - From: {}, To: {}, Amount: {}, Type: {}",
                fromAccountId, toAccountId, amount, transferType);

        if (fromAccountId == null || toAccountId == null || amount == null || transferType == null) {
            logger.warn("Missing required parameters for transfer");
            request.setAttribute("error", "All fields are required");
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
            return;
        }

        try {
            // Validate source account belongs to user
            BankAccount sourceAccount = bankAccountDAO.getAccountById(Integer.parseInt(fromAccountId));
            if (sourceAccount == null || sourceAccount.getUserId() != user.getUserId()) {
                logger.warn("Invalid source account or account doesn't belong to user");
                request.setAttribute("error", "Invalid source account");
                request.getRequestDispatcher("dashboard.jsp").forward(request, response);
                return;
            }

            // Validate destination account exists
            BankAccount destAccount = bankAccountDAO.getAccountById(Integer.parseInt(toAccountId));
            if (destAccount == null) {
                logger.warn("Destination account not found");
                request.setAttribute("error", "Destination account not found");
                request.getRequestDispatcher("dashboard.jsp").forward(request, response);
                return;
            }

            // Validate amount
            double transferAmount = Double.parseDouble(amount);
            if (transferAmount <= 0) {
                logger.warn("Invalid transfer amount: {}", transferAmount);
                request.setAttribute("error", "Amount must be greater than zero");
                request.getRequestDispatcher("dashboard.jsp").forward(request, response);
                return;
            }

            // Create and process transfer
            Transfer transfer = new Transfer();
            transfer.setFromAccountId(Integer.parseInt(fromAccountId));
            transfer.setToAccountId(Integer.parseInt(toAccountId));
            transfer.setAmount(transferAmount);
            transfer.setTransferType(transferType);
            transfer.setStatus("pending");
            transfer.setCreatedAt(LocalDateTime.now());

            logger.info("Processing transfer: {}", transfer);
            if (transferDAO.processTransfer(transfer)) {
                logger.info("Transfer completed successfully");
                request.setAttribute("message", "Transfer completed successfully");

                // Refresh accounts in session
                List<BankAccount> updatedAccounts = bankAccountDAO.getAccountsByUserId(user.getUserId());
                session.setAttribute("accounts", updatedAccounts);
            } else {
                logger.error("Transfer failed");
                request.setAttribute("error", "Transfer failed. Please check your balance and try again.");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid number format in transfer request", e);
            request.setAttribute("error", "Invalid amount format");
        } catch (Exception e) {
            logger.error("Error processing transfer", e);
            request.setAttribute("error", "An error occurred during transfer. Please try again.");
        }

        response.sendRedirect("dashboard.jsp");
    }
}