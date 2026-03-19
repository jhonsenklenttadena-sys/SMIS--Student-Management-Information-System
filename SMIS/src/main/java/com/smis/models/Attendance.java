package com.smis.models;

import java.time.LocalDate;

public class Attendance {
    private int id;
    private int enrollmentId;
    private LocalDate attendanceDate;
    private String status;
    private String remarks;
    private String studentName;
    private String studentIdNo;
    private String subjectCode;

    public Attendance() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int e) { this.enrollmentId = e; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate d) { this.attendanceDate = d; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String r) { this.remarks = r; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String n) { this.studentName = n; }
    public String getStudentIdNo() { return studentIdNo; }
    public void setStudentIdNo(String n) { this.studentIdNo = n; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String c) { this.subjectCode = c; }
}
