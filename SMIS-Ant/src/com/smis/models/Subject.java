package com.smis.models;

public class Subject {
    private int id;
    private String subjectCode;
    private String subjectName;
    private int units;
    private String description;
    private boolean isActive;

    public Subject() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String c) { this.subjectCode = c; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String n) { this.subjectName = n; }
    public int getUnits() { return units; }
    public void setUnits(int u) { this.units = u; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean a) { this.isActive = a; }

    @Override
    public String toString() { return subjectCode + " - " + subjectName; }
}
