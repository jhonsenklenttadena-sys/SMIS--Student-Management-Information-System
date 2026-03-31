package com.smis.models;

public class Faculty {
    private int id;
    private int userId;
    private String facultyId;
    private String department;
    private String specialization;
    private String fullName;
    private String username;
    private String email;
    private String contact;
    private boolean isActive;

    public Faculty() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int u) { this.userId = u; }
    public String getFacultyId() { return facultyId; }
    public void setFacultyId(String f) { this.facultyId = f; }
    public String getDepartment() { return department; }
    public void setDepartment(String d) { this.department = d; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String s) { this.specialization = s; }
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
    public String toString() { return fullName + " [" + facultyId + "]"; }
}
