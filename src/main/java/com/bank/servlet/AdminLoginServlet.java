package com.bank.servlet;

import com.bank.dao.AdminDAO;
import com.bank.dao.UserDAO;
import com.bank.dao.BankAccountDAO;
import com.bank.dao.LoanDAO;
import com.bank.dao.TransferDAO;
import com.bank.model.Admin;
import com.bank.model.User;
import com.bank.model.BankAccount;
import com.bank.model.Loan;
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

@WebServlet("/admin/auth/login")
public class AdminLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminLoginServlet.class);

    private AdminDAO adminDAO;
    private UserDAO userDAO;
    private BankAccountDAO bankAccountDAO;
    private LoanDAO loanDAO;
    private TransferDAO transferDAO;

    @Override
    public void init() throws ServletException {
        adminDAO = new AdminDAO();
        userDAO = new UserDAO();
        bankAccountDAO = new BankAccountDAO();
        loanDAO = new LoanDAO();
        transferDAO = new TransferDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        logger.info("Admin login attempt for user: {}", username);

        Admin admin = adminDAO.authenticate(username, password);

        if (admin != null) {
            HttpSession session = request.getSession();
            session.setAttribute("admin", admin);

            // Fetch all users
            List<User> users = userDAO.getAllUsers();
            session.setAttribute("users", users);

            // Fetch all bank accounts
            List<BankAccount> accounts = bankAccountDAO.getAllAccounts();
            session.setAttribute("accounts", accounts);

            // Fetch all loans
            List<Loan> loans = loanDAO.getAllLoans();
            session.setAttribute("loans", loans);

            // Fetch all transfers
            List<Transfer> transfers = transferDAO.getAllTransfers();
            session.setAttribute("transfers", transfers);

            // Calculate statistics
            double totalBalance = accounts.stream()
                    .mapToDouble(BankAccount::getBalance)
                    .sum();
            session.setAttribute("totalBalance", totalBalance);

            int pendingLoans = (int) loans.stream()
                    .filter(loan -> "PENDING".equalsIgnoreCase(loan.getStatus()))
                    .count();
            session.setAttribute("pendingLoans", pendingLoans);

            int activeUsers = (int) users.stream()
                    .filter(user -> "active".equalsIgnoreCase(user.getStatus()))
                    .count();
            session.setAttribute("activeUsers", activeUsers);

            response.sendRedirect("../dashboard.jsp");
        } else {
            logger.warn("Failed admin login attempt for username: {}", username);
            request.setAttribute("error", "Invalid admin credentials");
            request.getRequestDispatcher("../login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("admin") != null) {
            response.sendRedirect("../dashboard.jsp");
        } else {
            request.getRequestDispatcher("../login.jsp").forward(request, response);
        }
    }
}
