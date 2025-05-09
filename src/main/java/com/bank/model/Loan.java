package com.bank.model;

import java.time.LocalDateTime;

public class Loan {
    private int loanId;
    private int userId;
    private double loanAmount;
    private double interestRate;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double remaining_balance;
    private double amount_paid;

    public Loan() {
    }

    public Loan(int loanId, int userId, double loanAmount, double interestRate,
                String status, LocalDateTime startDate, LocalDateTime endDate, double remaining_balance, double amount_paid) {
        this.loanId = loanId;
        this.userId = userId;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remaining_balance=remaining_balance;
        this.amount_paid=amount_paid;
    }

    // Getters and Setters
    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public double getRemainingBalance() {
        return remaining_balance;
    }

    public void setRemainingBalance(double remaining_balance) {
        this.remaining_balance = remaining_balance;
    }

    public double getTotalPaid() {
        return amount_paid;
    }

    public void setTotalPaid(double amount_paid) {
        this.amount_paid = amount_paid;
    }
}