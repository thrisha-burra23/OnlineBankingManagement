<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.bank.model.User" %>
<%@ page import="com.bank.model.Transfer" %>
<%@ page import="com.bank.model.BankAccount" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    // Get transfers from session instead of request
    List<Transfer> transfers = (List<Transfer>) session.getAttribute("transfers");
    List<BankAccount> accounts = (List<BankAccount>) session.getAttribute("accounts");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Transaction History - Banking System</title>
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
        .transactions-section {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        .transactions-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .filter-section {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
        }
        .filter-section select,
        .filter-section input {
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
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
        .status-completed {
            color: #28a745;
            font-weight: bold;
        }
        .status-pending {
            color: #ffc107;
            font-weight: bold;
        }
        .status-failed {
            color: #dc3545;
            font-weight: bold;
        }
        .amount-positive {
            color: #28a745;
        }
        .amount-negative {
            color: #dc3545;
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
        .back-link {
            display: inline-block;
            margin-top: 20px;
            color: #007bff;
            text-decoration: none;
        }
        .back-link:hover {
            text-decoration: underline;
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
            <h1>Transaction History</h1>
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

        <div class="transactions-section">
            <div class="transactions-header">
                <h2>My Transactions</h2>
            </div>
            
            <div class="filter-section">
                <select name="accountFilter" id="accountFilter">
                    <option value="">All Accounts</option>
                    <% if (accounts != null && !accounts.isEmpty()) { %>
                        <% for (BankAccount account : accounts) { %>
                            <option value="<%= account.getAccountId() %>">
                                <%= account.getAccountType() %> - <%= account.getAccountId() %>
                            </option>
                        <% } %>
                    <% } %>
                </select>
                <select name="typeFilter" id="typeFilter">
                    <option value="">All Types</option>
                    <option value="intra-bank">Intra-Bank</option>
                    <option value="inter-bank">Inter-Bank</option>
                </select>
                <select name="statusFilter" id="statusFilter">
                    <option value="">All Status</option>
                    <option value="completed">Completed</option>
                    <option value="pending">Pending</option>
                    <option value="failed">Failed</option>
                </select>
                <input type="date" name="dateFilter" id="dateFilter" placeholder="Filter by date">
            </div>
            
            <table>
                <thead>
                    <tr>
                        <th>Transaction ID</th>
                        <th>Date</th>
                        <th>From Account</th>
                        <th>To Account</th>
                        <th>Type</th>
                        <th>Amount</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (transfers != null && !transfers.isEmpty()) { %>
                        <% for (Transfer transfer : transfers) { %>
                            <tr>
                                <td><%= transfer.getTransferId() %></td>
                                <td><%= transfer.getCreatedAt().format(formatter) %></td>
                                <td><%= transfer.getFromAccountId() %></td>
                                <td><%= transfer.getToAccountId() %></td>
                                <td><%= transfer.getTransferType() %></td>
                                <td class="amount-negative">-$<%= String.format("%.2f", transfer.getAmount()) %></td>
                                <td class="status-<%= transfer.getStatus() %>"><%= transfer.getStatus() %></td>
                            </tr>
                        <% } %>
                    <% } else { %>
                        <tr>
                            <td colspan="7" style="text-align: center;">No transactions found</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

    <script>
        // Add event listeners for filters
        document.getElementById('accountFilter').addEventListener('change', filterTransactions);
        document.getElementById('typeFilter').addEventListener('change', filterTransactions);
        document.getElementById('statusFilter').addEventListener('change', filterTransactions);
        document.getElementById('dateFilter').addEventListener('change', filterTransactions);

        function filterTransactions() {
            const accountFilter = document.getElementById('accountFilter').value;
            const typeFilter = document.getElementById('typeFilter').value;
            const statusFilter = document.getElementById('statusFilter').value;
            const dateFilter = document.getElementById('dateFilter').value;

            const rows = document.querySelectorAll('tbody tr');
            rows.forEach(row => {
                if (row.cells.length === 1) return; // Skip "No transactions found" row

                const fromAccount = row.cells[2].textContent;
                const toAccount = row.cells[3].textContent;
                const type = row.cells[4].textContent;
                const status = row.cells[6].textContent;
                const date = row.cells[1].textContent.split(' ')[0]; // Get date part only

                const accountMatch = !accountFilter || fromAccount === accountFilter || toAccount === accountFilter;
                const typeMatch = !typeFilter || type === typeFilter;
                const statusMatch = !statusFilter || status === statusFilter;
                const dateMatch = !dateFilter || date === dateFilter;

                row.style.display = accountMatch && typeMatch && statusMatch && dateMatch ? '' : 'none';
            });
        }
    </script>
</body>
</html> 