package com.bank.servlet;

import com.bank.dao.BankAccountDAO;
import com.bank.model.BankAccount;
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

@WebServlet("/user/accounts")
public class UserAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BankAccountDAO accountDAO;
    private static final Logger logger = LoggerFactory.getLogger(UserAccountServlet.class);

    @Override
    public void init() throws ServletException {
        accountDAO = new BankAccountDAO();
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

        logger.info("Loading accounts for user: {}", user.getUserId());
        List<BankAccount> accounts = accountDAO.getAccountsByUserId(user.getUserId());
        logger.info("Found {} accounts for user {}", accounts.size(), user.getUserId());

        request.setAttribute("accounts", accounts);
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
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

        String action = request.getParameter("action");
        logger.info("Processing action: {} for user: {}", action, user.getUserId());

        if ("create".equals(action)) {
            String accountType = request.getParameter("accountType");

            if (accountType == null || accountType.trim().isEmpty()) {
                logger.warn("Account type is empty for user: {}", user.getUserId());
                request.setAttribute("error", "Please select an account type");
            } else {
                BankAccount account = new BankAccount();
                account.setUserId(user.getUserId());
                account.setAccountType(accountType);
                account.setBalance(0.0);
                account.setActive(true);

                logger.info("Creating new account of type: {} for user: {}", accountType, user.getUserId());
                if (accountDAO.createAccount(account)) {
                    logger.info("Account created successfully for user: {}", user.getUserId());
                    request.setAttribute("message", "Account created successfully");
                } else {
                    logger.error("Failed to create account for user: {}", user.getUserId());
                    request.setAttribute("error", "You already have an active account of this type. Please select a different account type.");
                }
            }
        } else if ("deposit".equals(action)) {
            String accountId = request.getParameter("accountId");
            String amount = request.getParameter("amount");

            if (accountId == null || amount == null) {
                request.setAttribute("error", "Invalid deposit request");
            } else {
                try {
                    double depositAmount = Double.parseDouble(amount);
                    if (accountDAO.updateAccountBalance(Integer.parseInt(accountId), depositAmount)) {
                        request.setAttribute("message", "Deposit successful");
                    } else {
                        request.setAttribute("error", "Failed to process deposit");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Invalid amount format");
                }
            }
        } else if ("withdraw".equals(action)) {
            String accountId = request.getParameter("accountId");
            String amount = request.getParameter("amount");
            String password = request.getParameter("password");

            if (accountId == null || amount == null || password == null) {
                request.setAttribute("error", "Invalid withdrawal request");
            } else {
                try {
                    double withdrawAmount = Double.parseDouble(amount);
                    if (accountDAO.withdrawFromAccount(Integer.parseInt(accountId), withdrawAmount, password)) {
                        request.setAttribute("message", "Withdrawal successful");
                    } else {
                        request.setAttribute("error", "Failed to process withdrawal");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Invalid amount format");
                }
            }
        } else if ("activate".equals(action) || "deactivate".equals(action)) {
            String accountId = request.getParameter("accountId");
            boolean activate = "activate".equals(action);

            if (accountId == null) {
                request.setAttribute("error", "Invalid account ID");
            } else {
                if (accountDAO.updateAccountStatus(Integer.parseInt(accountId), activate)) {
                    request.setAttribute("message", "Account " + (activate ? "activated" : "deactivated") + " successfully");
                } else {
                    request.setAttribute("error", "Failed to update account status");
                }
            }
        }

        // Refresh the account list
        logger.info("Refreshing account list for user: {}", user.getUserId());
        List<BankAccount> accounts = accountDAO.getAccountsByUserId(user.getUserId());
        logger.info("Found {} accounts after refresh for user {}", accounts.size(), user.getUserId());

        request.setAttribute("accounts", accounts);
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}