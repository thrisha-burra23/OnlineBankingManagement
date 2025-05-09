<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container mt-4">
    <h2>Apply for a Loan</h2>
    
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    
    <div class="card">
        <div class="card-body">
            <form action="<c:url value='/loan/apply'/>" method="post">
                <div class="mb-3">
                    <label for="loanAmount" class="form-label">Loan Amount ($)</label>
                    <input type="number" class="form-control" id="loanAmount" name="loanAmount" 
                           min="100" step="100" required>
                    <div class="form-text">Minimum loan amount is $100</div>
                </div>
                
                <div class="mb-3">
                    <label for="interestRate" class="form-label">Interest Rate (%)</label>
                    <input type="number" class="form-control" id="interestRate" name="interestRate" 
                           min="1" max="30" step="0.1" required>
                    <div class="form-text">Interest rate must be between 1% and 30%</div>
                </div>
                
                <div class="mb-3">
                    <button type="submit" class="btn btn-primary">Submit Application</button>
                    <a href="<c:url value='/loan'/>" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div> 