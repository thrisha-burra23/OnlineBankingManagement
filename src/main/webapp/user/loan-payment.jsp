<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Loan Payment</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .loan-card {
            margin-bottom: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .payment-form {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <div class="container mt-4">
        <h2 class="mb-4">Loan Payment</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty message}">
            <div class="alert alert-success">${message}</div>
        </c:if>

        <c:if test="${empty loans}">
            <div class="alert alert-info">
                You don't have any active loans to pay.
            </div>
        </c:if>

        <c:forEach var="loan" items="${loans}">
            <div class="card loan-card">
                <div class="card-body">
                    <h5 class="card-title">Loan #${loan.loanId}</h5>
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>Loan Amount:</strong> $<fmt:formatNumber value="${loan.loanAmount}" pattern="#,##0.00"/></p>
                            <p><strong>Interest Rate:</strong> ${loan.interestRate}%</p>
                            <p><strong>Start Date:</strong> ${loan.startDate}</p>
                            <p><strong>End Date:</strong> ${loan.endDate}</p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>Remaining Balance:</strong> $<fmt:formatNumber value="${loan.remainingBalance}" pattern="#,##0.00"/></p>
                            <p><strong>Total Paid:</strong> $<fmt:formatNumber value="${loan.totalPaid}" pattern="#,##0.00"/></p>
                            <p><strong>Status:</strong> ${loan.status}</p>
                        </div>
                    </div>
                    
                    <div class="payment-form">
                        <form action="loan-payment" method="post">
                            <input type="hidden" name="loanId" value="${loan.loanId}">
                            <label>The accountId</label>
                            <input  name="accountId" value="${currentAccount.accountId}">
                            
                            <div class="mb-3">
                                <label for="amount" class="form-label">Payment Amount</label>
                                <div class="input-group">
                                    <span class="input-group-text">$</span>
                                    <input type="number" class="form-control" id="amount" name="amount" 
                                           min="0.01" max="${loan.remainingBalance}" step="0.01" required>
                                </div>
                                <small class="text-muted">Maximum payment: $<fmt:formatNumber value="${loan.remainingBalance}" pattern="#,##0.00"/></small>
                            </div>
                            
                            <div class="mb-3">
                                <p><strong>Payment from:</strong> Current Account (${currentAccount.accountNumber})</p>
                                <p><strong>Available Balance:</strong> $<fmt:formatNumber value="${currentAccount.balance}" pattern="#,##0.00"/></p>
                            </div>
                            
                            <button type="submit" class="btn btn-primary">Make Payment</button>
                        </form>
                    </div>
                </div>
            </div>
        </c:forEach>

        <div class="mt-3">
            <a href="dashboard.jsp" class="btn btn-secondary">Back to Dashboard</a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Validate payment amount
        document.querySelectorAll('form').forEach(form => {
            form.addEventListener('submit', function(e) {
                const amountInput = this.querySelector('#amount');
                const amount = parseFloat(amountInput.value);
                const maxAmount = parseFloat(amountInput.max);
                const balance = parseFloat('${currentAccount.balance}');
                
                if (!amount || isNaN(amount)) {
                    e.preventDefault();
                    alert('Please enter a valid payment amount');
                    amountInput.focus();
                    return;
                }
                
                if (amount <= 0) {
                    e.preventDefault();
                    alert('Payment amount must be greater than 0');
                    amountInput.focus();
                    return;
                }
                
                if (amount > maxAmount) {
                    e.preventDefault();
                    alert('Payment amount cannot exceed remaining loan balance');
                    amountInput.focus();
                    return;
                }
                
                if (amount > balance) {
                    e.preventDefault();
                    alert('Insufficient funds in your account');
                    amountInput.focus();
                    return;
                }
            });
        });
    </script>
</body>
</html> 