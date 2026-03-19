package com.smis.services;

import com.smis.database.DatabaseConnection;
import com.smis.models.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeService {

    public static void initGrade(int enrollmentId) {
        String sql = "INSERT OR IGNORE INTO grades (enrollment_id) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId); ps.executeUpdate();
        } catch (SQLException e) { System.err.println("[GradeService] Init: " + e.getMessage()); }
    }

    public static Grade getGradeByEnrollment(int enrollmentId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(BASE + "WHERE g.enrollment_id=?")) {
            ps.setInt(1, enrollmentId); ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.err.println("[GradeService] " + e.getMessage()); }
        return null;
    }

    public static List<Grade> getGradesByStudent(int studentId) {
        List<Grade> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(BASE + "WHERE e.student_id=?")) {
            ps.setInt(1, studentId); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[GradeService] " + e.getMessage()); }
        return list;
    }

    public static List<Grade> getGradesByAssignment(int assignmentId) {
        List<Grade> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(BASE + "WHERE e.assignment_id=?")) {
            ps.setInt(1, assignmentId); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[GradeService] " + e.getMessage()); }
        return list;
    }

    public static boolean updateGrade(int enrollmentId, Double prelim, Double midterm,
                                       Double prefinal, Double finalGrade, String performanceNotes) {
        double sum = 0; int count = 0;
        if (prelim     != null) { sum += prelim;     count++; }
        if (midterm    != null) { sum += midterm;    count++; }
        if (prefinal   != null) { sum += prefinal;   count++; }
        if (finalGrade != null) { sum += finalGrade; count++; }
        Double computed = count > 0 ? Math.round((sum / count) * 100.0) / 100.0 : null;
        String autoRemark = computed != null ? (computed >= 75 ? "PASSED" : "FAILED") : "IN PROGRESS";

        String sql = "UPDATE grades SET prelim=?,midterm=?,prefinal=?,final_grade=?," +
                     "computed_grade=?,remarks=?,performance_notes=? WHERE enrollment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setD(ps,1,prelim); setD(ps,2,midterm); setD(ps,3,prefinal);
            setD(ps,4,finalGrade); setD(ps,5,computed);
            ps.setString(6, autoRemark);
            if (performanceNotes != null) ps.setString(7, performanceNotes); else ps.setNull(7, Types.VARCHAR);
            ps.setInt(8, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("[GradeService] Update: " + e.getMessage()); return false; }
    }

    private static final String BASE =
        "SELECT g.*, u.full_name as student_name, st.student_id as student_id_no, " +
        "s.subject_code, s.subject_name FROM grades g " +
        "JOIN enrollments e ON g.enrollment_id = e.id " +
        "JOIN students st ON e.student_id = st.id " +
        "JOIN users u ON st.user_id = u.id " +
        "JOIN faculty_assignments fa ON e.assignment_id = fa.id " +
        "JOIN subjects s ON fa.subject_id = s.id ";

    private static void setD(PreparedStatement ps, int i, Double v) throws SQLException {
        if (v != null) ps.setDouble(i, v); else ps.setNull(i, Types.REAL);
    }

    private static Grade map(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setId(rs.getInt("id"));
        g.setEnrollmentId(rs.getInt("enrollment_id"));
        double p = rs.getDouble("prelim");      if (!rs.wasNull()) g.setPrelim(p);
        double m = rs.getDouble("midterm");     if (!rs.wasNull()) g.setMidterm(m);
        double pf = rs.getDouble("prefinal");   if (!rs.wasNull()) g.setPrefinal(pf);
        double f = rs.getDouble("final_grade"); if (!rs.wasNull()) g.setFinalGrade(f);
        double c = rs.getDouble("computed_grade"); if (!rs.wasNull()) g.setComputedGrade(c);
        g.setRemarks(rs.getString("remarks"));
        g.setPerformanceNotes(rs.getString("performance_notes"));
        g.setStudentName(rs.getString("student_name"));
        g.setStudentIdNo(rs.getString("student_id_no"));
        g.setSubjectCode(rs.getString("subject_code"));
        g.setSubjectName(rs.getString("subject_name"));
        return g;
    }
}
