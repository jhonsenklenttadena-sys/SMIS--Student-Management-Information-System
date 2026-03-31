package com.smis.models;

public class Student {
    private int id;
    private int userId;
    private String studentId;
    private String yearLevel;
    private String course;
    private String section;
    private String guardianName;
    private String guardianContact;
    private String address;
    private String fullName;
    private String username;
    private String email;
    private String contact;
    private boolean isActive;

    public Student() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int u) { this.userId = u; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String s) { this.studentId = s; }
    public String getYearLevel() { return yearLevel; }
    public void setYearLevel(String y) { this.yearLevel = y; }
    public String getCourse() { return course; }
    public void setCourse(String c) { this.course = c; }
    public String getSection() { return section; }
    public void setSection(String s) { this.section = s; }
    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String g) { this.guardianName = g; }
    public String getGuardianContact() { return guardianContact; }
    public void setGuardianContact(String g) { this.guardianContact = g; }
    public String getAddress() { return address; }
    public void setAddress(String a) { this.address = a; }
    public String getFullName() { return fullName; }
    public void setFullName(String n) { this.fullName = n; }
    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getContact() { return contact; }
    public void setContact(String c) { this.contact = c; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean a) { this.isActive = a; }

    @Override
    public String toString() { return fullName + " [" + studentId + "]"; }
}
