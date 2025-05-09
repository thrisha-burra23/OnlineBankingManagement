package com.bank.servlet;

import com.bank.dao.LoanDAO;
import com.bank.model.Loan;
import com.bank.model.User;
import com.bank.util.EmailUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/user/loans")
public class LoanServlet extends HttpServlet {
    private LoanDAO loanDAO;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void init() throws ServletException {
        loanDAO = new LoanDAO();
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

        // Get user's loans and set them in session
        session.setAttribute("loans", loanDAO.getLoansByUserId(user.getUserId()));
        request.getRequestDispatcher("loans.jsp").forward(request, response);
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

        String action = request.getParameter("action");

        if ("apply".equals(action)) {
            handleLoanApplication(request, response, user);
        } else {
            response.sendRedirect("loans.jsp");
        }
    }

    private void handleLoanApplication(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        try {
            // Get form dataloanAmount
            double addAmount = Double.parseDouble(request.getParameter("loanAmount"));
            // Store addAmount in session
            request.getSession().setAttribute("addAmount", addAmount);
            double loanAmount = Double.parseDouble(request.getParameter("totalAmount"));
            double interestRate = Double.parseDouble(request.getParameter("interestRate"));
            LocalDate startDate = LocalDate.parse(request.getParameter("startDate"), DATE_FORMATTER);
            LocalDate endDate = LocalDate.parse(request.getParameter("endDate"), DATE_FORMATTER);

            // Validate dates
            if (startDate.isBefore(LocalDate.now())) {
                request.setAttribute("error", "Start date cannot be in the past");
                request.getRequestDispatcher("loans.jsp").forward(request, response);
                return;
            }

            if (endDate.isBefore(startDate)) {
                request.setAttribute("error", "End date must be after start date");
                request.getRequestDispatcher("loans.jsp").forward(request, response);
                return;
            }

            // Create loan object
            Loan loan = new Loan();
            loan.setUserId(user.getUserId());
            loan.setLoanAmount(loanAmount);
            loan.setInterestRate(interestRate);
            loan.setStatus("PENDING");
            loan.setStartDate(LocalDateTime.of(startDate, LocalTime.MIN));
            loan.setEndDate(LocalDateTime.of(endDate, LocalTime.MAX));
            loan.setRemainingBalance(loanAmount);
            loan.setTotalPaid(0.0);

            // Save loan to database
            if (loanDAO.createLoan(loan)) {
                // Send email to user about document verification
                EmailUtil.sendLoanApplicationEmail(user.getEmail(), user.getUsername());
                request.setAttribute("message",
                        "Loan application submitted successfully. Please check your email for further instructions.");
            } else {
                request.setAttribute("error", "Failed to submit loan application");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid amount or interest rate format");
        } catch (Exception e) {
            request.setAttribute("error", "An error occurred while processing your request");
        }

        // Refresh the loans list
        request.getSession().setAttribute("loans", loanDAO.getLoansByUserId(user.getUserId()));
        request.getRequestDispatcher("loans.jsp").forward(request, response);
    }
}