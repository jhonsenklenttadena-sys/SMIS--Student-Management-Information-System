package dashboards;

import Login.LoginForm;
import database.DatabaseManager;
import database.Session;
import javax.swing.*;
import java.sql.*;
import utils.*;

public class StudentDashboard extends javax.swing.JFrame {

    private int studentUserId;   // field for student ID
    private boolean adminProxy = false;

    // ── Constructors ──────────────────────────────────────────────────────────

    public StudentDashboard() {
        initComponents();
        this.adminProxy = false;
        this.studentUserId = Session.getProfileId(); // set from Session
        loadUserData();
    }

    public StudentDashboard(int studentUserId, boolean isAdminProxy) {
        initComponents();
        this.studentUserId = studentUserId;
        this.adminProxy = isAdminProxy;
        if (isAdminProxy) {
            btnLogout.setText("Close (Admin)");
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        }
        loadUserDataForUser(studentUserId);
    }

    // ── Business logic ────────────────────────────────────────────────────────

    private void loadUserData() {
        lblName.setText(Session.getFullName());
        lblStudentNo.setText("Student No: " + (Session.getProfileNo() != null ? Session.getProfileNo() : "-"));

        int pid = Session.getProfileId();
        if (pid <= 0) return;

        loadStatsForStudentId(pid);
    }

    private void loadUserDataForUser(int userId) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT id, first_name || ' ' || last_name AS full_name, student_no " +
                 "FROM students WHERE user_id=?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lblName.setText(rs.getString("full_name") + "  [Admin View]");
                lblStudentNo.setText("Student No: " + rs.getString("student_no"));
                loadStatsForStudentId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("StudentDashboard loadUserDataForUser: " + e.getMessage());
        }
    }

    private void loadStatsForStudentId(int studentId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*), AVG(COALESCE(grade,0)) FROM enrollments WHERE student_id=?");
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lblEnrolled.setText(String.valueOf(rs.getInt(1)));
                double avg = rs.getDouble(2);
                lblGPA.setText(avg > 0 ? String.format("%.2f", avg) : "--");
            }

            ps = conn.prepareStatement(
                "SELECT COALESCE(sec.name,'--'), COALESCE(c.code,'--') FROM students s " +
                "LEFT JOIN sections sec ON sec.id=s.section_id " +
                "LEFT JOIN courses c ON c.id=s.course_id WHERE s.id=?");
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            if (rs.next()) {
                lblSection.setText(rs.getString(1));
                lblCourse.setText(rs.getString(2));
            }

            lblStudentId.setText(String.valueOf(studentId));

        } catch (SQLException e) {
            System.err.println("StudentDashboard loadStatsForStudentId: " + e.getMessage());
        }
    }

    // ── Generated Code ────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidebar = new panelDesigns.Panel_Sidebar();
        lblSmis = new javax.swing.JLabel();
        lblPortal = new javax.swing.JLabel();
        jSep1 = new javax.swing.JSeparator();
        btnDash = new color.Button_Student("");
        btnSubjects = new color.Button_Student("");
        btnGrades = new color.Button_Student("");
        btnAttend = new color.Button_Student("");
        btnAnnounce = new color.Button_Student("");
        btnProfile = new color.Button_Student("");
        btnLogout = new color.Button_Student("");
        jPanelMain = new javax.swing.JPanel();
        lblWelcome = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblStudentNo = new javax.swing.JLabel();
        jSep2 = new javax.swing.JSeparator();
        panelEnrolled = new javax.swing.JPanel();
        lblEnrolled = new javax.swing.JLabel();
        lblEnrLbl = new javax.swing.JLabel();
        panelGPA = new javax.swing.JPanel();
        lblGPA = new javax.swing.JLabel();
        lblGPALbl = new javax.swing.JLabel();
        panelSection = new javax.swing.JPanel();
        lblSection = new javax.swing.JLabel();
        lblSecLbl = new javax.swing.JLabel();
        panelCourse = new javax.swing.JPanel();
        lblCourse = new javax.swing.JLabel();
        lblCourseLbl = new javax.swing.JLabel();
        panelId = new javax.swing.JPanel();
        lblStudentId = new javax.swing.JLabel();
        lblIdLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sidebar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblSmis.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        lblSmis.setText("SMIS");
        sidebar.add(lblSmis, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, -1, -1));

        lblPortal.setText("Student Portal");
        sidebar.add(lblPortal, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 120, -1, -1));
        sidebar.add(jSep1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 150, 200, 10));

        btnDash.setText("Dashboard");
        btnDash.setBorderPainted(false);
        btnDash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashActionPerformed(evt);
            }
        });
        sidebar.add(btnDash, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 170, 210, 30));

        btnSubjects.setText("My Subjects");
        btnSubjects.setBorderPainted(false);
        btnSubjects.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubjectsActionPerformed(evt);
            }
        });
        sidebar.add(btnSubjects, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 208, 210, 30));

        btnGrades.setText("My Grades");
        btnGrades.setBorderPainted(false);
        btnGrades.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGradesActionPerformed(evt);
            }
        });
        sidebar.add(btnGrades, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 246, 210, 30));

        btnAttend.setText("My Attendance");
        btnAttend.setBorderPainted(false);
        btnAttend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttendActionPerformed(evt);
            }
        });
        sidebar.add(btnAttend, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 284, 210, 30));

        btnAnnounce.setText("Announcements");
        btnAnnounce.setBorderPainted(false);
        btnAnnounce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnnounceActionPerformed(evt);
            }
        });
        sidebar.add(btnAnnounce, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 322, 210, 30));

        btnProfile.setText("My Profile");
        btnProfile.setBorderPainted(false);
        btnProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfileActionPerformed(evt);
            }
        });
        sidebar.add(btnProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 360, 210, 30));

        btnLogout.setText("Log Out");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        sidebar.add(btnLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 580, -1, -1));

        getContentPane().add(sidebar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 390, 700));

        jPanelMain.setBackground(new java.awt.Color(232, 240, 254));
        jPanelMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblWelcome.setText("Welcome,");
        jPanelMain.add(lblWelcome, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        lblName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblName.setText("Student Name");
        jPanelMain.add(lblName, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 30, 300, -1));

        lblStudentNo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblStudentNo.setText("Student No: --");
        jPanelMain.add(lblStudentNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 58, -1, -1));

        jPanelMain.add(jSep2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 660, 10));

        panelEnrolled.setBackground(new java.awt.Color(21, 101, 192));
        panelEnrolled.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblEnrolled.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblEnrolled.setForeground(new java.awt.Color(255, 255, 255));
        lblEnrolled.setText("0");
        panelEnrolled.add(lblEnrolled, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, 150, -1));

        lblEnrLbl.setForeground(new java.awt.Color(255, 255, 255));
        lblEnrLbl.setText("Enrolled Subjects");
        panelEnrolled.add(lblEnrLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 62, -1, -1));

        jPanelMain.add(panelEnrolled, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 105, 175, 95));

        panelGPA.setBackground(new java.awt.Color(27, 94, 32));
        panelGPA.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblGPA.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblGPA.setForeground(new java.awt.Color(255, 255, 255));
        lblGPA.setText("--");
        panelGPA.add(lblGPA, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, 150, -1));

        lblGPALbl.setForeground(new java.awt.Color(255, 255, 255));
        lblGPALbl.setText("Average Grade");
        panelGPA.add(lblGPALbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 62, -1, -1));

        jPanelMain.add(panelGPA, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 105, 175, 95));

        panelSection.setBackground(new java.awt.Color(94, 53, 177));
        panelSection.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblSection.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblSection.setForeground(new java.awt.Color(255, 255, 255));
        lblSection.setText("--");
        panelSection.add(lblSection, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, 150, -1));

        lblSecLbl.setForeground(new java.awt.Color(255, 255, 255));
        lblSecLbl.setText("Section");
        panelSection.add(lblSecLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 62, -1, -1));

        jPanelMain.add(panelSection, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 105, 175, 95));

        panelCourse.setBackground(new java.awt.Color(21, 101, 192));
        panelCourse.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblCourse.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblCourse.setForeground(new java.awt.Color(255, 255, 255));
        lblCourse.setText("--");
        panelCourse.add(lblCourse, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, 150, -1));

        lblCourseLbl.setForeground(new java.awt.Color(255, 255, 255));
        lblCourseLbl.setText("Course");
        panelCourse.add(lblCourseLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 62, -1, -1));

        jPanelMain.add(panelCourse, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 225, 175, 95));

        panelId.setBackground(new java.awt.Color(78, 52, 46));
        panelId.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblStudentId.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblStudentId.setForeground(new java.awt.Color(255, 255, 255));
        lblStudentId.setText("--");
        panelId.add(lblStudentId, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, 150, -1));

        lblIdLbl.setForeground(new java.awt.Color(255, 255, 255));
        lblIdLbl.setText("Student ID");
        panelId.add(lblIdLbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 62, -1, -1));

        jPanelMain.add(panelId, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 225, 175, 95));

        getContentPane().add(jPanelMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 0, 700, 700));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addBtn(javax.swing.JPanel p, javax.swing.JButton b, String text, int x, int y, int w) {
        b.setText(text);
        p.add(b, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, 30));
    }

    private void makeCard(javax.swing.JPanel parent, javax.swing.JPanel card,
                          javax.swing.JLabel lbl, String title,
                          javax.swing.JLabel val, java.awt.Color bg, int x, int y) {
        card.setBackground(bg);
        card.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        lbl.setFont(new java.awt.Font("Segoe UI", 0, 11));
        lbl.setForeground(java.awt.Color.WHITE);
        lbl.setText(title);
        card.add(lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 62, -1, -1));
        val.setFont(new java.awt.Font("Segoe UI", 1, 22));
        val.setForeground(java.awt.Color.WHITE);
        val.setText("--");
        card.add(val, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, 150, -1));
        parent.add(card, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, 175, 95));
    }

    // ── Button handlers ───────────────────────────────────────────────────────

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {
        if (adminProxy) { dispose(); return; }
        Session.logout();
        dispose();
        JFrame frame = new JFrame("SMIS");
        frame.setSize(739, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new LoginForm());
        frame.setVisible(true);
        
        ActivityLogger.endSession();
        Session.logout();
    }//GEN-LAST:event_jButton1ActionPerformed


   // FIX: was empty TODO — now refreshes dashboard stats
private void btnDashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashActionPerformed
    loadUserData();
}//GEN-LAST:event_btnDashActionPerformed

// FIX: was empty TODO — now opens StudentSubjectsWindow
private void btnSubjectsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubjectsActionPerformed
    new windows.StudentSubjectsWindow(this.studentUserId).setVisible(true);
}//GEN-LAST:event_btnSubjectsActionPerformed

// FIX: was empty TODO — now opens StudentGradesWindow
private void btnGradesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGradesActionPerformed
    new windows.StudentGradesWindow(this.studentUserId).setVisible(true);
}//GEN-LAST:event_btnGradesActionPerformed

// FIX: was empty TODO — now opens StudentAttendanceWindow
private void btnAttendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttendActionPerformed
    new windows.StudentAttendanceWindow(this.studentUserId).setVisible(true);
}//GEN-LAST:event_btnAttendActionPerformed

// FIX: was empty TODO — now opens StudentAnnouncementsWindow
private void btnAnnounceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnnounceActionPerformed
    new windows.StudentAnnouncementsWindow(this.studentUserId).setVisible(true);
}//GEN-LAST:event_btnAnnounceActionPerformed

// FIX: was empty TODO — now opens StudentProfileWindow
private void btnProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfileActionPerformed
    new windows.StudentProfileWindow(this.studentUserId).setVisible(true);
}//GEN-LAST:event_btnProfileActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(StudentDashboard.class.getName())
                .log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new StudentDashboard().setVisible(true));
    }

     // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnnounce;
    private javax.swing.JButton btnAttend;
    private javax.swing.JButton btnDash;
    private javax.swing.JButton btnGrades;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProfile;
    private javax.swing.JButton btnSubjects;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JSeparator jSep1;
    private javax.swing.JSeparator jSep2;
    private javax.swing.JLabel lblCourse;
    private javax.swing.JLabel lblCourseLbl;
    private javax.swing.JLabel lblEnrLbl;
    private javax.swing.JLabel lblEnrolled;
    private javax.swing.JLabel lblGPA;
    private javax.swing.JLabel lblGPALbl;
    private javax.swing.JLabel lblIdLbl;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPortal;
    private javax.swing.JLabel lblSecLbl;
    private javax.swing.JLabel lblSection;
    private javax.swing.JLabel lblSmis;
    private javax.swing.JLabel lblStudentId;
    private javax.swing.JLabel lblStudentNo;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel panelCourse;
    private javax.swing.JPanel panelEnrolled;
    private javax.swing.JPanel panelGPA;
    private javax.swing.JPanel panelId;
    private javax.swing.JPanel panelSection;
    private javax.swing.JPanel sidebar;
    // End of variables declaration//GEN-END:variables
}
