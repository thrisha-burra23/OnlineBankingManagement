package com.bank.dao;
import com.bank.model.Loan;
import com.bank.model.BankAccount;
import com.bank.model.User;
import com.bank.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BankAccountDAO {
    private static final Logger logger = LoggerFactory.getLogger(BankAccountDAO.class);

    public boolean createAccount(BankAccount account) {
        // First check if user already has an account of this type
        if (hasAccountOfType(account.getUserId(), account.getAccountType())) {
            logger.warn("User {} already has an active account of type {}",
                    account.getUserId(), account.getAccountType());
            return false;
        }

        String query = "INSERT INTO Accounts (user_id, account_type, balance, date_opened, is_active) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, account.getUserId());
            pstmt.setString(2, account.getAccountType());
            pstmt.setDouble(3, account.getBalance());
            pstmt.setObject(4, LocalDateTime.now());
            pstmt.setBoolean(5, true);

            int rowsAffected = pstmt.executeUpdate();
            logger.info("Created new account for user {}: {} rows affected", account.getUserId(), rowsAffected);
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("Error creating account for user {}: {}", account.getUserId(), e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String generateAccountNumber() {
        // Generate a random 10-digit account number
        return String.format("%010d", (long) (Math.random() * 10000000000L));
    }

    public List<BankAccount> getAccountsByUserId(int userId) {
        List<BankAccount> accounts = new ArrayList<>();
        String query = "SELECT * FROM Accounts WHERE user_id = ? ORDER BY date_opened DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            logger.info("Executing query to get accounts for user: {}", userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BankAccount account = new BankAccount();
                    account.setAccountId(rs.getInt("account_id"));
                    account.setUserId(rs.getInt("user_id"));
                    account.setAccountType(rs.getString("account_type"));
                    account.setBalance(rs.getDouble("balance"));
                    account.setDateOpened(rs.getTimestamp("date_opened").toLocalDateTime());
                    account.setActive(rs.getBoolean("is_active"));
                    accounts.add(account);
                    logger.debug("Found account: ID={}, Type={}, Balance={}",
                            account.getAccountId(),
                            account.getAccountType(),
                            account.getBalance());
                }
            }
            logger.info("Retrieved {} accounts for user {}", accounts.size(), userId);
        } catch (SQLException e) {
            logger.error("Error retrieving accounts for user {}: {}", userId, e.getMessage());
            e.printStackTrace();
        }
        return accounts;
    }

    public BankAccount getAccountById(int accountId) {
        String query = "SELECT * FROM Accounts WHERE account_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, accountId);
            logger.info("Executing query to get account: {}", accountId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BankAccount account = new BankAccount();
                    account.setAccountId(rs.getInt("account_id"));
                    account.setUserId(rs.getInt("user_id"));
                    account.setAccountType(rs.getString("account_type"));
                    account.setBalance(rs.getDouble("balance"));
                    account.setDateOpened(rs.getTimestamp("date_opened").toLocalDateTime());
                    account.setActive(rs.getBoolean("is_active"));
                    logger.info("Found account: ID={}, Type={}, Balance={}",
                            account.getAccountId(),
                            account.getAccountType(),
                            account.getBalance());
                    return account;
                }
            }
            logger.warn("No account found with ID: {}", accountId);
        } catch (SQLException e) {
            logger.error("Error retrieving account {}: {}", accountId, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateAccountStatus(int accountId, boolean isActive) {
        String query = "UPDATE Accounts SET is_active = ? WHERE account_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, accountId);

            int rowsAffected = pstmt.executeUpdate();
            logger.info("Updated account {} status to {}: {} rows affected",
                    accountId, isActive, rowsAffected);
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("Error updating account {} status: {}", accountId, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAccountBalance(int accountId, double amount) {
        String query = "UPDATE Accounts SET balance = balance + ? WHERE account_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);

            int rowsAffected = pstmt.executeUpdate();
            logger.info("Updated account {} balance by {}: {} rows affected",
                    accountId, amount, rowsAffected);
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("Error updating account {} balance: {}", accountId, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean activateAccount(int accountId) {
        String query = "UPDATE accounts SET is_active = true WHERE account_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, accountId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyUserPassword(int userId, String password) {
        String query = "SELECT password_hash FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    // In a real application, you would hash the provided password and compare
                    // For this example, we're doing a direct comparison
                    return storedHash.equals(password);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean withdrawFromAccount(int accountId, double amount, String password) {
        // First get the user ID from the account
        String getUserIdQuery = "SELECT user_id FROM accounts WHERE account_id = ?";
        int userId = -1;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement getUserIdStmt = conn.prepareStatement(getUserIdQuery)) {

            getUserIdStmt.setInt(1, accountId);
            try (ResultSet rs = getUserIdStmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("user_id");
                } else {
                    return false; // Account not found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Verify the password
        UserDAO userDAO = new UserDAO();
        if (!userDAO.verifyUserPassword(userId, password)) {
            return false;
        }

        // Check if account has sufficient balance
        String checkBalanceQuery = "SELECT balance FROM accounts WHERE account_id = ?";
        String updateBalanceQuery = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkBalanceQuery);
                 PreparedStatement updateStmt = conn.prepareStatement(updateBalanceQuery)) {

                // Check current balance
                checkStmt.setInt(1, accountId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        double currentBalance = rs.getDouble("balance");
                        if (currentBalance < amount) {
                            conn.rollback();
                            return false; // Insufficient balance
                        }
                    } else {
                        conn.rollback();
                        return false; // Account not found
                    }
                }

                // Update balance
                updateStmt.setDouble(1, amount);
                updateStmt.setInt(2, accountId);
                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<BankAccount> getAllAccounts() {
        List<BankAccount> accounts = new ArrayList<>();
        String query = "SELECT * FROM Accounts ORDER BY date_opened DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                BankAccount account = new BankAccount();
                account.setAccountId(rs.getInt("account_id"));
                account.setUserId(rs.getInt("user_id"));
                account.setAccountType(rs.getString("account_type"));
                account.setBalance(rs.getDouble("balance"));
                account.setDateOpened(rs.getTimestamp("date_opened").toLocalDateTime());
                account.setActive(rs.getBoolean("is_active"));
                accounts.add(account);
                logger.debug("Loaded account: ID={}, Type={}, Balance={}",
                        account.getAccountId(),
                        account.getAccountType(),
                        account.getBalance());
            }
            logger.info("Total accounts retrieved: {}", accounts.size());
        } catch (SQLException e) {
            logger.error("Error retrieving all accounts: {}", e.getMessage());
            e.printStackTrace();
        }

        return accounts;
    }

    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans ORDER BY start_date DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("loan_id"));
                loan.setUserId(rs.getInt("user_id"));
                loan.setLoanAmount(rs.getDouble("loan_amount"));
                loan.setInterestRate(rs.getDouble("interest_rate"));
                loan.setStatus(rs.getString("status"));

                // Handle nullable timestamps
                if (rs.getTimestamp("start_date") != null) {
                    loan.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
                }
                if (rs.getTimestamp("end_date") != null) {
                    loan.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
                }

                loans.add(loan);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loans;
    }

    public boolean hasAccountOfType(int userId, String accountType) {
        String query = "SELECT COUNT(*) FROM Accounts WHERE user_id = ? AND account_type = ? AND is_active = true";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, accountType);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.info("User {} has {} active accounts of type {}", userId, count, accountType);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking account type for user {}: {}", userId, e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public BankAccount getCheckingAccountByUserId(int userId) {
        String query = "SELECT * FROM Accounts WHERE user_id = ? AND account_type = 'CHECKING' AND is_active = true";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            logger.info("Executing query to get checking account for user: {}", userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BankAccount account = new BankAccount();
                    account.setAccountId(rs.getInt("account_id"));
                    account.setUserId(rs.getInt("user_id"));
                    account.setAccountType(rs.getString("account_type"));
                    account.setBalance(rs.getDouble("balance"));
                    account.setDateOpened(rs.getTimestamp("date_opened").toLocalDateTime());
                    account.setActive(rs.getBoolean("is_active"));
                    logger.info("Found checking account: ID={}, Balance={}", account.getAccountId(), account.getBalance());
                    return account;
                }
            }
            logger.warn("No active checking account found for user: {}", userId);
        } catch (SQLException e) {
            logger.error("Error retrieving checking account for user {}: {}", userId, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public BankAccount getCurrentAccountByUserId(int userId) {
        String query = "SELECT * FROM accounts WHERE user_id = ? AND account_type = 'checking' AND is_active = true";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                BankAccount account = new BankAccount();
                account.setAccountId(rs.getInt("account_id"));
                account.setUserId(rs.getInt("user_id"));
                account.setAccountType(rs.getString("account_type"));
                account.setBalance(rs.getDouble("balance"));
                account.setActive(rs.getBoolean("is_active"));
                return account;
            }
        } catch (SQLException e) {
            logger.error("Error getting current account for user {}: {}", userId, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}