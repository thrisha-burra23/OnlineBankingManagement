package com.bank.dao;

import com.bank.model.Transfer;
import com.bank.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransferDAO {
    private static final Logger logger = LoggerFactory.getLogger(TransferDAO.class);

    public boolean createTransfer(Transfer transfer) {
        String query = "INSERT INTO Transfers (from_account_id, to_account_id, transfer_type, amount, scheduled_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, transfer.getFromAccountId());
            pstmt.setInt(2, transfer.getToAccountId());
            pstmt.setString(3, transfer.getTransferType());
            pstmt.setDouble(4, transfer.getAmount());
            pstmt.setObject(5, transfer.getScheduledDate());
            pstmt.setString(6, transfer.getStatus());

            int rowsAffected = pstmt.executeUpdate();
            logger.info("Created transfer record: {} rows affected", rowsAffected);
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("Error creating transfer record: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean processTransfer(Transfer transfer) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Check if source account has sufficient balance
            String checkBalanceQuery = "SELECT balance, is_active FROM Accounts WHERE account_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkBalanceQuery)) {
                pstmt.setInt(1, transfer.getFromAccountId());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        double currentBalance = rs.getDouble("balance");
                        boolean isActive = rs.getBoolean("is_active");

                        if (!isActive) {
                            logger.warn("Source account {} is inactive", transfer.getFromAccountId());
                            conn.rollback();
                            return false;
                        }

                        if (currentBalance < transfer.getAmount()) {
                            logger.warn("Insufficient funds in account {}: required {}, available {}",
                                    transfer.getFromAccountId(), transfer.getAmount(), currentBalance);
                            conn.rollback();
                            return false;
                        }
                    } else {
                        logger.warn("Source account {} not found", transfer.getFromAccountId());
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Check if destination account exists and is active
            String checkDestQuery = "SELECT is_active FROM Accounts WHERE account_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkDestQuery)) {
                pstmt.setInt(1, transfer.getToAccountId());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        boolean isActive = rs.getBoolean("is_active");
                        if (!isActive) {
                            logger.warn("Destination account {} is inactive", transfer.getToAccountId());
                            conn.rollback();
                            return false;
                        }
                    } else {
                        logger.warn("Destination account {} not found", transfer.getToAccountId());
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 3. Deduct from source account
            String deductQuery = "UPDATE Accounts SET balance = balance - ? WHERE account_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deductQuery)) {
                pstmt.setDouble(1, transfer.getAmount());
                pstmt.setInt(2, transfer.getFromAccountId());
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    logger.error("Failed to update source account balance");
                    conn.rollback();
                    return false;
                }
                logger.info("Deducted {} from account {}", transfer.getAmount(), transfer.getFromAccountId());
            }

            // 4. Add to destination account
            String addQuery = "UPDATE Accounts SET balance = balance + ? WHERE account_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(addQuery)) {
                pstmt.setDouble(1, transfer.getAmount());
                pstmt.setInt(2, transfer.getToAccountId());
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    logger.error("Failed to update destination account balance");
                    conn.rollback();
                    return false;
                }
                logger.info("Added {} to account {}", transfer.getAmount(), transfer.getToAccountId());
            }

            // 5. Create transfer record
            String transferQuery = "INSERT INTO Transfers (from_account_id, to_account_id, transfer_type, amount, status, created_at) " +
                    "VALUES (?, ?, ?, ?, 'completed', ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(transferQuery)) {
                pstmt.setInt(1, transfer.getFromAccountId());
                pstmt.setInt(2, transfer.getToAccountId());
                pstmt.setString(3, transfer.getTransferType());
                pstmt.setDouble(4, transfer.getAmount());
                pstmt.setObject(5, LocalDateTime.now());
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    logger.error("Failed to create transfer record");
                    conn.rollback();
                    return false;
                }
                logger.info("Created transfer record successfully");
            }

            conn.commit();
            logger.info("Transfer completed successfully");
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                logger.error("Error rolling back transaction: {}", ex.getMessage());
            }
            logger.error("Error processing transfer: {}", e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error("Error closing connection: {}", e.getMessage());
            }
        }
    }
    public List<Transfer> getAllTransfers() {
        List<Transfer> transfers = new ArrayList<>();
        String query = "SELECT * FROM Transfers ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Transfer transfer = new Transfer();
                transfer.setTransferId(rs.getInt("transfer_id"));
                transfer.setFromAccountId(rs.getInt("from_account_id"));
                transfer.setToAccountId(rs.getInt("to_account_id"));
                transfer.setTransferType(rs.getString("transfer_type"));
                transfer.setAmount(rs.getDouble("amount"));

                Timestamp scheduledDate = rs.getTimestamp("scheduled_date");
                if (scheduledDate != null) {
                    transfer.setScheduledDate(scheduledDate.toLocalDateTime());
                }

                transfer.setStatus(rs.getString("status"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    transfer.setCreatedAt(createdAt.toLocalDateTime());
                } else {
                    transfer.setCreatedAt(LocalDateTime.now());
                }

                transfers.add(transfer);
            }

            logger.info("Retrieved {} total transfers", transfers.size());

        } catch (SQLException e) {
            logger.error("Error retrieving all transfers: {}", e.getMessage());
            e.printStackTrace();
        }

        return transfers;
    }


    public List<Transfer> getTransfersByAccountId(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String query = "SELECT * FROM Transfers WHERE from_account_id = ? OR to_account_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, accountId);
            pstmt.setInt(2, accountId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transfer transfer = new Transfer();
                    transfer.setTransferId(rs.getInt("transfer_id"));
                    transfer.setFromAccountId(rs.getInt("from_account_id"));
                    transfer.setToAccountId(rs.getInt("to_account_id"));
                    transfer.setTransferType(rs.getString("transfer_type"));
                    transfer.setAmount(rs.getDouble("amount"));
                    transfer.setScheduledDate(rs.getTimestamp("scheduled_date").toLocalDateTime());
                    transfer.setStatus(rs.getString("status"));
                    transfer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    transfers.add(transfer);
                }
            }
            logger.info("Retrieved {} transfers for account {}", transfers.size(), accountId);
        } catch (SQLException e) {
            logger.error("Error retrieving transfers for account {}: {}", accountId, e.getMessage());
            e.printStackTrace();
        }
        return transfers;
    }

    public List<Transfer> getTransfersByUserId(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String query = "SELECT t.* FROM Transfers t " +
                "JOIN Accounts a1 ON t.from_account_id = a1.account_id " +
                "JOIN Accounts a2 ON t.to_account_id = a2.account_id " +
                "WHERE a1.user_id = ? OR a2.user_id = ? " +
                "ORDER BY t.created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);

            logger.info("Executing query to get transfers for user: {}", userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transfer transfer = new Transfer();
                    transfer.setTransferId(rs.getInt("transfer_id"));
                    transfer.setFromAccountId(rs.getInt("from_account_id"));
                    transfer.setToAccountId(rs.getInt("to_account_id"));
                    transfer.setTransferType(rs.getString("transfer_type"));
                    transfer.setAmount(rs.getDouble("amount"));

                    // Handle null timestamps safely
                    Timestamp scheduledDate = rs.getTimestamp("scheduled_date");
                    if (scheduledDate != null) {
                        transfer.setScheduledDate(scheduledDate.toLocalDateTime());
                    }

                    transfer.setStatus(rs.getString("status"));

                    // Handle null created_at timestamp
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        transfer.setCreatedAt(createdAt.toLocalDateTime());
                    } else {
                        // Set current time if created_at is null
                        transfer.setCreatedAt(LocalDateTime.now());
                    }

                    transfers.add(transfer);
                    logger.debug("Found transfer: ID={}, Amount={}, Status={}",
                            transfer.getTransferId(),
                            transfer.getAmount(),
                            transfer.getStatus());
                }
            }
            logger.info("Retrieved {} transfers for user {}", transfers.size(), userId);
        } catch (SQLException e) {
            logger.error("Error retrieving transfers for user {}: {}", userId, e.getMessage());
            e.printStackTrace();
        }
        return transfers;
    }
}