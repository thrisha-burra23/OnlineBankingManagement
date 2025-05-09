package com.bank.servlet;

import com.bank.dao.LoanDAO;
import com.bank.dao.BankAccountDAO;
import com.bank.dao.UserDAO;
import com.bank.model.Loan;
import com.bank.model.BankAccount;
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
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/admin/processLoan")
public class ProcessLoanServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ProcessLoanServlet.class);
    private LoanDAO loanDAO;
    private BankAccountDAO accountDAO;

    @Override
    public void init() throws ServletException {
        loanDAO = new LoanDAO();
        accountDAO = new BankAccountDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.jsp");
            return;
        }

        int loanId = Integer.parseInt(request.getParameter("loanId"));
        String action = request.getParameter("action");

        boolean success = false;
        if ("approve".equals(action)) {
            // Get the loan details
            Loan loan = loanDAO.getLoanById(loanId);
            if (loan != null) {
                // Get user's current account
                BankAccount currentAccount = accountDAO.getCurrentAccountByUserId(loan.getUserId());
                if (currentAccount != null) {
                    // Start transaction
                    Connection conn = null;
                    try {
                        conn = com.bank.util.DatabaseUtil.getConnection();
                        conn.setAutoCommit(false);

                        // Update loan status
                        success = loanDAO.updateLoanStatus(loanId, "APPROVED");
                        if (success) {
                            // Get addAmount from session
                            Double addAmount = (Double) request.getSession().getAttribute("addAmount");
                            if (addAmount == null) {
                                addAmount = loan.getLoanAmount(); // Fallback to loan amount if session value not found
                            }
                            
                            // Add loan amount to current account
                            success = accountDAO.updateAccountBalance(currentAccount.getAccountId(),
                                    addAmount);
                            if (success) {
                                conn.commit();
                                logger.info("Loan {} approved and amount {} added to current account {}",
                                        loanId, addAmount, currentAccount.getAccountId());

                                // Get user details for email notification
                                User user = new UserDAO().getUserById(loan.getUserId());
                                if (user != null) {
                                    // Send approval email
                                    EmailUtil.sendLoanApprovalEmail(
                                            user.getEmail(),
                                            user.getFullName(),
                                            loan);
                                }

                                request.setAttribute("message",
                                        "Loan approved and amount added to user's current account");
                            } else {
                                conn.rollback();
                                logger.error("Failed to update account balance for loan {}", loanId);
                                request.setAttribute("error", "Failed to update account balance");
                            }
                        } else {
                            conn.rollback();
                            logger.error("Failed to update loan status for loan {}", loanId);
                            request.setAttribute("error", "Failed to update loan status");
                        }
                    } catch (SQLException e) {
                        if (conn != null) {
                            try {
                                conn.rollback();
                            } catch (SQLException ex) {
                                logger.error("Error rolling back transaction", ex);
                            }
                        }
                        logger.error("Error processing loan approval", e);
                        request.setAttribute("error", "Error processing loan approval: " + e.getMessage());
                        success = false;
                    } finally {
                        if (conn != null) {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                logger.error("Error closing connection", e);
                            }
                        }
                    }
                } else {
                    logger.error("No current account found for user {}", loan.getUserId());
                    request.setAttribute("error",
                            "User does not have a current account. Please create a current account first.");
                    // Keep loan as pending
                    success = false;
                }
            } else {
                logger.error("Loan {} not found", loanId);
                request.setAttribute("error", "Loan not found");
            }
        } else if ("reject".equals(action)) {
            success = loanDAO.updateLoanStatus(loanId, "REJECTED");
            if (success) {
                request.setAttribute("message", "Loan rejected successfully");
            } else {
                request.setAttribute("error", "Failed to reject loan");
            }
        }

        // Redirect back to loans page
        response.sendRedirect(request.getContextPath() + "/admin/loans");
    }
}