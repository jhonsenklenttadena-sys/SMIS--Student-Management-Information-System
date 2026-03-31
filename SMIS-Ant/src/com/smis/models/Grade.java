package com.smis.models;

public class Grade {
    private int id;
    private int enrollmentId;
    private Double prelim;
    private Double midterm;
    private Double prefinal;
    private Double finalGrade;
    private Double computedGrade;
    private String remarks;
    private String performanceNotes;
    private String studentName;
    private String studentIdNo;
    private String subjectCode;
    private String subjectName;

    public Grade() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int e) { this.enrollmentId = e; }
    public Double getPrelim() { return prelim; }
    public void setPrelim(Double p) { this.prelim = p; }
    public Double getMidterm() { return midterm; }
    public void setMidterm(Double m) { this.midterm = m; }
    public Double getPrefinal() { return prefinal; }
    public void setPrefinal(Double p) { this.prefinal = p; }
    public Double getFinalGrade() { return finalGrade; }
    public void setFinalGrade(Double f) { this.finalGrade = f; }
    public Double getComputedGrade() { return computedGrade; }
    public void setComputedGrade(Double c) { this.computedGrade = c; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String r) { this.remarks = r; }
    public String getPerformanceNotes() { return performanceNotes; }
    public void setPerformanceNotes(String n) { this.performanceNotes = n; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String n) { this.studentName = n; }
    public String getStudentIdNo() { return studentIdNo; }
    public void setStudentIdNo(String n) { this.studentIdNo = n; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String c) { this.subjectCode = c; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String n) { this.subjectName = n; }
}
