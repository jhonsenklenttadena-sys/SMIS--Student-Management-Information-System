package com.smis.models;

public class ActivityLog {
    private int id;
    private int userId;
    private String username;
    private String action;
    private String details;
    private String loggedAt;

    public ActivityLog() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int u) { this.userId = u; }
    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getAction() { return action; }
    public void setAction(String a) { this.action = a; }
    public String getDetails() { return details; }
    public void setDetails(String d) { this.details = d; }
    public String getLoggedAt() { return loggedAt; }
    public void setLoggedAt(String t) { this.loggedAt = t; }
}
