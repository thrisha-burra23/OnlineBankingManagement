package com.bank.servlet;

import com.bank.dao.LoanDAO;
import com.bank.dao.BankAccountDAO;
import com.bank.model.Loan;
import com.bank.model.User;
import com.bank.model.BankAccount;
import com.bank.util.DatabaseUtil;
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
import java.sql.PreparedStatement;
import java.util.List;

@WebServlet("/user/loan-payment")
public class LoanPaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(LoanPaymentServlet.class);
    private LoanDAO loanDAO;
    private BankAccountDAO bankAccountDAO;

    @Override
    public void init() throws ServletException {
        loanDAO = new LoanDAO();
        bankAccountDAO = new BankAccountDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Get user's current account
        BankAccount currentAccount = bankAccountDAO.getCurrentAccountByUserId(user.getUserId());
        if (currentAccount == null) {
            request.setAttribute("error", "You need a current account to make loan payments");
            request.getRequestDispatcher("loans.jsp").forward(request, response);
            return;
        }

        // Get user's active loans
        List<Loan> activeLoans = loanDAO.getActiveLoansByUserId(user.getUserId());
        request.setAttribute("loans", activeLoans);
        request.setAttribute("currentAccount", currentAccount);
        request.getRequestDispatcher("loan-payment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Debug: Print all request parameters
        logger.debug("Request parameters:");
        request.getParameterMap().forEach((key, values) -> {
            logger.debug("{}: {}", key, String.join(", ", values));
        });

        String loanIdStr = request.getParameter("loanId");
        String amountStr = request.getParameter("amount");
        String accountIdStr = request.getParameter("accountId");

        logger.debug("Raw parameters - loanId: {}, amount: {}, accountId: {}", 
            loanIdStr, amountStr, accountIdStr);

        if (loanIdStr == null || amountStr == null || accountIdStr == null) {
            logger.error("Missing required parameters - loanId: {}, amount: {}, accountId: {}", 
                loanIdStr, amountStr, accountIdStr);
            request.setAttribute("error", "Missing required parameters");
            doGet(request, response);
            return;
        }
        System.out.print("the values of loan id  are "+loanIdStr+"  The amount  "+amountStr+"  the accountid"+accountIdStr+"  the end");
        try {
            // Debug: Print before parsing
            logger.debug("Attempting to parse values - loanId: {}, amount: {}, accountId: {}", 
                loanIdStr, amountStr, accountIdStr);

            int loanId = Integer.parseInt(loanIdStr);
            double amount = Double.parseDouble(amountStr);
            int accountId = Integer.parseInt(accountIdStr);

            // Debug: Print after parsing
            logger.debug("Successfully parsed values - loanId: {}, amount: {}, accountId: {}", 
                loanId, amount, accountId);

            // Verify the account belongs to the user
            BankAccount account = bankAccountDAO.getAccountById(accountId);
            if (account == null || account.getUserId() != user.getUserId()) {
                logger.error("Invalid account - accountId: {}, userId: {}, accountUserId: {}", 
                    accountId, user.getUserId(), account != null ? account.getUserId() : "null");
                request.setAttribute("error", "Invalid account selected");
                doGet(request, response);
                return;
            }

            // Process the loan payment
            boolean success = processLoanPayment(loanId, accountId, amount);
            if (success) {
                logger.info("Loan payment successful - loanId: {}, accountId: {}, amount: {}", 
                    loanId, accountId, amount);
                request.setAttribute("message", "Loan payment processed successfully");
                
                // Update session data
                List<Loan> updatedLoans = loanDAO.getLoansByUserId(user.getUserId());
                session.setAttribute("loans", updatedLoans);
                
                List<BankAccount> updatedAccounts = bankAccountDAO.getAccountsByUserId(user.getUserId());
                session.setAttribute("accounts", updatedAccounts);
            } else {
                logger.error("Loan payment failed - loanId: {}, accountId: {}, amount: {}", 
                    loanId, accountId, amount);
                request.setAttribute("error", "Failed to process loan payment. Please check your balance.");
            }
        } catch (NumberFormatException e) {
            logger.error("Number format exception - loanId: {}, amount: {}, accountId: {}, error: {}", 
                loanIdStr, amountStr, accountIdStr, e.getMessage());
            request.setAttribute("error", "Invalid amount format: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing loan payment", e);
            request.setAttribute("error", "An error occurred while processing your payment: " + e.getMessage());
        }

        doGet(request, response);
    }

    private boolean processLoanPayment(int loanId, int accountId, double amount) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Get loan details
            Loan loan = loanDAO.getLoanById(loanId);
            if (loan == null) {
                logger.error("Loan not found: {}", loanId);
                return false;
            }

            // Debug: Print loan details
            logger.debug("Loan details - loanId: {}, remainingBalance: {}, amountPaid: {}", 
                loan.getLoanId(), loan.getRemainingBalance(), loan.getTotalPaid());

            // 2. Check if payment amount is valid
            if (amount <= 0 || amount > loan.getRemainingBalance()) {
                logger.error("Invalid payment amount: {} for loan: {} (remaining balance: {})", 
                    amount, loanId, loan.getRemainingBalance());
                return false;
            }

            // 3. Check if account has sufficient balance
            BankAccount account = bankAccountDAO.getAccountById(accountId);
            if (account == null || account.getBalance() < amount) {
                logger.error("Insufficient balance in account: {} (balance: {}, payment amount: {})", 
                    accountId, account != null ? account.getBalance() : "null", amount);
                return false;
            }

            // Debug: Print account details
            logger.debug("Account details - accountId: {}, balance: {}", 
                account.getAccountId(), account.getBalance());

            // 4. Update loan balance and amount paid
            String updateLoanSql = "UPDATE loans SET remaining_balance = remaining_balance - ?, amount_paid = amount_paid + ? WHERE loan_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateLoanSql)) {
                pstmt.setDouble(1, amount);
                pstmt.setDouble(2, amount);
                pstmt.setInt(3, loanId);
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated != 1) {
                    throw new SQLException("Failed to update loan balance and amount paid");
                }
                logger.info("Updated loan {}: remaining_balance decreased by {}, amount_paid increased by {}", 
                    loanId, amount, amount);
            }

            // 5. Deduct from account
            String updateAccountSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateAccountSql)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, accountId);
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated != 1) {
                    throw new SQLException("Failed to update account balance");
                }
                logger.info("Deducted {} from account {}", amount, accountId);
            }

            conn.commit();
            logger.info("Successfully processed loan payment: loanId={}, accountId={}, amount={}", loanId, accountId, amount);
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.error("Transaction rolled back due to error: {}", e.getMessage());
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            logger.error("Error processing loan payment: {}", e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection", e);
                }
            }
        }
    }
} 