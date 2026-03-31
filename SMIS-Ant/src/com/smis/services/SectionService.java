package com.smis.services;

import com.smis.database.DatabaseConnection;
import com.smis.models.Section;
import com.smis.models.Student;

import java.sql.*;
import java.util.*;

public class SectionService {

    public static List<Section> getAllSections() {
        List<Section> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM sections ORDER BY section_name")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[SectionService] " + e.getMessage()); }
        return list;
    }

    public static Section getSectionById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM sections WHERE id=?")) {
            ps.setInt(1, id); ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.err.println("[SectionService] " + e.getMessage()); }
        return null;
    }

    public static Map<String, Integer> countStudentsByYearLevel() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT year_level, COUNT(*) as cnt FROM students GROUP BY year_level ORDER BY year_level";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("year_level"), rs.getInt("cnt"));
        } catch (SQLException e) { System.err.println("[SectionService] " + e.getMessage()); }
        return map;
    }

    public static Map<String, Integer> countSectionsByYearLevel() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT year_level, COUNT(*) as cnt FROM sections GROUP BY year_level ORDER BY year_level";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("year_level"), rs.getInt("cnt"));
        } catch (SQLException e) { System.err.println("[SectionService] " + e.getMessage()); }
        return map;
    }

    /** Get students whose section field matches this section's name */
    public static List<Student> getStudentsBySection(int sectionId) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, u.full_name, u.username, u.email, u.contact, u.is_active " +
                     "FROM students s JOIN users u ON s.user_id = u.id " +
                     "WHERE s.section = (SELECT section_name FROM sections WHERE id=?) " +
                     "ORDER BY u.full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId); ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Student st = new Student();
                st.setId(rs.getInt("id")); st.setUserId(rs.getInt("user_id"));
                st.setStudentId(rs.getString("student_id")); st.setYearLevel(rs.getString("year_level"));
                st.setCourse(rs.getString("course")); st.setSection(rs.getString("section"));
                st.setGuardianName(rs.getString("guardian_name")); st.setGuardianContact(rs.getString("guardian_contact"));
                st.setAddress(rs.getString("address")); st.setFullName(rs.getString("full_name"));
                st.setEmail(rs.getString("email")); st.setContact(rs.getString("contact"));
                st.setActive(rs.getInt("is_active") == 1);
                list.add(st);
            }
        } catch (SQLException e) { System.err.println("[SectionService] " + e.getMessage()); }
        return list;
    }

    public static boolean createSection(String name, String yearLevel, String course,
                                         String schoolYear, String semester) {
        String sql = "INSERT INTO sections (section_name,year_level,course,school_year,semester) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,name); ps.setString(2,yearLevel); ps.setString(3,course);
            ps.setString(4,schoolYear); ps.setString(5,semester);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean deleteSection(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM sections WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private static Section map(ResultSet rs) throws SQLException {
        Section s = new Section();
        s.setId(rs.getInt("id")); s.setSectionName(rs.getString("section_name"));
        s.setYearLevel(rs.getString("year_level")); s.setCourse(rs.getString("course"));
        s.setSchoolYear(rs.getString("school_year")); s.setSemester(rs.getString("semester"));
        return s;
    }
}
