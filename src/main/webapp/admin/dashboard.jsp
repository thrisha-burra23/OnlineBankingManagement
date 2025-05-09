<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
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
                        <a class="nav-link active" href="/banking/admin/dashboard">
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
                        <a class="nav-link" href="transfers.jsp">
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
                <h2 class="mb-4">Dashboard Overview</h2>

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

                <!-- Users Table -->
                <div class="table-responsive">
                    <h3 class="mb-3">Users</h3>
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Full Name</th>
                                <th>Email</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${users}" var="user">
                                <tr>
                                    <td>${user.userId}</td>
                                    <td>${user.username}</td>
                                    <td>${user.fullName}</td>
                                    <td>${user.email}</td>
                                    <td>
                                        <span class="badge bg-${user.status == 'active' ? 'success' : 'warning'}">
                                            ${user.status}
                                        </span>
                                    </td>
                                    <td>
                                        <c:if test="${user.status != 'active'}">
                                            <form action="${pageContext.request.contextPath}/admin/activateUser" method="post" style="display: inline;">
                                                <input type="hidden" name="userId" value="${user.userId}">
                                                <button type="submit" class="btn btn-success btn-sm">Activate</button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <!-- Accounts Table -->
                <div class="table-responsive">
                    <h3 class="mb-3">Accounts</h3>
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Account ID</th>
                                <th>User ID</th>
                                <th>Type</th>
                                <th>Balance</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${accounts}" var="account">
                                <tr>
                                    <td>${account.accountId}</td>
                                    <td>${account.userId}</td>
                                    <td>${account.accountType}</td>
                                    <td>$<fmt:formatNumber value="${account.balance}" pattern="#,##0.00"/></td>
                                    <td>
                                        <span class="badge bg-${account.active ? 'success' : 'danger'}">
                                            ${account.active ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>
                                    <td>
                                        <c:if test="${account.active}">
                                            <button type="button" class="btn btn-primary btn-sm" 
                                                    data-bs-toggle="modal" 
                                                    data-bs-target="#addMoneyModal${account.accountId}">
                                                Add Money
                                            </button>
                                        </c:if>
                                    </td>
                                </tr>

                                <!-- Add Money Modal for each account -->
                                <div class="modal fade" id="addMoneyModal${account.accountId}" tabindex="-1" 
                                     aria-labelledby="addMoneyModalLabel${account.accountId}" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="addMoneyModalLabel${account.accountId}">
                                                    Add Money to Account #${account.accountId}
                                                </h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                            </div>
                                            <form action="${pageContext.request.contextPath}/admin/addMoney" method="post">
                                                <div class="modal-body">
                                                    <input type="hidden" name="accountId" value="${account.accountId}">
                                                    <div class="mb-3">
                                                        <label for="amount${account.accountId}" class="form-label">Amount ($)</label>
                                                        <input type="number" step="0.01" min="0.01" class="form-control" 
                                                               id="amount${account.accountId}" name="amount" required>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                    <button type="submit" class="btn btn-primary">Add Money</button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <!-- Loans Table -->
                <div class="table-responsive">
                    <h3 class="mb-3">Loans</h3>
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Loan ID</th>
                                <th>User ID</th>
                                <th>Amount</th>
                                <th>Interest Rate</th>
                                <th>Status</th>
                                <th>Start Date</th>
                                <th>Remaining Balance</th>
                                <th>Amount Paid</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${loans}" var="loan">
                                <tr>
                                    <td>${loan.loanId}</td>
                                    <td>${loan.userId}</td>
                                    <td>$<fmt:formatNumber value="${loan.loanAmount}" pattern="#,##0.00"/></td>
                                    <td>${loan.interestRate}%</td>
                                    <td>
                                        <span class="badge bg-${loan.status.toUpperCase() == 'APPROVED' ? 'success' : loan.status.toUpperCase() == 'PENDING' ? 'warning' : 'danger'}">
                                            ${loan.status}
                                        </span>
                                    </td>
                                    <td><fmt:parseDate value="${loan.startDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedStartDate" type="both" />
                                        <fmt:formatDate value="${parsedStartDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                                    <td>$<fmt:formatNumber value="${loan.remainingBalance}" pattern="#,##0.00"/></td>
                                    <td>$<fmt:formatNumber value="${loan.totalPaid}" pattern="#,##0.00"/></td>
                                    <td>
                                        <c:if test="${loan.status.toUpperCase() == 'PENDING'}">
                                            <form action="${pageContext.request.contextPath}/admin/processLoan" method="post" style="display: inline;">
                                                <input type="hidden" name="loanId" value="${loan.loanId}">
                                                <input type="hidden" name="action" value="approve">
                                                <button type="submit" class="btn btn-success btn-sm">Approve</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/admin/processLoan" method="post" style="display: inline;">
                                                <input type="hidden" name="loanId" value="${loan.loanId}">
                                                <input type="hidden" name="action" value="reject">
                                                <button type="submit" class="btn btn-danger btn-sm">Reject</button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <!-- Transfers Table -->
                <div class="table-responsive">
                    <h3 class="mb-3">Transfers</h3>
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Transfer ID</th>
                                <th>From Account</th>
                                <th>To Account</th>
                                <th>Amount</th>
                                <th>Status</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${transfers}" var="transfer">
                                <tr>
                                    <td>${transfer.transferId}</td>
                                    <td>${transfer.fromAccountId}</td>
                                    <td>${transfer.toAccountId}</td>
                                    <td>$<fmt:formatNumber value="${transfer.amount}" pattern="#,##0.00"/></td>
                                    <td>
                                        <span class="badge bg-${transfer.status == 'COMPLETED' ? 'success' : transfer.status == 'PENDING' ? 'warning' : 'danger'}">
                                            ${transfer.status}
                                        </span>
                                    </td>
                                    <td>${transfer.createdAt}</td>
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