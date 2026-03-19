package com.smis.services;

import com.smis.database.DatabaseConnection;
import com.smis.models.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentService {

    private static final String JOIN = "SELECT s.*, u.full_name, u.username, u.email, u.contact, u.is_active " +
                     "FROM students s JOIN users u ON s.user_id = u.id ";

    public static List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(JOIN + "ORDER BY u.full_name")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[StudentService] " + e.getMessage()); }
        return list;
    }

    public static Student getStudentByUserId(int userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOIN + "WHERE s.user_id=?")) {
            ps.setInt(1, userId); ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.err.println("[StudentService] " + e.getMessage()); }
        return null;
    }

    public static Student getStudentById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOIN + "WHERE s.id=?")) {
            ps.setInt(1, id); ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.err.println("[StudentService] " + e.getMessage()); }
        return null;
    }

    public static List<Student> searchStudents(String query) {
        List<Student> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOIN +
                     "WHERE u.full_name LIKE ? OR s.student_id LIKE ? OR s.section LIKE ?")) {
            String q = "%" + query + "%";
            ps.setString(1, q); ps.setString(2, q); ps.setString(3, q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[StudentService] " + e.getMessage()); }
        return list;
    }

    public static boolean createStudent(int userId, String studentId, String yearLevel,
                                         String course, String section, String guardianName,
                                         String guardianContact, String address) {
        String sql = "INSERT INTO students (user_id,student_id,year_level,course,section,guardian_name,guardian_contact,address) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,userId); ps.setString(2,studentId); ps.setString(3,yearLevel);
            ps.setString(4,course); ps.setString(5,section); ps.setString(6,guardianName);
            ps.setString(7,guardianContact); ps.setString(8,address);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("[StudentService] " + e.getMessage()); return false; }
    }

    public static boolean updateStudent(int id, String yearLevel, String course, String section,
                                         String guardianName, String guardianContact, String address) {
        String sql = "UPDATE students SET year_level=?,course=?,section=?,guardian_name=?,guardian_contact=?,address=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,yearLevel); ps.setString(2,course); ps.setString(3,section);
            ps.setString(4,guardianName); ps.setString(5,guardianContact);
            ps.setString(6,address); ps.setInt(7,id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private static Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id")); s.setUserId(rs.getInt("user_id"));
        s.setStudentId(rs.getString("student_id")); s.setYearLevel(rs.getString("year_level"));
        s.setCourse(rs.getString("course")); s.setSection(rs.getString("section"));
        s.setGuardianName(rs.getString("guardian_name")); s.setGuardianContact(rs.getString("guardian_contact"));
        s.setAddress(rs.getString("address")); s.setFullName(rs.getString("full_name"));
        s.setUsername(rs.getString("username")); s.setEmail(rs.getString("email"));
        s.setContact(rs.getString("contact")); s.setActive(rs.getInt("is_active") == 1);
        return s;
    }
}
