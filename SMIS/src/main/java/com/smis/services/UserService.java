package com.smis.services;

import com.smis.database.DatabaseConnection;
import com.smis.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    public static List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY role, full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[UserService] " + e.getMessage()); }
        return list;
    }

    public static List<User> getUsersByRole(String role) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role=? ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[UserService] " + e.getMessage()); }
        return list;
    }

    public static User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.err.println("[UserService] " + e.getMessage()); }
        return null;
    }

    public static List<User> searchUsers(String query) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE full_name LIKE ? OR username LIKE ? ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String q = "%" + query + "%";
            ps.setString(1, q); ps.setString(2, q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[UserService] " + e.getMessage()); }
        return list;
    }

    public static int createUser(String username, String password, String role,
                                  String fullName, String email, String contact) {
        String sql = "INSERT INTO users (username, password, role, full_name, email, contact) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, AuthService.hashPassword(password));
            ps.setString(3, role);
            ps.setString(4, fullName);
            ps.setString(5, email);
            ps.setString(6, contact);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) { System.err.println("[UserService] Create: " + e.getMessage()); }
        return -1;
    }

    public static boolean updateUser(int id, String fullName, String email, String contact) {
        String sql = "UPDATE users SET full_name=?, email=?, contact=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName); ps.setString(2, email);
            ps.setString(3, contact); ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static int countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role=? AND is_active=1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("[UserService] " + e.getMessage()); }
        return 0;
    }

    private static User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setRole(User.Role.valueOf(rs.getString("role")));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setContact(rs.getString("contact"));
        u.setActive(rs.getInt("is_active") == 1);
        return u;
    }
}
