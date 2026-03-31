package com.smis.services;

import com.smis.database.DatabaseConnection;
import com.smis.models.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentService {

    private static final String JOIN =
        "SELECT e.*, u.full_name as student_name, st.student_id as student_id_no, " +
        "s.subject_code, s.subject_name, sec.section_name, uf.full_name as faculty_name, fa.schedule " +
        "FROM enrollments e " +
        "JOIN students st ON e.student_id = st.id " +
        "JOIN users u ON st.user_id = u.id " +
        "JOIN faculty_assignments fa ON e.assignment_id = fa.id " +
        "JOIN subjects s ON fa.subject_id = s.id " +
        "JOIN sections sec ON fa.section_id = sec.id " +
        "JOIN faculty f ON fa.faculty_id = f.id " +
        "JOIN users uf ON f.user_id = uf.id ";

    public static List<Enrollment> getEnrollmentsByStudent(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOIN + "WHERE e.student_id=? AND e.status='ENROLLED'")) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[EnrollmentService] getByStudent: " + e.getMessage()); }
        return list;
    }

    public static List<Enrollment> getEnrollmentsByAssignment(int assignmentId) {
        List<Enrollment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOIN + "WHERE e.assignment_id=? AND e.status='ENROLLED'")) {
            ps.setInt(1, assignmentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[EnrollmentService] getByAssignment: " + e.getMessage()); }
        return list;
    }

    /**
     * Get all students enrolled in a section (deduplicated by student).
     * Used by faculty to see their complete student list per section.
     */
    public static List<Enrollment> getStudentsBySection(int sectionId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = JOIN +
            "WHERE fa.section_id=? AND e.status='ENROLLED' " +
            "GROUP BY e.student_id ORDER BY u.full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[EnrollmentService] getStudentsBySection: " + e.getMessage()); }
        return list;
    }

    /**
     * Auto-enroll a student into ALL subjects of a given section.
     * Returns how many subjects they were enrolled in.
     */
    public static int autoEnrollInSection(int studentId, int sectionId) {
        // Get all faculty_assignments for this section
        String sql = "SELECT id FROM faculty_assignments WHERE section_id=?";
        int count = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int assignmentId = rs.getInt("id");
                if (enrollStudent(studentId, assignmentId)) count++;
            }
        } catch (SQLException e) { System.err.println("[EnrollmentService] autoEnroll: " + e.getMessage()); }
        return count;
    }

    /** Enroll a student into one specific assignment, creating grade record too. */
    public static boolean enrollStudent(int studentId, int assignmentId) {
        String sql = "INSERT OR IGNORE INTO enrollments (student_id, assignment_id, status) VALUES (?,?,'ENROLLED')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId); ps.setInt(2, assignmentId);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                GradeService.initGrade(keys.getInt(1));
                return true;
            }
            // Already enrolled (INSERT OR IGNORE skipped it) — still ok
            return true;
        } catch (SQLException e) {
            System.err.println("[EnrollmentService] enroll: " + e.getMessage()); return false;
        }
    }

    public static Enrollment getEnrollmentById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOIN + "WHERE e.id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.err.println("[EnrollmentService] " + e.getMessage()); }
        return null;
    }

    private static Enrollment map(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setId(rs.getInt("id")); e.setStudentId(rs.getInt("student_id"));
        e.setAssignmentId(rs.getInt("assignment_id")); e.setStatus(rs.getString("status"));
        e.setStudentName(rs.getString("student_name")); e.setStudentIdNo(rs.getString("student_id_no"));
        e.setSubjectCode(rs.getString("subject_code")); e.setSubjectName(rs.getString("subject_name"));
        e.setSectionName(rs.getString("section_name")); e.setFacultyName(rs.getString("faculty_name"));
        e.setSchedule(rs.getString("schedule"));
        return e;
    }
}
