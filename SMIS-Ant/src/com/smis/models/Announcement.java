package com.smis.models;

import java.time.LocalDateTime;

public class Announcement {
    private int id;
    private String title;
    private String content;
    private int fontSize;
    private String textColor;
    private String imageBase64;
    private int postedBy;
    private String type;
    private Integer assignmentId;
    private LocalDateTime createdAt;
    private boolean isActive;
    private String postedByName;
    private String subjectName;
    private String sectionName;

    public Announcement() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }
    public String getContent() { return content; }
    public void setContent(String c) { this.content = c; }
    public int getFontSize() { return fontSize; }
    public void setFontSize(int f) { this.fontSize = f; }
    public String getTextColor() { return textColor; }
    public void setTextColor(String c) { this.textColor = c; }
    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String img) { this.imageBase64 = img; }
    public int getPostedBy() { return postedBy; }
    public void setPostedBy(int p) { this.postedBy = p; }
    public String getType() { return type; }
    public void setType(String t) { this.type = t; }
    public Integer getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Integer a) { this.assignmentId = a; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean a) { this.isActive = a; }
    public String getPostedByName() { return postedByName; }
    public void setPostedByName(String n) { this.postedByName = n; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String n) { this.subjectName = n; }
    public String getSectionName() { return sectionName; }
    public void setSectionName(String n) { this.sectionName = n; }
}
