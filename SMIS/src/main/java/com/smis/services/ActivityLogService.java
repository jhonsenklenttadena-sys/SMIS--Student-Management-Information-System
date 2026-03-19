package com.smis.services;

import com.smis.database.DatabaseConnection;
import com.smis.models.ActivityLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogService {

    public static void log(int userId, String action, String details) {
        String sql = "INSERT INTO activity_logs (user_id, action, details) VALUES (?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setString(2, action); ps.setString(3, details);
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("[ActivityLog] " + e.getMessage()); }
    }

    public static List<ActivityLog> getRecentLogs(int limit) {
        List<ActivityLog> list = new ArrayList<>();
        String sql = "SELECT al.*, u.username FROM activity_logs al " +
                     "JOIN users u ON al.user_id = u.id ORDER BY al.logged_at DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setId(rs.getInt("id"));
                log.setUserId(rs.getInt("user_id"));
                log.setUsername(rs.getString("username"));
                log.setAction(rs.getString("action"));
                log.setDetails(rs.getString("details"));
                log.setLoggedAt(rs.getString("logged_at"));
                list.add(log);
            }
        } catch (SQLException e) { System.err.println("[ActivityLog] " + e.getMessage()); }
        return list;
    }
}
