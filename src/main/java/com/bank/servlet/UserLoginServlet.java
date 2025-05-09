package com.bank.servlet;

import com.bank.dao.UserDAO;
import com.bank.dao.BankAccountDAO;
import com.bank.dao.TransferDAO;
import com.bank.dao.LoanDAO;
import com.bank.model.Loan;
import com.bank.model.User;
import com.bank.model.BankAccount;
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
import java.util.List;

@WebServlet("/user/login")
public class UserLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(UserLoginServlet.class);
    private UserDAO userDAO;
    private BankAccountDAO bankAccountDAO;
    private TransferDAO transferDAO;
    private LoanDAO loanDAO;
    

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        bankAccountDAO = new BankAccountDAO();
        transferDAO = new TransferDAO();
        loanDAO = new LoanDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        logger.info("Attempting login for user: {}", username);

        User user = userDAO.authenticate(username, password);

        if (user != null) {
            if (user.getStatus().equals("active")) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                // Fetch user's bank accounts
                logger.info("Fetching bank accounts for user: {}", user.getUserId());
                List<BankAccount> accounts = bankAccountDAO.getAccountsByUserId(user.getUserId());
                logger.info("Found {} accounts for user {}", accounts.size(), user.getUserId());

                // Store accounts in session
                session.setAttribute("accounts", accounts);

                // Fetch user's transaction history
                logger.info("Fetching transaction history for user: {}", user.getUserId());
                List<Transfer> transfers = transferDAO.getTransfersByUserId(user.getUserId());
                logger.info("Found {} transactions for user {}", transfers.size(), user.getUserId());

                // Store transactions in session
                session.setAttribute("transfers", transfers);

                // Fetch user's loans
                logger.info("Fetching loans for user: {}", user.getUserId());
                List<Loan> loans = loanDAO.getLoansByUserId(user.getUserId());
                logger.info("Found {} loans for user {}", loans.size(), user.getUserId());

                // Store loans in session
                session.setAttribute("loans", loans);

                response.sendRedirect("dashboard.jsp");
            } else if (user.getStatus().equals("pending")) {
                request.setAttribute("error", "Your account is pending approval. Please wait for admin verification.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Your account is inactive. Please contact support.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } else {
            logger.warn("Failed login attempt for username: {}", username);
            request.setAttribute("error", "Invalid username or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect("dashboard.jsp");
            return;
        }
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}