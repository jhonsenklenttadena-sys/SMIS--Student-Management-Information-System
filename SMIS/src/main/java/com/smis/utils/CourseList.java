package com.smis.utils;

import java.util.List;

/**
 * Master list of available courses.
 * Add or remove courses here to update all dropdowns system-wide.
 */
public class CourseList {

    public static final List<String> COURSES = List.of(
        "ABM",
        "BS Accountancy",
        "BS Agriculture",
        "BS Architecture",
        "BSBA",
        "BS Biology",
        "BS Chemical Engineering",
        "BS Chemistry",
        "BS Civil Engineering",
        "BS Computer Engineering",
        "BS Computer Science",
        "BS Criminology",
        "BS Dentistry",
        "BS Economics",
        "BS Electrical Engineering",
        "BS Electronics Engineering",
        "BS Environmental Science",
        "BS Food Technology",
        "BS Forestry",
        "BS Hotel & Restaurant Management",
        "BS Industrial Engineering",
        "BSIT",
        "BS Marine Biology",
        "BS Marine Engineering",
        "BS Marine Transportation",
        "BS Mathematics",
        "BS Mechanical Engineering",
        "BS Medical Laboratory Science",
        "BS Midwifery",
        "BSN (Nursing)",
        "BS Nutrition & Dietetics",
        "BS Pharmacy",
        "BS Physical Therapy",
        "BS Psychology",
        "BS Radiologic Technology",
        "BS Social Work",
        "BS Statistics",
        "BS Tourism Management",
        "BSED (Education)",
        "BEED (Elementary Education)",
        "Criminology",
        "HUMSS",
        "Political Science",
        "Philosophy",
        "Sociology",
        "STEM",
        "TVL"
    );

    public static javafx.scene.control.ComboBox<String> createComboBox() {
        javafx.scene.control.ComboBox<String> cb = new javafx.scene.control.ComboBox<>();
        cb.getItems().addAll(COURSES);
        cb.setStyle("-fx-background-color:#F8F9FA;-fx-border-color:#DEE2E6;" +
                    "-fx-border-radius:6;-fx-background-radius:6;-fx-font-size:13px;");
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setEditable(false);
        return cb;
    }
}
