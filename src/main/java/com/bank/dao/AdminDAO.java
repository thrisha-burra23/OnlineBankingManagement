package com.bank.dao;

import com.bank.model.Admin;
import com.bank.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {
    public Admin authenticate(String username, String password) {
        String query = "SELECT * FROM admins WHERE username = ? AND password_hash = ? AND status = 'active'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password); // In a real application, you should hash the password

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setAdminId(rs.getInt("admin_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setFullName(rs.getString("full_name"));
                    admin.setEmail(rs.getString("email"));
                    admin.setPhone(rs.getString("phone"));
                    admin.setStatus(rs.getString("status"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}