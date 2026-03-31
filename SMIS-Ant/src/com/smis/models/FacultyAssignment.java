package com.smis.models;

public class FacultyAssignment {
    private int id;
    private int facultyId;
    private int subjectId;
    private int sectionId;
    private String schedule;
    private String room;
    private String schoolYear;
    private String semester;

    // Joined fields
    private String facultyName;
    private String subjectCode;
    private String subjectName;
    private String sectionName;

    public FacultyAssignment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int f) { this.facultyId = f; }
    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int s) { this.subjectId = s; }
    public int getSectionId() { return sectionId; }
    public void setSectionId(int s) { this.sectionId = s; }
    public String getSchedule() { return schedule; }
    public void setSchedule(String s) { this.schedule = s; }
    public String getRoom() { return room; }
    public void setRoom(String r) { this.room = r; }
    public String getSchoolYear() { return schoolYear; }
    public void setSchoolYear(String s) { this.schoolYear = s; }
    public String getSemester() { return semester; }
    public void setSemester(String s) { this.semester = s; }
    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String n) { this.facultyName = n; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String c) { this.subjectCode = c; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String n) { this.subjectName = n; }
    public String getSectionName() { return sectionName; }
    public void setSectionName(String n) { this.sectionName = n; }

    @Override
    public String toString() { return subjectCode + " - " + sectionName; }
}
