package database;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

/**
 * Handles login authentication and session creation.
 */
public class AuthService {

    public enum LoginResult {
        SUCCESS_ADMIN,
        SUCCESS_FACULTY,
        SUCCESS_STUDENT,
        INVALID_CREDENTIALS,
        DB_ERROR
    }

    /**
     * Attempts to log in with the given username and password.
     * On success, populates the Session singleton and returns the matching result.
     */
    public static LoginResult login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return LoginResult.INVALID_CREDENTIALS;
        }

        try (Connection conn = DatabaseManager.getConnection()) {

            // 1. Look up the user
            String userSql = "SELECT id, password, role FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setString(1, username.trim());
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) return LoginResult.INVALID_CREDENTIALS;

                int    userId       = rs.getInt("id");
                String storedHash   = rs.getString("password");
                String role         = rs.getString("role");

                // 2. Verify password with bcrypt
                if (!BCrypt.checkpw(password, storedHash)) {
                    return LoginResult.INVALID_CREDENTIALS;
                }

                // 3. Load profile depending on role
                return switch (role) {
                    case "admin"   -> loadAdminSession(userId, username);
                    case "faculty" -> loadFacultySession(conn, userId, username);
                    case "student" -> loadStudentSession(conn, userId, username);
                    default        -> LoginResult.INVALID_CREDENTIALS;
                };
            }

        } catch (SQLException e) {
            System.err.println("[Auth] DB error: " + e.getMessage());
            return LoginResult.DB_ERROR;
        }
    }

    private static LoginResult loadAdminSession(int userId, String username) {
        Session.login(userId, username, "Admin", "User",
                      Session.Role.ADMIN, 0, null);
        utils.ActivityLogger.startSession(userId); 
        return LoginResult.SUCCESS_ADMIN;
    }

    private static LoginResult loadFacultySession(Connection conn, int userId, String username)
            throws SQLException {
        String sql = "SELECT id, first_name, last_name FROM faculty WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Session.login(userId, username,
                              rs.getString("first_name"), rs.getString("last_name"),
                              Session.Role.FACULTY, rs.getInt("id"), null);
            } else {
                // Faculty record missing — use username as fallback
                Session.login(userId, username, username, "", Session.Role.FACULTY, 0, null);
            }
        }
        utils.ActivityLogger.startSession(userId);
        return LoginResult.SUCCESS_FACULTY;
    }

    private static LoginResult loadStudentSession(Connection conn, int userId, String username)
            throws SQLException {
        String sql = "SELECT id, student_no, first_name, last_name FROM students WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Session.login(userId, username,
                              rs.getString("first_name"), rs.getString("last_name"),
                              Session.Role.STUDENT, rs.getInt("id"), rs.getString("student_no"));
            } else {
                Session.login(userId, username, username, "", Session.Role.STUDENT, 0, null);
            }
        }
        utils.ActivityLogger.startSession(userId);
        return LoginResult.SUCCESS_STUDENT;
    }
}
