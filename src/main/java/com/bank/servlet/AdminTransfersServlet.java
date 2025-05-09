package com.bank.servlet;

import com.bank.dao.BankAccountDAO;
import com.bank.model.Transfer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/transfers")
public class AdminTransfersServlet extends HttpServlet {
    private BankAccountDAO accountDAO;

    @Override
    public void init() throws ServletException {
        accountDAO = new BankAccountDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        //List<Transfer> transfers = accountDAO.getAllTransfers();
       // request.setAttribute("transfers", transfers);
        //request.getRequestDispatcher("/admin/transfers.jsp").forward(request, response);
    }
} 