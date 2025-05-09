package com.bank.model;

import java.time.LocalDateTime;

public class BankAccount {
    private int accountId;
    private int userId;
    private String accountType;
    private double balance;
    private LocalDateTime dateOpened;
    private boolean isActive;
    private String accountNumber;

    public BankAccount() {
    }

    public BankAccount(int accountId, int userId, String accountType, double balance, LocalDateTime dateOpened, boolean isActive, String accountNumber) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountType = accountType;
        this.balance = balance;
        this.dateOpened = dateOpened;
        this.isActive = isActive;
        this.accountNumber = accountNumber;
    }

    // Getters and Setters
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDateTime getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(LocalDateTime dateOpened) {
        this.dateOpened = dateOpened;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getAccountNumber() {
        return String.valueOf(accountId);
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}