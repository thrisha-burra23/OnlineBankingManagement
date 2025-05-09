<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.bank.model.User" %>
<%@ page import="com.bank.model.BankAccount" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<BankAccount> accounts = (List<BankAccount>) session.getAttribute("accounts");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Dashboard - Banking System</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            display: flex;
            min-height: 100vh;
        }
        .sidebar {
            width: 250px;
            background-color: #2c3e50;
            color: white;
            padding: 20px 0;
        }
        .sidebar-header {
            padding: 0 20px 20px;
            border-bottom: 1px solid #34495e;
        }
        .welcome-message {
            font-size: 1.1em;
            margin-bottom: 10px;
        }
        .user-email {
            font-size: 0.9em;
            color: #bdc3c7;
        }
        .nav-menu {
            list-style: none;
            padding: 20px 0;
        }
        .nav-item {
            padding: 10px 20px;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        .nav-item:hover {
            background-color: #34495e;
        }
        .nav-item a {
            color: white;
            text-decoration: none;
            display: block;
        }
        .main-content {
            flex: 1;
            padding: 20px;
        }
        .dashboard-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 1px solid #ddd;
        }
        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .accounts-section {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        .accounts-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f8f9fa;
            font-weight: bold;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .status-active {
            color: #28a745;
            font-weight: bold;
        }
        .status-inactive {
            color: #dc3545;
            font-weight: bold;
        }
        .new-account-form {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .form-group {
            margin-bottom: 1rem;
        }
        label {
            display: block;
            margin-bottom: 0.5rem;
            color: #333;
        }
        select {
            width: 100%;
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        button {
            padding: 0.5rem 1rem;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover {
            background-color: #0056b3;
        }
        .logout-btn {
            padding: 0.5rem 1rem;
            background-color: #dc3545;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
        .logout-btn:hover {
            background-color: #c82333;
        }
        .transfer-form {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-top: 20px;
        }
        .transfer-form h2 {
            margin-bottom: 20px;
        }
        .transfer-form .form-group {
            margin-bottom: 15px;
        }
        .transfer-form label {
            display: block;
            margin-bottom: 5px;
            color: #333;
        }
        .transfer-form select,
        .transfer-form input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .transfer-form button {
            background-color: #28a745;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .transfer-form button:hover {
            background-color: #218838;
        }
    </style>
</head>
<body>
    <div class="sidebar">
        <div class="sidebar-header">
            <div class="welcome-message">Welcome, <%= user.getFullName() %></div>
            <div class="user-email"><%= user.getEmail() %></div>
        </div>
        <ul class="nav-menu">
            <li class="nav-item"><a href="dashboard.jsp">Dashboard</a></li>
            <li class="nav-item"><a href="transactions.jsp">Transactions</a></li>
            <li class="nav-item"><a href="loans.jsp">Loans</a></li>
            <li class="nav-item"><a href="payments.html">Payments</a></li>

        </ul>
    </div>
    
    <div class="main-content">
        <div class="dashboard-header">
            <h1>My Accounts</h1>
            <a href="../welcome.html" class="logout-btn">Logout</a>
        </div>

        <% if (request.getAttribute("message") != null) { %>
            <div class="message success">
                <%= request.getAttribute("message") %>
            </div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
            <div class="message error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <div class="accounts-section">
            <div class="accounts-header">
                <h2>My Bank Accounts</h2>
            </div>
            
            <table>
                <thead>
                    <tr>
                        <th>Account ID</th>
                        <th>Account Type</th>
                        <th>Balance</th>
                        <th>Date Opened</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (accounts != null && !accounts.isEmpty()) { %>
                        <% for (BankAccount account : accounts) { %>
                            <tr>
                                <td><%= account.getAccountId() %></td>
                                <td><%= account.getAccountType() %></td>
                                <td>$<%= String.format("%.2f", account.getBalance()) %></td>
                                <td><%= account.getDateOpened().format(formatter) %></td>
                                <td class="status-<%= account.isActive() ? "active" : "inactive" %>">
                                    <%= account.isActive() ? "Active" : "Inactive" %>
                                </td>
                                <td>
                                    <% if (account.isActive()) { %>
                                        <form action="accounts" method="post" style="display: inline;">
                                            <input type="hidden" name="action" value="deactivate">
                                            <input type="hidden" name="accountId" value="<%= account.getAccountId() %>">
                                            <button type="submit">Deactivate</button>
                                        </form>
                                    <% } else { %>
                                        <form action="accounts" method="post" style="display: inline;">
                                            <input type="hidden" name="action" value="activate">
                                            <input type="hidden" name="accountId" value="<%= account.getAccountId() %>">
                                            <button type="submit">Activate</button>
                                        </form>
                                    <% } %>
                                </td>
                            </tr>
                        <% } %>
                    <% } else { %>
                        <tr>
                            <td colspan="6" style="text-align: center;">No accounts found</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <div class="new-account-form">
            <h2>Open New Account</h2>
            <form action="accounts" method="post">
                <input type="hidden" name="action" value="create">
                <div class="form-group">
                    <label for="accountType">Account Type</label>
                    <select name="accountType" id="accountType" required>
                        <option value="">Select Account Type</option>
                        <option value="Savings">Savings Account</option>
                        <option value="Checking">Checking Account</option>

                    </select>
                </div>
                <button type="submit">Create Account</button>
            </form>
        </div>

        <div class="transfer-form">
            <h2>Transfer Money</h2>
            <form action="transfer" method="post">
                <div class="form-group">
                    <label for="fromAccountId">From Account</label>
                    <select name="fromAccountId" id="fromAccountId" required>
                        <option value="">Select Source Account</option>
                        <% if (accounts != null && !accounts.isEmpty()) { %>
                            <% for (BankAccount account : accounts) { %>
                                <% if (account.isActive()) { %>
                                    <option value="<%= account.getAccountId() %>">
                                        <%= account.getAccountType() %> - $<%= String.format("%.2f", account.getBalance()) %>
                                    </option>
                                <% } %>
                            <% } %>
                        <% } %>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="toAccountId">To Account ID</label>
                    <input type="number" name="toAccountId" id="toAccountId" required 
                           placeholder="Enter recipient's account ID">
                </div>
                
                <div class="form-group">
                    <label for="amount">Amount</label>
                    <input type="number" name="amount" id="amount" required 
                           step="0.01" min="0.01" placeholder="Enter amount">
                </div>
                
                <div class="form-group">
                    <label for="transferType">Transfer Type</label>
                    <select name="transferType" id="transferType" required>
                        <option value="intra-bank">Intra-Bank Transfer</option>
                        <option value="inter-bank">Inter-Bank Transfer</option>
                    </select>
                </div>
                
                <button type="submit">Transfer Money</button>
            </form>
        </div>
    </div>
</body>
</html> 