package com.bank.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseMigration {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigration.class);

    public static void updateLoanTable() {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Read the SQL script
            InputStream is = DatabaseMigration.class.getResourceAsStream("/sql/alter_loan_table.sql");
            if (is == null) {
                logger.error("SQL script not found");
                return;
            }

            String sql = new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .collect(Collectors.joining("\n"));

            // Execute the SQL script
            stmt.execute(sql);
            logger.info("Successfully updated loan table with remaining_balance and total_paid columns");

        } catch (Exception e) {
            logger.error("Error updating loan table: {}", e.getMessage());
        }
    }
} 