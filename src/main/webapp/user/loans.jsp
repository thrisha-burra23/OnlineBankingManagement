<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Loans</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
                .loan-card {
                    transition: transform 0.2s;
                    margin-bottom: 1rem;
                }
                .loan-card:hover {
                    transform: translateY(-5px);
                }
                .status-badge {
                    font-size: 0.9rem;
                    padding: 0.5rem 1rem;
                }
                .status-pending { background-color: #ffd700; }
                .status-approved { background-color: #28a745; }
                .status-rejected { background-color: #dc3545; }
                .interest-rate-info {
                    font-size: 0.9rem;
                    color: #6c757d;
                    margin-top: 0.25rem;
                }
                .instructions {
                    background-color: #f8f9fa;
                    border-radius: 5px;
                    padding: 1.5rem;
                    margin-top: 2rem;
                }
                .instructions h4 {
                    color: #0d6efd;
                    margin-bottom: 1rem;
                }
                .instructions ul {
                    padding-left: 1.5rem;
                }
                .instructions li {
                    margin-bottom: 0.5rem;
                }
    </style>
</head>
<body>
    <div class="container py-5">
        <h2 class="mb-4">My Loans</h2>

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

        <div class="row">
            <c:forEach items="${loans}" var="loan">
                <div class="col-md-6 col-lg-4">
                    <div class="card loan-card">
                        <div class="card-body">
                            <h5 class="card-title">Loan #${loan.loanId}</h5>
                            <div class="mb-3">
                                <span class="badge status-badge status-${loan.status.toLowerCase()}">
                                    ${loan.status}
                                </span>
                            </div>
                            <div class="card-text">
                                <p><strong>Amount:</strong> $<fmt:formatNumber value="${loan.loanAmount}" pattern="#,##0.00"/></p>
                                <p><strong>Interest Rate:</strong> <fmt:formatNumber value="${loan.interestRate}" pattern="#,##0.00"/>%</p>
                                <p><strong>Start Date:</strong> ${loan.startDate.toLocalDate()}</p>
                                <p><strong>End Date:</strong> ${loan.endDate.toLocalDate()}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <c:if test="${empty loans}">
            <div class="alert alert-info">
                You don't have any loans yet.
            </div>
        </c:if>

        <div class="mt-4">
            <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#applyLoanModal">
                Apply for a New Loan
            </button>
        </div>
    </div>

    <!-- Apply for Loan Modal -->
    <div class="modal fade" id="applyLoanModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Apply for a Loan</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="loans" method="post" onsubmit="return validateLoanForm()">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="apply">
                        <input type="hidden" name="totalAmount" id="totalAmountHidden">

                        <div class="mb-3">
                            <label for="loanAmount" class="form-label">Loan Amount ($)</label>
                            <input type="number" class="form-control" id="loanAmount" name="loanAmount" required min="100" step="100" onchange="updateInterestRates()">
                            <div class="interest-rate-info">Minimum loan amount: $100</div>
                        </div>

                        <div class="mb-3">
                            <label for="startDate" class="form-label">Start Date</label>
                            <input type="date" class="form-control" id="startDate" name="startDate" required onchange="updateEndDateMin(); updateInterestRates()">
                        </div>

                        <div class="mb-3">
                            <label for="endDate" class="form-label">End Date</label>
                            <input type="date" class="form-control" id="endDate" name="endDate" required onchange="updateInterestRates()">
                            <div class="interest-rate-info" id="loanDurationInfo"></div>
                        </div>

                        <div class="mb-3">
                            <label for="interestRate" class="form-label">Interest Rate (%)</label>
                            <select class="form-select" id="interestRate" name="interestRate" required>
                                <option value="">Select loan amount and duration first</option>
                            </select>
                            <div class="interest-rate-info" id="interestRateInfo"></div>
                        </div>

                        <div class="mb-3">
                            <div class="card">
                                <div class="card-body">
                                    <h5 class="card-title">Loan Summary</h5>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <p><strong>Principal Amount:</strong> $<span id="principalAmount">0.00</span></p>
                                            <p><strong>Interest Rate:</strong> <span id="displayInterestRate">0.00</span>%</p>
                                        </div>
                                        <div class="col-md-6">
                                            <p><strong>Loan Duration:</strong> <span id="displayDuration">0</span> days</p>
                                            <p><strong>Total Interest:</strong> $<span id="totalInterest">0.00</span></p>
                                        </div>
                                    </div>
                                    <div class="mt-3">
                                        <h4 class="text-primary">Total Amount to Pay: $<span id="totalAmount">0.00</span></h4>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Submit Application</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
        <div class="instructions mt-4">
                                <h4><i class="bi bi-info-circle"></i> Loan Application Instructions</h4>
                                <ul>
                                    <li><strong>Loan Amount:</strong> Minimum $100, maximum $50,000. Larger loans typically qualify for better interest rates.</li>
                                    <li><strong>Loan Duration:</strong> Minimum 7 days, maximum 5 years. Shorter terms usually have lower rates.</li>
                                    <li><strong>Interest Rates:</strong> Determined based on amount and duration. Rates are fixed for the loan term.</li>
                                    <li><strong>Repayment:</strong> Full payment (principal + interest) is due on the end date.</li>
                                    <li><strong>Approval:</strong> Applications are typically processed within 1-2 business days.</li>
                                    <li><strong>Early Repayment:</strong> You may repay early without penalty.</li>
                                </ul>
                                <div class="alert alert-warning mt-3">
                                    <strong>Note:</strong> Late payments will incur additional charges of 1.5% per day on the outstanding balance.
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-primary">Submit Application</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Set minimum date for start date to today
        document.getElementById('startDate').min = new Date().toISOString().split('T')[0];

        // Update end date minimum when start date changes
        function updateEndDateMin() {
            const startDate = document.getElementById('startDate').value;
            if (startDate) {
                document.getElementById('endDate').min = startDate;
                document.getElementById('endDate').value = '';
            }
        }

        // Calculate loan duration in days
        function getLoanDurationDays() {
            const startDate = new Date(document.getElementById('startDate').value);
            const endDate = new Date(document.getElementById('endDate').value);

            if (!isNaN(startDate.getTime()) && !isNaN(endDate.getTime())) {
                const diffTime = endDate - startDate;
                return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
            }
            return 0;
        }

        // Calculate and update loan summary
        function updateLoanSummary() {
            const amount = parseFloat(document.getElementById('loanAmount').value) || 0;
            const interestRate = parseFloat(document.getElementById('interestRate').value) || 0;
            const durationDays = getLoanDurationDays();

            // Update principal amount
            document.getElementById('principalAmount').textContent = amount.toFixed(2);
            document.getElementById('displayInterestRate').textContent = interestRate.toFixed(2);
            document.getElementById('displayDuration').textContent = durationDays;

            // Calculate total interest
            const totalInterest = (amount * (interestRate / 100) * (durationDays / 365)).toFixed(2);
            document.getElementById('totalInterest').textContent = totalInterest;

            // Calculate total amount to pay
            const totalAmount = (amount + parseFloat(totalInterest)).toFixed(2);
            document.getElementById('totalAmount').textContent = totalAmount;
            document.getElementById('totalAmountHidden').value = totalAmount;
        }

        // Update interest rates based on loan amount and duration
        function updateInterestRates() {
            const amount = parseFloat(document.getElementById('loanAmount').value) || 0;
            const durationDays = getLoanDurationDays();
            const interestRateSelect = document.getElementById('interestRate');
            const durationInfo = document.getElementById('loanDurationInfo');
            const rateInfo = document.getElementById('interestRateInfo');

            // Clear previous options
            interestRateSelect.innerHTML = '';

            if (amount < 100 || durationDays < 1) {
                interestRateSelect.innerHTML = '<option value="">Select loan amount and duration first</option>';
                durationInfo.textContent = '';
                rateInfo.textContent = '';
                updateLoanSummary();
                return;
            }

            // Calculate duration in months for display
            const durationMonths = Math.ceil(durationDays / 30);
            durationInfo.textContent = `Loan duration: ${durationDays} days (≈${durationMonths} months)`;

            // Determine interest rates based on amount and duration
            let rates = [];

            if (amount <= 1000) {
                // Small loan
                if (durationDays <= 30) {
                    rates = [{value: 5.0, text: "5.0% - Standard rate for small short-term loan"}];
                } else if (durationDays <= 90) {
                    rates = [{value: 6.5, text: "6.5% - Standard rate for small medium-term loan"}];
                } else {
                    rates = [{value: 8.0, text: "8.0% - Standard rate for small long-term loan"}];
                }
            } else if (amount <= 5000) {
                // Medium loan
                if (durationDays <= 30) {
                    rates = [
                        {value: 4.5, text: "4.5% - Preferred rate for medium short-term loan"},
                        {value: 5.0, text: "5.0% - Standard rate for medium short-term loan"}
                    ];
                } else if (durationDays <= 180) {
                    rates = [
                        {value: 5.5, text: "5.5% - Preferred rate for medium medium-term loan"},
                        {value: 6.0, text: "6.0% - Standard rate for medium medium-term loan"}
                    ];
                } else {
                    rates = [
                        {value: 6.5, text: "6.5% - Preferred rate for medium long-term loan"},
                        {value: 7.0, text: "7.0% - Standard rate for medium long-term loan"}
                    ];
                }
            } else {
                // Large loan
                if (durationDays <= 30) {
                    rates = [
                        {value: 3.5, text: "3.5% - Preferred rate for large short-term loan"},
                        {value: 4.0, text: "4.0% - Standard rate for large short-term loan"}
                    ];
                } else if (durationDays <= 180) {
                    rates = [
                        {value: 4.5, text: "4.5% - Preferred rate for large medium-term loan"},
                        {value: 5.0, text: "5.0% - Standard rate for large medium-term loan"}
                    ];
                } else {
                    rates = [
                        {value: 5.5, text: "5.5% - Preferred rate for large long-term loan"},
                        {value: 6.0, text: "6.0% - Standard rate for large long-term loan"}
                    ];
                }
            }

            // Add options to select
            rates.forEach(rate => {
                const option = document.createElement('option');
                option.value = rate.value;
                option.textContent = rate.text;
                interestRateSelect.appendChild(option);
            });

            // Add default option if no rates available
            if (rates.length === 0) {
                interestRateSelect.innerHTML = '<option value="">No available rates for this loan</option>';
            }

            // Update rate information
            if (rates.length > 0) {
                const minRate = Math.min(...rates.map(r => r.value));
                const maxRate = Math.max(...rates.map(r => r.value));

                if (minRate === maxRate) {
                    rateInfo.textContent = `Your interest rate: ${minRate}%`;
                } else {
                    rateInfo.textContent = `Available interest rates: ${minRate}% - ${maxRate}%`;
                }
            } else {
                rateInfo.textContent = '';
            }

            // Add event listener to interest rate select
            interestRateSelect.addEventListener('change', updateLoanSummary);
        }

        // Add event listeners to form inputs
        document.getElementById('loanAmount').addEventListener('input', updateLoanSummary);
        document.getElementById('startDate').addEventListener('change', function() {
            updateEndDateMin();
            updateInterestRates();
            updateLoanSummary();
        });
        document.getElementById('endDate').addEventListener('change', function() {
            updateInterestRates();
            updateLoanSummary();
        });

        // Initial update
        updateLoanSummary();

        // Validate form before submission
        function validateLoanForm() {
            const amount = parseFloat(document.getElementById('loanAmount').value) || 0;
            const durationDays = getLoanDurationDays();
            const interestRate = document.getElementById('interestRate').value;
            const totalAmount = parseFloat(document.getElementById('totalAmountHidden').value) || 0;

            if (amount < 100) {
                alert('Minimum loan amount is $100');
                return false;
            }

            if (durationDays < 7) {
                alert('Minimum loan duration is 7 days');
                return false;
            }

            if (!interestRate) {
                alert('Please select an interest rate');
                return false;
            }

            if (totalAmount <= 0) {
                alert('Invalid total amount calculated');
                return false;
            }

            return true;
        }
    </script>
</body>
</html>