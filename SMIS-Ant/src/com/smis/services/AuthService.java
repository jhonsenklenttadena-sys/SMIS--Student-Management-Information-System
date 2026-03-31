package com.smis.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.smis.database.DatabaseConnection;
import com.smis.models.User;

import java.sql.*;

public class AuthService {

    public static User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashed = rs.getString("password");
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashed);
                if (result.verified) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(User.Role.valueOf(rs.getString("role")));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setContact(rs.getString("contact"));
                    user.setActive(rs.getBoolean("is_active"));
                    ActivityLogService.log(user.getId(), "LOGIN", "User logged in.");
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("[AuthService] " + e.getMessage());
        }
        return null;
    }

    public static String hashPassword(String plain) {
        return BCrypt.withDefaults().hashToString(12, plain.toCharArray());
    }

    public static boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashPassword(newPassword));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean usernameExists(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }
}
