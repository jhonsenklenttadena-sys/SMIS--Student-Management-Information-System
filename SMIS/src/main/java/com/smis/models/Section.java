package com.smis.models;

public class Section {
    private int id;
    private String sectionName;
    private String yearLevel;
    private String course;
    private String schoolYear;
    private String semester;

    public Section() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSectionName() { return sectionName; }
    public void setSectionName(String n) { this.sectionName = n; }
    public String getYearLevel() { return yearLevel; }
    public void setYearLevel(String y) { this.yearLevel = y; }
    public String getCourse() { return course; }
    public void setCourse(String c) { this.course = c; }
    public String getSchoolYear() { return schoolYear; }
    public void setSchoolYear(String s) { this.schoolYear = s; }
    public String getSemester() { return semester; }
    public void setSemester(String s) { this.semester = s; }

    @Override
    public String toString() { return sectionName + " (" + yearLevel + " - " + course + ")"; }
}
