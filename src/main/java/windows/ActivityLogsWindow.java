package windows;

import database.DatabaseManager;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;

public class ActivityLogsWindow extends javax.swing.JFrame {

    private DefaultTableModel logModel;
    private Integer facultyUserId = null;

    public ActivityLogsWindow() {
        logModel = new DefaultTableModel(new Object[]{"ID","User","Action","Detail","Timestamp"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        initComponents();
        facultyUserId = null;
        tblLogs.setModel(logModel);
        loadLogs();
    }

    public ActivityLogsWindow(int facultyUserId) {
        this.facultyUserId = facultyUserId;
        logModel = new DefaultTableModel(new Object[]{"ID","User","Action","Detail","Timestamp"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        initComponents();
        tblLogs.setModel(logModel);
        setTitle("Activity Logs");
        setLocationRelativeTo(null);
        loadLogsForFaculty(facultyUserId);
    }

    private void loadLogsForFaculty(int facultyUserId) {
        logModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT al.id, COALESCE(u.username,'system'), al.action, COALESCE(al.detail,''), al.logged_at " +
                 "FROM activity_logs al LEFT JOIN users u ON u.id=al.user_id " +
                 "WHERE al.user_id = ? ORDER BY al.id DESC LIMIT 300")) {
            ps.setInt(1, facultyUserId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                logModel.addRow(new Object[]{
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5)
                });
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    private void loadLogs() {
        logModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(
                 "SELECT al.id, COALESCE(u.username,'system'), al.action, COALESCE(al.detail,''), al.logged_at " +
                 "FROM activity_logs al LEFT JOIN users u ON u.id=al.user_id " +
                 "ORDER BY al.id DESC LIMIT 300")) {
            while (rs.next()) {
                logModel.addRow(new Object[]{
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5)
                });
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLogs = new javax.swing.JTable();
        btnRefresh = new color.Button_Admin("");
        jButton3 = new color.Button_Back();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(232, 240, 254));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(26, 35, 126));
        jLabel1.setText("Activity Logs");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 15, -1, -1));

        jScrollPane1.setViewportView(tblLogs);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 770, 400));

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        jPanel1.add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 460, 100, 30));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("←Back");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(682, 10, 90, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 790, 500));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        if (facultyUserId == null){
            loadLogs();
        }else{
            loadLogsForFaculty(facultyUserId);
        }
    }//GEN-LAST:event_btnRefreshActionPerformed

        public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ActivityLogsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ActivityLogsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ActivityLogsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ActivityLogsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ActivityLogsWindow().setVisible(true);
            }
        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblLogs;
    // End of variables declaration//GEN-END:variables
}
