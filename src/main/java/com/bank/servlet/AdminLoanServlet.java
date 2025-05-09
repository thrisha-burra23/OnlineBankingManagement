package com.bank.servlet;

import com.bank.dao.LoanDAO;
import com.bank.model.Loan;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/loans")
public class AdminLoanServlet extends HttpServlet {
    private LoanDAO loanDAO;

    @Override
    public void init() throws ServletException {
        loanDAO = new LoanDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        List<Loan> loans = loanDAO.getAllLoans();
        request.setAttribute("loans", loans);
        request.getRequestDispatcher("/admin/loans.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String action = request.getParameter("action");
        int loanId = Integer.parseInt(request.getParameter("loanId"));

        if ("approve".equals(action)) {
            loanDAO.updateLoanStatus(loanId, "APPROVED");
        } else if ("reject".equals(action)) {
            loanDAO.updateLoanStatus(loanId, "REJECTED");
        }

        response.sendRedirect(request.getContextPath() + "/admin/loans");
    }
} 