<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin - Transfers Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/boxicons@2.0.7/css/boxicons.min.css" rel="stylesheet">
    <style>
        .sidebar {
            min-height: 100vh;
            background: #2c3e50;
            color: white;
        }
        .sidebar .nav-link {
            color: white;
            padding: 1rem;
        }
        .sidebar .nav-link:hover {
            background: #34495e;
        }
        .sidebar .nav-link.active {
            background: #3498db;
        }
        .table-responsive {
            margin-top: 2rem;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 px-0 sidebar">
                <div class="p-3">
                    <h4>Admin Panel</h4>
                </div>
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link" href="dashboard.jsp">
                            <i class='bx bxs-dashboard'></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="users.jsp">
                            <i class='bx bxs-user-detail'></i> Users
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="accounts.jsp">
                            <i class='bx bxs-bank'></i> Accounts
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="loans.jsp">
                            <i class='bx bxs-wallet'></i> Loans
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="transfers.jsp">
                            <i class='bx bxs-transfer'></i> Transfers
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="../welcome.html">
                            <i class='bx bxs-log-out'></i> Logout
                        </a>
                    </li>
                </ul>
            </div>

            <!-- Main Content -->
            <div class="col-md-9 col-lg-10 p-4">
                <h2 class="mb-4">Transfers Management</h2>

                <!-- Messages -->
                <c:if test="${not empty message}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        ${message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <!-- Transfers Table -->
                <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Transfer ID</th>
                                <th>From Account</th>
                                <th>To Account</th>
                                <th>Amount</th>
                                <th>Type</th>
                                <th>Status</th>
                                <th>Created At</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${transfers}" var="transfer">
                                <tr>
                                    <td>${transfer.transferId}</td>
                                    <td>${transfer.fromAccountId}</td>
                                    <td>${transfer.toAccountId}</td>
                                    <td>$<fmt:formatNumber value="${transfer.amount}" pattern="#,##0.00"/></td>
                                    <td>${transfer.transferType}</td>
                                    <td>
                                        <span class="badge bg-${transfer.status.toUpperCase() == 'COMPLETED' ? 'success' : transfer.status.toUpperCase() == 'PENDING' ? 'warning' : 'danger'}">
                                            ${transfer.status}
                                        </span>
                                    </td>
                                    <td><fmt:parseDate value="${transfer.createdAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                        <fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 