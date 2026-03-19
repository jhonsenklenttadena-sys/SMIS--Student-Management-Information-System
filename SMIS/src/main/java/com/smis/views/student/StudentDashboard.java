package com.smis.views.student;

import com.smis.models.*;
import com.smis.services.*;
import com.smis.utils.*;
import com.smis.views.common.LoginView;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;

import java.util.List;

/**
 * Student Dashboard — orange theme.
 * Read-only access to grades, attendance, announcements.
 */
public class StudentDashboard {

    private Stage stage;
    private BorderPane root;
    private User currentUser;
    private Student student;

    private static final String COLOR = UIUtils.STUDENT_PRIMARY;

    public StudentDashboard(Stage stage) {
        this.stage = stage;
        this.currentUser = SessionManager.getCurrentUser();
        this.student = StudentService.getStudentByUserId(currentUser.getId());
    }

    public void show() {
        root = new BorderPane();
        root.setLeft(buildSidebar());
        root.setCenter(wrap(buildHome()));

        Scene scene = new Scene(root, 1280, 760);
        stage.setScene(scene);
        stage.setTitle("SMIS — Student Portal");
        stage.setMaximized(true);
        stage.show();
    }

    private ScrollPane wrap(javafx.scene.Node node) {
        ScrollPane sp = new ScrollPane(node);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:" + UIUtils.BG_COLOR + ";-fx-background:" + UIUtils.BG_COLOR + ";");
        return sp;
    }

    // ── Sidebar ───────────────────────────────────────────────

    private VBox buildSidebar() {
        VBox sb = new VBox(4);
        sb.setPrefWidth(240);
        sb.setPadding(new Insets(0, 10, 20, 10));
        sb.setStyle("-fx-background-color:" + UIUtils.SIDEBAR_DARK + ";");

        VBox logo = new VBox(4);
        logo.setPadding(new Insets(24, 10, 20, 10));
        Label lTitle = new Label("SMIS");
        lTitle.setFont(Font.font("System", FontWeight.BOLD, 26));
        lTitle.setTextFill(Color.WHITE);
        Label lRole = new Label("Student Portal");
        lRole.setTextFill(Color.web("rgba(255,255,255,0.5)"));
        lRole.setFont(Font.font("System", 12));
        logo.getChildren().addAll(lTitle, lRole);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:rgba(255,255,255,0.1);");

        Button btnHome     = UIUtils.navButton("Dashboard",    "🏠");
        Button btnSubjects = UIUtils.navButton("My Subjects",  "📚");
        Button btnGrades   = UIUtils.navButton("My Grades",    "📊");
        Button btnAttend   = UIUtils.navButton("Attendance",   "✅");
        Button btnAnnounce = UIUtils.navButton("Announcements","📢");
        Button btnProfile  = UIUtils.navButton("My Profile",   "👤");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label("👤  " + currentUser.getFullName());
        userInfo.setFont(Font.font("System", 12));
        userInfo.setTextFill(Color.web("rgba(255,255,255,0.6)"));
        userInfo.setPadding(new Insets(0, 0, 6, 10));

        Button btnLogout = UIUtils.primaryButton("Log Out", UIUtils.DANGER_COLOR);
        btnLogout.setMaxWidth(Double.MAX_VALUE);

        sb.getChildren().addAll(logo, sep, btnHome, btnSubjects, btnGrades,
                btnAttend, btnAnnounce, btnProfile, spacer, userInfo, btnLogout);

        btnHome.setOnAction(e     -> root.setCenter(wrap(buildHome())));
        btnSubjects.setOnAction(e -> root.setCenter(wrap(buildSubjectsPanel())));
        btnGrades.setOnAction(e   -> root.setCenter(wrap(buildGradesPanel())));
        btnAttend.setOnAction(e   -> root.setCenter(wrap(buildAttendancePanel())));
        btnAnnounce.setOnAction(e -> root.setCenter(wrap(buildAnnouncementsPanel())));
        btnProfile.setOnAction(e  -> root.setCenter(wrap(buildProfilePanel())));
        btnLogout.setOnAction(e -> {
            if (UIUtils.showConfirm("Log Out", "Are you sure you want to log out?")) {
                SessionManager.clearSession();
                stage.setScene(new LoginView(stage).getScene());
                stage.setMaximized(false);
                stage.setWidth(900); stage.setHeight(600);
                stage.centerOnScreen();
            }
        });

        return sb;
    }

    // ── Home ──────────────────────────────────────────────────

    private VBox buildHome() {
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:" + UIUtils.BG_COLOR + ";");

        Label title = UIUtils.pageTitle("Welcome, " + currentUser.getFullName() + "!");
        Label sub = new Label(student != null ? student.getCourse() + " | " + student.getSection() + " | " + student.getYearLevel() : "");
        sub.setTextFill(Color.web(UIUtils.TEXT_MUTED));

        // Stat cards
        int enrolledSubjects = student != null ? EnrollmentService.getEnrollmentsByStudent(student.getId()).size() : 0;
        List<Grade> grades = student != null ? GradeService.getGradesByStudent(student.getId()) : List.of();
        double gpa = grades.stream()
                .filter(g -> g.getComputedGrade() != null)
                .mapToDouble(Grade::getComputedGrade)
                .average().orElse(0);
        String gpaStr = gpa > 0 ? String.format("%.2f", gpa) : "—";

        HBox stats = new HBox(16);
        stats.getChildren().addAll(
            UIUtils.statCard("Enrolled Subjects", String.valueOf(enrolledSubjects), COLOR),
            UIUtils.statCard("GPA Average", gpaStr, UIUtils.FACULTY_PRIMARY),
            UIUtils.statCard("Student ID", student != null ? student.getStudentId() : "—", UIUtils.ADMIN_PRIMARY)
        );
        stats.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // Recent announcements preview
        VBox announceCard = UIUtils.card(UIUtils.sectionLabel("📢  Latest Announcements"));
        if (student != null) {
            List<Announcement> announcements = AnnouncementService.getAnnouncementsForStudent(student.getId());
            if (announcements.isEmpty()) {
                announceCard.getChildren().add(new Label("No announcements yet."));
            } else {
                announcements.stream().limit(5).forEach(a -> {
                    Label lbl = new Label("• [" + a.getType() + "] " + a.getTitle() + " — " + a.getPostedByName());
                    lbl.setFont(Font.font("System", 13));
                    announceCard.getChildren().add(lbl);
                });
            }
        }

        panel.getChildren().addAll(title, sub, stats, announceCard);
        return panel;
    }

    // ── Subjects Panel ────────────────────────────────────────

    private VBox buildSubjectsPanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:" + UIUtils.BG_COLOR + ";");
        panel.getChildren().add(UIUtils.pageTitle("My Subjects"));

        TableView<Enrollment> table = UIUtils.styledTable();
        TableColumn<Enrollment, String> colCode  = new TableColumn<>("Code");     colCode.setCellValueFactory(new PropertyValueFactory<>("subjectCode"));
        TableColumn<Enrollment, String> colName  = new TableColumn<>("Subject");  colName.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        TableColumn<Enrollment, String> colSec   = new TableColumn<>("Section");  colSec.setCellValueFactory(new PropertyValueFactory<>("sectionName"));
        TableColumn<Enrollment, String> colFac   = new TableColumn<>("Teacher");  colFac.setCellValueFactory(new PropertyValueFactory<>("facultyName"));
        TableColumn<Enrollment, String> colSched = new TableColumn<>("Schedule"); colSched.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        TableColumn<Enrollment, String> colStat  = new TableColumn<>("Status");   colStat.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(colCode, colName, colSec, colFac, colSched, colStat);
        table.setPrefHeight(440);

        if (student != null) {
            table.setItems(FXCollections.observableArrayList(
                    EnrollmentService.getEnrollmentsByStudent(student.getId())));
        }

        panel.getChildren().add(table);
        return panel;
    }

    // ── Grades Panel ──────────────────────────────────────────

    private VBox buildGradesPanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:" + UIUtils.BG_COLOR + ";");
        panel.getChildren().add(UIUtils.pageTitle("My Grades"));

        if (student == null) { panel.getChildren().add(new Label("Student profile not found.")); return panel; }

        List<Grade> grades = GradeService.getGradesByStudent(student.getId());

        // GPA summary card
        double gpa = grades.stream()
                .filter(g -> g.getComputedGrade() != null)
                .mapToDouble(Grade::getComputedGrade)
                .average().orElse(0);
        long passed = grades.stream().filter(g -> "PASSED".equals(g.getRemarks())).count();
        long failed = grades.stream().filter(g -> "FAILED".equals(g.getRemarks())).count();

        HBox summary = new HBox(16);
        summary.getChildren().addAll(
            UIUtils.statCard("GPA Average", gpa > 0 ? String.format("%.2f", gpa) : "—", COLOR),
            UIUtils.statCard("Passed", String.valueOf(passed), UIUtils.FACULTY_PRIMARY),
            UIUtils.statCard("Failed", String.valueOf(failed), UIUtils.DANGER_COLOR)
        );
        summary.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // Grades table
        TableView<Grade> table = UIUtils.styledTable();
        TableColumn<Grade, String> colCode    = new TableColumn<>("Subject Code"); colCode.setCellValueFactory(new PropertyValueFactory<>("subjectCode"));
        TableColumn<Grade, String> colName    = new TableColumn<>("Subject Name"); colName.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        TableColumn<Grade, Double> colPrelim  = new TableColumn<>("Prelim");       colPrelim.setCellValueFactory(new PropertyValueFactory<>("prelim"));
        TableColumn<Grade, Double> colMid     = new TableColumn<>("Midterm");      colMid.setCellValueFactory(new PropertyValueFactory<>("midterm"));
        TableColumn<Grade, Double> colPre     = new TableColumn<>("Pre-Final");    colPre.setCellValueFactory(new PropertyValueFactory<>("prefinal"));
        TableColumn<Grade, Double> colFinal   = new TableColumn<>("Final");        colFinal.setCellValueFactory(new PropertyValueFactory<>("finalGrade"));
        TableColumn<Grade, Double> colComp    = new TableColumn<>("Computed");     colComp.setCellValueFactory(new PropertyValueFactory<>("computedGrade"));
        TableColumn<Grade, String> colRemarks = new TableColumn<>("Remarks");      colRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        // Color-code remarks column
        colRemarks.setCellFactory(col -> new TableCell<Grade, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                if ("PASSED".equals(item)) setStyle("-fx-text-fill: " + UIUtils.SUCCESS_GREEN + "; -fx-font-weight: bold;");
                else if ("FAILED".equals(item)) setStyle("-fx-text-fill: " + UIUtils.DANGER_COLOR + "; -fx-font-weight: bold;");
                else setStyle("-fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
            }
        });

        table.getColumns().addAll(colCode, colName, colPrelim, colMid, colPre, colFinal, colComp, colRemarks);
        table.setItems(FXCollections.observableArrayList(grades));
        table.setPrefHeight(420);

        panel.getChildren().addAll(summary, table);
        return panel;
    }

    // ── Attendance Panel ──────────────────────────────────────

    private VBox buildAttendancePanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:" + UIUtils.BG_COLOR + ";");
        panel.getChildren().add(UIUtils.pageTitle("My Attendance"));

        if (student == null) { panel.getChildren().add(new Label("Student profile not found.")); return panel; }

        // Attendance summary per subject
        List<Enrollment> enrollments = EnrollmentService.getEnrollmentsByStudent(student.getId());

        // Summary cards
        HBox summaryRow = new HBox(12);
        for (Enrollment en : enrollments) {
            double pct = AttendanceService.getAttendancePercentage(en.getId());
            String color = pct >= 80 ? UIUtils.FACULTY_PRIMARY : pct >= 60 ? UIUtils.STUDENT_PRIMARY : UIUtils.DANGER_COLOR;
            summaryRow.getChildren().add(UIUtils.statCard(en.getSubjectCode(), String.format("%.1f%%", pct), color));
        }
        if (summaryRow.getChildren().isEmpty()) summaryRow.getChildren().add(new Label("No enrolled subjects."));

        // Detailed attendance log
        TabPane tabs = new TabPane();
        for (Enrollment en : enrollments) {
            Tab tab = new Tab(en.getSubjectCode());
            tab.setClosable(false);

            TableView<Attendance> attTable = UIUtils.styledTable();
            TableColumn<Attendance, String> colDate   = new TableColumn<>("Date");    colDate.setCellValueFactory(new PropertyValueFactory<>("attendanceDate"));
            TableColumn<Attendance, String> colStatus = new TableColumn<>("Status");  colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            TableColumn<Attendance, String> colRemark = new TableColumn<>("Remarks"); colRemark.setCellValueFactory(new PropertyValueFactory<>("remarks"));
            attTable.getColumns().addAll(colDate, colStatus, colRemark);
            attTable.setItems(FXCollections.observableArrayList(
                    AttendanceService.getAttendanceByEnrollment(en.getId())));

            // Color-code status
            colStatus.setCellFactory(col -> new TableCell<Attendance, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) { setText(null); setStyle(""); return; }
                    setText(item);
                    switch (item) {
                        case "PRESENT" -> setStyle("-fx-text-fill: " + UIUtils.SUCCESS_GREEN + "; -fx-font-weight: bold;");
                        case "ABSENT"  -> setStyle("-fx-text-fill: " + UIUtils.DANGER_COLOR + "; -fx-font-weight: bold;");
                        case "LATE"    -> setStyle("-fx-text-fill: " + UIUtils.STUDENT_PRIMARY + ";");
                        default        -> setStyle("-fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
                    }
                }
            });

            VBox tabContent = new VBox(8, attTable);
            tabContent.setPadding(new Insets(12));
            tab.setContent(tabContent);
            tabs.getTabs().add(tab);
        }

        panel.getChildren().addAll(summaryRow, tabs);
        return panel;
    }

    // ── Announcements Panel ───────────────────────────────────

    private VBox buildAnnouncementsPanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:" + UIUtils.BG_COLOR + ";");
        panel.getChildren().add(UIUtils.pageTitle("Announcements"));

        if (student == null) { panel.getChildren().add(new Label("Student profile not found.")); return panel; }

        TabPane tabs = new TabPane();

        // General tab
        Tab generalTab = new Tab("📢  General (Admin)");
        generalTab.setClosable(false);
        TableView<Announcement> genTable = UIUtils.styledTable();
        buildAnnouncementTable(genTable);
        genTable.setItems(FXCollections.observableArrayList(AnnouncementService.getGeneralAnnouncements()));
        generalTab.setContent(new VBox(genTable) {{ setPadding(new Insets(12)); }});

        // Subject-specific tab
        Tab subjectTab = new Tab("📘  Subject Announcements");
        subjectTab.setClosable(false);
        TableView<Announcement> subjTable = UIUtils.styledTable();
        buildAnnouncementTable(subjTable);
        List<Announcement> all = AnnouncementService.getAnnouncementsForStudent(student.getId());
        List<Announcement> subjectOnly = all.stream()
                .filter(a -> "SUBJECT".equals(a.getType()))
                .toList();
        subjTable.setItems(FXCollections.observableArrayList(subjectOnly));
        subjectTab.setContent(new VBox(subjTable) {{ setPadding(new Insets(12)); }});

        tabs.getTabs().addAll(generalTab, subjectTab);

        // Click to read
        for (TableView<Announcement> t : List.of(genTable, subjTable)) {
            t.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
                if (sel != null) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle(sel.getTitle()); a.setHeaderText(sel.getTitle());
                    a.setContentText(sel.getContent() + "\n\n— Posted by: " + sel.getPostedByName());
                    a.show();
                }
            });
        }

        panel.getChildren().add(tabs);
        return panel;
    }

    private void buildAnnouncementTable(TableView<Announcement> table) {
        TableColumn<Announcement, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Announcement, String> colBy    = new TableColumn<>("Posted By");
        colBy.setCellValueFactory(new PropertyValueFactory<>("postedByName"));
        TableColumn<Announcement, String> colSubj  = new TableColumn<>("Subject");
        colSubj.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        table.getColumns().addAll(colTitle, colBy, colSubj);
        table.setPrefHeight(360);
    }

    // ── Profile Panel ─────────────────────────────────────────

    private VBox buildProfilePanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:" + UIUtils.BG_COLOR + ";");
        panel.getChildren().add(UIUtils.pageTitle("My Profile"));

        if (student == null) { panel.getChildren().add(new Label("Student profile not found.")); return panel; }

        VBox infoCard = UIUtils.card(
            UIUtils.sectionLabel("Personal Information"),
            row("Student ID:",    student.getStudentId()),
            row("Full Name:",     currentUser.getFullName()),
            row("Username:",      currentUser.getUsername()),
            row("Email:",         currentUser.getEmail()),
            row("Contact:",       currentUser.getContact()),
            row("Course:",        student.getCourse()),
            row("Year Level:",    student.getYearLevel()),
            row("Section:",       student.getSection()),
            row("Guardian:",      student.getGuardianName()),
            row("Guardian Tel:",  student.getGuardianContact())
        );
        infoCard.setMaxWidth(560);

        Label note = new Label("ℹ️  To update your information, please contact your Administrator.");
        note.setTextFill(Color.web(UIUtils.TEXT_MUTED));
        note.setFont(Font.font("System", FontPosture.ITALIC, 13));

        panel.getChildren().addAll(infoCard, note);
        return panel;
    }

    private HBox row(String label, String value) {
        Label lbl = new Label(label);
        lbl.setMinWidth(130);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web(UIUtils.TEXT_MUTED));
        Label val = new Label(value != null ? value : "—");
        val.setFont(Font.font("System", 13));
        val.setTextFill(Color.web(UIUtils.TEXT_DARK));
        HBox r = new HBox(10, lbl, val);
        r.setAlignment(Pos.CENTER_LEFT);
        return r;
    }
}
