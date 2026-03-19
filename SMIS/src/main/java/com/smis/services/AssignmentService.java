package com.smis.services;

import com.smis.database.DatabaseConnection;
import com.smis.models.FacultyAssignment;
import com.smis.models.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentService {

    private static final String JOIN =
        "SELECT fa.*, u.full_name as faculty_name, s.subject_code, s.subject_name, sec.section_name " +
        "FROM faculty_assignments fa " +
        "JOIN faculty f ON fa.faculty_id = f.id " +
        "JOIN users u ON f.user_id = u.id " +
        "JOIN subjects s ON fa.subject_id = s.id " +
        "JOIN sections sec ON fa.section_id = sec.id ";

    public static List<FacultyAssignment> getAllAssignments() {
        List<FacultyAssignment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(JOIN + "ORDER BY u.full_name")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[AssignmentService] " + e.getMessage()); }
        return list;
    }

    public static List<FacultyAssignment> getAssignmentsByFaculty(int facultyId) {
        List<FacultyAssignment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOIN + "WHERE fa.faculty_id=?")) {
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[AssignmentService] " + e.getMessage()); }
        return list;
    }

    public static List<FacultyAssignment> getAssignmentsBySection(int sectionId) {
        List<FacultyAssignment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOIN + "WHERE fa.section_id=?")) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[AssignmentService] " + e.getMessage()); }
        return list;
    }

    /** Get subjects already assigned to a section (for the assignment dialog) */
    public static List<Subject> getSubjectsBySection(int sectionId) {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT DISTINCT s.* FROM subjects s " +
                     "JOIN faculty_assignments fa ON s.id = fa.subject_id " +
                     "WHERE fa.section_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Subject s = new Subject();
                s.setId(rs.getInt("id"));
                s.setSubjectCode(rs.getString("subject_code"));
                s.setSubjectName(rs.getString("subject_name"));
                s.setUnits(rs.getInt("units"));
                list.add(s);
            }
        } catch (SQLException e) { System.err.println("[AssignmentService] getSubjectsBySection: " + e.getMessage()); }
        return list;
    }

    /** Get a specific assignment by section + subject */
    public static FacultyAssignment getAssignment(int sectionId, int subjectId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOIN + "WHERE fa.section_id=? AND fa.subject_id=?")) {
            ps.setInt(1, sectionId); ps.setInt(2, subjectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.err.println("[AssignmentService] " + e.getMessage()); }
        return null;
    }

    public static boolean createAssignment(int facultyId, int subjectId, int sectionId,
                                            String schedule, String room,
                                            String schoolYear, String semester) {
        String sql = "INSERT INTO faculty_assignments " +
                     "(faculty_id,subject_id,section_id,schedule,room,school_year,semester) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facultyId); ps.setInt(2, subjectId); ps.setInt(3, sectionId);
            ps.setString(4, schedule); ps.setString(5, room);
            ps.setString(6, schoolYear); ps.setString(7, semester);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AssignmentService] Create: " + e.getMessage()); return false;
        }
    }

    public static boolean deleteAssignment(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM faculty_assignments WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private static FacultyAssignment map(ResultSet rs) throws SQLException {
        FacultyAssignment fa = new FacultyAssignment();
        fa.setId(rs.getInt("id")); fa.setFacultyId(rs.getInt("faculty_id"));
        fa.setSubjectId(rs.getInt("subject_id")); fa.setSectionId(rs.getInt("section_id"));
        fa.setSchedule(rs.getString("schedule")); fa.setRoom(rs.getString("room"));
        fa.setSchoolYear(rs.getString("school_year")); fa.setSemester(rs.getString("semester"));
        fa.setFacultyName(rs.getString("faculty_name")); fa.setSubjectCode(rs.getString("subject_code"));
        fa.setSubjectName(rs.getString("subject_name")); fa.setSectionName(rs.getString("section_name"));
        return fa;
    }
}
