package com.smis.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

// ============================================================
// ENROLLMENT
// ============================================================
public class Enrollment {
    private int id;
    private int studentId;
    private int assignmentId;
    private String status;

    // Joined fields
    private String studentName;
    private String studentIdNo;
    private String subjectCode;
    private String subjectName;
    private String sectionName;
    private String facultyName;
    private String schedule;

    public Enrollment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int s) { this.studentId = s; }
    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int a) { this.assignmentId = a; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String n) { this.studentName = n; }
    public String getStudentIdNo() { return studentIdNo; }
    public void setStudentIdNo(String n) { this.studentIdNo = n; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String c) { this.subjectCode = c; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String n) { this.subjectName = n; }
    public String getSectionName() { return sectionName; }
    public void setSectionName(String n) { this.sectionName = n; }
    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String n) { this.facultyName = n; }
    public String getSchedule() { return schedule; }
    public void setSchedule(String s) { this.schedule = s; }
}
