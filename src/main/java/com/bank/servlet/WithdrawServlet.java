package com.bank.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.bank.dao.BankAccountDAO;

@WebServlet("/withdraw")
public class WithdrawServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BankAccountDAO accountDAO;

    @Override
    public void init() throws ServletException {
        accountDAO = new BankAccountDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountId = request.getParameter("accountId");
        String amount = request.getParameter("amount");
        String password = request.getParameter("password");

        if (accountId == null || accountId.trim().isEmpty()) {
            request.setAttribute("error", "Please enter your account ID");
        } else if (amount == null || amount.trim().isEmpty()) {
            request.setAttribute("error", "Please enter the amount to withdraw");
        } else if (password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please enter your password to withdraw money");
        } else {
            boolean success = false;
            try {
                double amountToWithdraw = Double.parseDouble(amount);
                success = accountDAO.withdrawFromAccount(Integer.parseInt(accountId), amountToWithdraw, password);
                if (success) {
                    request.setAttribute("message", "Withdrawal successful");
                } else {
                    request.setAttribute("error", "Failed to withdraw money. Please check your balance and password.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid amount format");
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/withdraw.jsp").forward(request, response);
    }
}