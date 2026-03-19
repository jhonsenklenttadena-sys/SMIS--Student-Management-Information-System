package com.smis.services;

import com.smis.database.DatabaseConnection;
import com.smis.models.Attendance;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceService {

    public static List<Attendance> getAttendanceByEnrollment(int enrollmentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name as student_name, st.student_id as student_id_no, s.subject_code " +
                     "FROM attendance a JOIN enrollments e ON a.enrollment_id = e.id " +
                     "JOIN students st ON e.student_id = st.id JOIN users u ON st.user_id = u.id " +
                     "JOIN faculty_assignments fa ON e.assignment_id = fa.id " +
                     "JOIN subjects s ON fa.subject_id = s.id WHERE a.enrollment_id=? ORDER BY a.attendance_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[AttendanceService] " + e.getMessage()); }
        return list;
    }

    public static List<Attendance> getAttendanceByStudent(int studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name as student_name, st.student_id as student_id_no, s.subject_code " +
                     "FROM attendance a JOIN enrollments e ON a.enrollment_id = e.id " +
                     "JOIN students st ON e.student_id = st.id JOIN users u ON st.user_id = u.id " +
                     "JOIN faculty_assignments fa ON e.assignment_id = fa.id " +
                     "JOIN subjects s ON fa.subject_id = s.id WHERE e.student_id=? ORDER BY a.attendance_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[AttendanceService] " + e.getMessage()); }
        return list;
    }

    public static boolean markAttendance(int enrollmentId, LocalDate date, String status, String remarks) {
        // SQLite uses INSERT OR REPLACE instead of ON DUPLICATE KEY UPDATE
        String sql = "INSERT OR REPLACE INTO attendance (enrollment_id, attendance_date, status, remarks) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setString(2, date.toString());
            ps.setString(3, status);
            ps.setString(4, remarks);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("[AttendanceService] Mark: " + e.getMessage()); return false; }
    }

    public static double getAttendancePercentage(int enrollmentId) {
        String sql = "SELECT COUNT(*) as total, " +
                     "SUM(CASE WHEN status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) as present " +
                     "FROM attendance WHERE enrollment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                int present = rs.getInt("present");
                return total > 0 ? (present * 100.0 / total) : 0;
            }
        } catch (SQLException e) { System.err.println("[AttendanceService] " + e.getMessage()); }
        return 0;
    }

    private static Attendance map(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("id"));
        a.setEnrollmentId(rs.getInt("enrollment_id"));
        String dateStr = rs.getString("attendance_date");
        if (dateStr != null) a.setAttendanceDate(LocalDate.parse(dateStr.substring(0, 10)));
        a.setStatus(rs.getString("status"));
        a.setRemarks(rs.getString("remarks"));
        a.setStudentName(rs.getString("student_name"));
        a.setStudentIdNo(rs.getString("student_id_no"));
        a.setSubjectCode(rs.getString("subject_code"));
        return a;
    }
}
