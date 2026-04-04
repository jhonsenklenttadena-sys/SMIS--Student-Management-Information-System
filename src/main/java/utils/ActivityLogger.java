package utils;

import database.DatabaseManager;
import java.sql.*;

public class ActivityLogger {
    private static int currentUserId = -1;

    // Start a session when user logs in
    public static void startSession(int userId) {
        currentUserId = userId;
        logAction("Logged in", null);
    }

    // End a session when user logs out
    public static void endSession() {
        if (currentUserId <= 0) return;
        logAction("Logged out", null);
        currentUserId = -1;
    }

    // Log any action during the session
    public static void logAction(String action, String detail) {
        if (currentUserId <= 0) return;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO activity_logs (user_id, action, detail) VALUES (?,?,?)")) {
            ps.setInt(1, currentUserId);
            ps.setString(2, action);
            ps.setString(3, detail);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ActivityLogger] Error logging action: " + e.getMessage());
        }
    }
}