package com.bank.dao;

import com.bank.model.Loan;
//import com.bank.util.DBUtil;
import com.bank.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    private static final Logger logger = LoggerFactory.getLogger(LoanDAO.class);

    public boolean createLoan(Loan loan) {
        String sql = "INSERT INTO loans (user_id, loan_amount, interest_rate, status, start_date, end_date, remaining_balance, amount_paid) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, loan.getUserId());
            pstmt.setDouble(2, loan.getLoanAmount());
            pstmt.setDouble(3, loan.getInterestRate());
            pstmt.setString(4, loan.getStatus());
            pstmt.setTimestamp(5, Timestamp.valueOf(loan.getStartDate()));
            pstmt.setTimestamp(6, Timestamp.valueOf(loan.getEndDate()));
            pstmt.setDouble(7, loan.getRemainingBalance());
            pstmt.setDouble(8, loan.getTotalPaid());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Loan> getLoansByUserId(int userId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("loan_id"));
                loan.setUserId(rs.getInt("user_id"));
                loan.setLoanAmount(rs.getDouble("loan_amount"));
                loan.setInterestRate(rs.getDouble("interest_rate"));
                loan.setStatus(rs.getString("status"));
                loan.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
                loan.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
                loan.setRemainingBalance(rs.getDouble("remaining_balance"));
                loan.setTotalPaid(rs.getDouble("amount_paid"));
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public boolean updateLoanStatus(int loanId, String status) {
        String query = "UPDATE loans SET status = ? WHERE loan_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, loanId);
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Updated loan {} status to {}: {} rows affected", loanId, status, rowsAffected);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating loan {} status: {}", loanId, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Loan getLoanById(int loanId) {
        String query = "SELECT * FROM loans WHERE loan_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, loanId);
            logger.info("Executing query to get loan: {}", loanId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Loan loan = new Loan();
                    loan.setLoanId(rs.getInt("loan_id"));
                    loan.setUserId(rs.getInt("user_id"));
                    loan.setLoanAmount(rs.getDouble("loan_amount"));
                    loan.setInterestRate(rs.getDouble("interest_rate"));
                    loan.setStatus(rs.getString("status"));
                    loan.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
                    loan.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
                    loan.setRemainingBalance(rs.getDouble("remaining_balance"));
                    loan.setTotalPaid(rs.getDouble("amount_paid"));
                    logger.info("Found loan: ID={}, Amount={}, Status={}", 
                        loan.getLoanId(), loan.getLoanAmount(), loan.getStatus());
                    return loan;
                }
            }
            logger.warn("No loan found with ID: {}", loanId);
        } catch (SQLException e) {
            logger.error("Error retrieving loan {}: {}", loanId, e.getMessage());
            e.printStackTrace();
        }
        return null;
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
                loan.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
                loan.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
                loan.setRemainingBalance(rs.getDouble("remaining_balance"));
                loan.setTotalPaid(rs.getDouble("amount_paid"));
                loans.add(loan);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loans;
    }

    public List<Loan> getPendingLoans() {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans WHERE status = 'PENDING' ORDER BY start_date DESC";
        
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
                loan.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
                loan.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
                loan.setRemainingBalance(rs.getDouble("remaining_balance"));
                loan.setTotalPaid(rs.getDouble("amount_paid"));
                loans.add(loan);
            }
            
        } catch (SQLException e) {
            logger.error("Error retrieving pending loans: {}", e.getMessage());
            e.printStackTrace();
        }
        
        return loans;
    }

    public List<Loan> getActiveLoansByUserId(int userId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE user_id = ? AND status = 'APPROVED' AND remaining_balance > 0 ORDER BY end_date ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("loan_id"));
                loan.setUserId(rs.getInt("user_id"));
                loan.setLoanAmount(rs.getDouble("loan_amount"));
                loan.setInterestRate(rs.getDouble("interest_rate"));
                loan.setStatus(rs.getString("status"));
                loan.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
                loan.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
                loan.setRemainingBalance(rs.getDouble("remaining_balance"));
                loan.setTotalPaid(rs.getDouble("amount_paid"));
                loans.add(loan);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving active loans for user {}: {}", userId, e.getMessage());
            e.printStackTrace();
        }
        return loans;
    }

    public boolean processLoanPayment(int loanId, int accountId, double amount) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Get loan details
            Loan loan = getLoanById(loanId);
            if (loan == null) {
                logger.error("Loan not found: {}", loanId);
                return false;
            }

            // 2. Check if payment amount is valid
            if (amount <= 0 || amount > loan.getRemainingBalance()) {
                logger.error("Invalid payment amount: {} for loan: {}", amount, loanId);
                return false;
            }

            // 3. Update loan balance and amount paid
            String updateLoanSql = "UPDATE loans SET remaining_balance = remaining_balance - ?, amount_paid = amount_paid + ? WHERE loan_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateLoanSql)) {
                pstmt.setDouble(1, amount);
                pstmt.setDouble(2, amount);
                pstmt.setInt(3, loanId);
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated != 1) {
                    throw new SQLException("Failed to update loan balance and amount paid");
                }
                logger.info("Updated loan {}: remaining_balance decreased by {}, amount_paid increased by {}", 
                    loanId, amount, amount);
            }

            // 4. Deduct from account
            String updateAccountSql = "UPDATE bank_accounts SET balance = balance - ? WHERE account_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateAccountSql)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, accountId);
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated != 1) {
                    throw new SQLException("Failed to update account balance");
                }
                logger.info("Deducted {} from account {}", amount, accountId);
            }

            conn.commit();
            logger.info("Successfully processed loan payment: loanId={}, accountId={}, amount={}", loanId, accountId, amount);
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.error("Transaction rolled back due to error: {}", e.getMessage());
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            logger.error("Error processing loan payment: {}", e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection", e);
                }
            }
        }
    }
}