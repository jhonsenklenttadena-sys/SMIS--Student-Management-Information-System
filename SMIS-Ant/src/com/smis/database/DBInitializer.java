package com.smis.database;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBInitializer {

    public static void initialize() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) { System.err.println("[DBInit] No connection."); return; }
        try (Statement st = conn.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL CHECK(role IN ('ADMIN','FACULTY','STUDENT')),
                    full_name TEXT NOT NULL,
                    email TEXT,
                    contact TEXT,
                    is_active INTEGER DEFAULT 1,
                    created_at TEXT DEFAULT (datetime('now'))
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    student_id TEXT UNIQUE NOT NULL,
                    year_level TEXT,
                    course TEXT,
                    section TEXT,
                    guardian_name TEXT,
                    guardian_contact TEXT,
                    address TEXT,
                    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS faculty (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    faculty_id TEXT UNIQUE NOT NULL,
                    department TEXT,
                    specialization TEXT,
                    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS subjects (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    subject_code TEXT UNIQUE NOT NULL,
                    subject_name TEXT NOT NULL,
                    units INTEGER DEFAULT 3,
                    description TEXT,
                    is_active INTEGER DEFAULT 1
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS sections (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    section_name TEXT NOT NULL,
                    year_level TEXT,
                    course TEXT,
                    school_year TEXT,
                    semester TEXT DEFAULT '1ST'
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS faculty_assignments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    faculty_id INTEGER NOT NULL DEFAULT 0,
                    subject_id INTEGER NOT NULL,
                    section_id INTEGER NOT NULL,
                    schedule TEXT,
                    room TEXT,
                    school_year TEXT,
                    semester TEXT DEFAULT '1ST',
                    FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
                    FOREIGN KEY(section_id) REFERENCES sections(id) ON DELETE CASCADE,
                    UNIQUE(subject_id, section_id)
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS enrollments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER NOT NULL,
                    assignment_id INTEGER NOT NULL,
                    enrollment_date TEXT DEFAULT (datetime('now')),
                    status TEXT DEFAULT 'ENROLLED',
                    FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE,
                    FOREIGN KEY(assignment_id) REFERENCES faculty_assignments(id) ON DELETE CASCADE,
                    UNIQUE(student_id, assignment_id)
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS grades (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    enrollment_id INTEGER NOT NULL,
                    prelim REAL,
                    midterm REAL,
                    prefinal REAL,
                    final_grade REAL,
                    computed_grade REAL,
                    remarks TEXT DEFAULT 'IN PROGRESS',
                    performance_notes TEXT,
                    updated_at TEXT DEFAULT (datetime('now')),
                    FOREIGN KEY(enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS attendance (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    enrollment_id INTEGER NOT NULL,
                    attendance_date TEXT NOT NULL,
                    status TEXT DEFAULT 'PRESENT',
                    remarks TEXT,
                    FOREIGN KEY(enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
                    UNIQUE(enrollment_id, attendance_date)
                )""");

            // Announcements with rich text support (Base64 image, font styles)
            st.execute("""
                CREATE TABLE IF NOT EXISTS announcements (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    content TEXT NOT NULL,
                    font_size INTEGER DEFAULT 14,
                    text_color TEXT DEFAULT '#2C3E50',
                    image_base64 TEXT,
                    posted_by INTEGER NOT NULL,
                    type TEXT DEFAULT 'GENERAL',
                    assignment_id INTEGER,
                    created_at TEXT DEFAULT (datetime('now')),
                    is_active INTEGER DEFAULT 1,
                    FOREIGN KEY(posted_by) REFERENCES users(id) ON DELETE CASCADE
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS activity_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    action TEXT NOT NULL,
                    details TEXT,
                    logged_at TEXT DEFAULT (datetime('now')),
                    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS feedback (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER NOT NULL,
                    recipient_id INTEGER NOT NULL,
                    message TEXT NOT NULL,
                    status TEXT DEFAULT 'UNREAD',
                    created_at TEXT DEFAULT (datetime('now'))
                )""");

            // Add new columns to existing tables if upgrading
            try { st.execute("ALTER TABLE grades ADD COLUMN performance_notes TEXT"); } catch (Exception ignored) {}
            try { st.execute("ALTER TABLE announcements ADD COLUMN font_size INTEGER DEFAULT 14"); } catch (Exception ignored) {}
            try { st.execute("ALTER TABLE announcements ADD COLUMN text_color TEXT DEFAULT '#2C3E50'"); } catch (Exception ignored) {}
            try { st.execute("ALTER TABLE announcements ADD COLUMN image_base64 TEXT"); } catch (Exception ignored) {}

            System.out.println("[DBInit] All tables ready.");

            // Default admin
            var check = conn.prepareStatement("SELECT id FROM users WHERE username='admin'");
            var rs = check.executeQuery();
            if (!rs.next()) {
                String hashed = BCrypt.withDefaults().hashToString(12, "admin123".toCharArray());
                var ins = conn.prepareStatement(
                    "INSERT INTO users (username,password,role,full_name,email,contact) VALUES (?,?,?,?,?,?)");
                ins.setString(1,"admin"); ins.setString(2,hashed); ins.setString(3,"ADMIN");
                ins.setString(4,"System Administrator"); ins.setString(5,"admin@smis.edu"); ins.setString(6,"09000000000");
                ins.executeUpdate();
                System.out.println("[DBInit] Default admin created: admin / admin123");
            }
        } catch (SQLException e) {
            System.err.println("[DBInit] Error: " + e.getMessage());
        }
    }
}
