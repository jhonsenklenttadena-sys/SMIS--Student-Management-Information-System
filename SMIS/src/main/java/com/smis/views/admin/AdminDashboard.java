package com.smis.views.admin;

import com.smis.models.*;
import com.smis.services.*;
import com.smis.utils.*;
import com.smis.views.common.LoginView;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.Base64;

public class AdminDashboard {

    private Stage stage;
    private BorderPane root;
    private User currentUser;
    private static final String COLOR = UIUtils.ADMIN_COLOR;

    public AdminDashboard(Stage stage) {
        this.stage = stage;
        this.currentUser = SessionManager.getCurrentUser();
    }

    public void show() {
        root = new BorderPane();
        root.setLeft(buildSidebar());
        root.setCenter(wrap(buildHome()));
        stage.setScene(new Scene(root, 1280, 760));
        stage.setTitle("SMIS — Admin Dashboard");
        stage.setMaximized(true);
        stage.show();
    }

    private ScrollPane wrap(javafx.scene.Node n) {
        ScrollPane sp = new ScrollPane(n);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:" + UIUtils.BG_COLOR + ";-fx-background:" + UIUtils.BG_COLOR + ";");
        return sp;
    }

    private VBox buildSidebar() {
        VBox sb = new VBox(4);
        sb.setPrefWidth(240);
        sb.setPadding(new Insets(0,10,20,10));
        sb.setStyle("-fx-background-color:" + UIUtils.SIDEBAR_DARK + ";");

        VBox logo = new VBox(4); logo.setPadding(new Insets(24,10,20,10));
        Label lt = new Label("SMIS"); lt.setFont(Font.font("System",FontWeight.BOLD,26)); lt.setTextFill(Color.WHITE);
        Label lr = new Label("Administrator"); lr.setTextFill(Color.web("rgba(255,255,255,0.5)")); lr.setFont(Font.font("System",12));
        logo.getChildren().addAll(lt,lr);

        Separator sep = new Separator(); sep.setStyle("-fx-background-color:rgba(255,255,255,0.1);");

        Button btnHome   = UIUtils.navButton("Dashboard",    "🏠");
        Button btnStud   = UIUtils.navButton("Students",     "👨‍🎓");
        Button btnFac    = UIUtils.navButton("Faculty",      "👩‍🏫");
        Button btnSubj   = UIUtils.navButton("Subjects",     "📚");
        Button btnSec    = UIUtils.navButton("Sections",     "🏫");
        Button btnAssign = UIUtils.navButton("Assignments",  "📋");
        Button btnAnn    = UIUtils.navButton("Announcements","📢");
        Button btnLogs   = UIUtils.navButton("Activity Logs","📄");

        Region spacer = new Region(); VBox.setVgrow(spacer,Priority.ALWAYS);
        Label uInfo = new Label("👤  " + currentUser.getFullName());
        uInfo.setFont(Font.font("System",12)); uInfo.setTextFill(Color.web("rgba(255,255,255,0.6)")); uInfo.setPadding(new Insets(0,0,6,10));
        Button btnOut = UIUtils.primaryButton("Log Out", UIUtils.DANGER_COLOR); btnOut.setMaxWidth(Double.MAX_VALUE);

        sb.getChildren().addAll(logo,sep,btnHome,btnStud,btnFac,btnSubj,btnSec,btnAssign,btnAnn,btnLogs,spacer,uInfo,btnOut);

        btnHome.setOnAction(e   -> root.setCenter(wrap(buildHome())));
        btnStud.setOnAction(e   -> root.setCenter(wrap(buildStudentsPanel())));
        btnFac.setOnAction(e    -> root.setCenter(wrap(buildFacultyPanel())));
        btnSubj.setOnAction(e   -> root.setCenter(wrap(buildSubjectsPanel())));
        btnSec.setOnAction(e    -> root.setCenter(wrap(buildSectionsPanel())));
        btnAssign.setOnAction(e -> root.setCenter(wrap(buildAssignmentsPanel())));
        btnAnn.setOnAction(e    -> root.setCenter(wrap(buildAnnouncementsPanel())));
        btnLogs.setOnAction(e   -> root.setCenter(wrap(buildLogsPanel())));
        btnOut.setOnAction(e -> {
            if (UIUtils.showConfirm("Log Out","Are you sure?")) {
                SessionManager.clearSession();
                stage.setScene(new LoginView(stage).getScene());
                stage.setMaximized(false); stage.setWidth(900); stage.setHeight(600); stage.centerOnScreen();
            }
        });
        return sb;
    }

    // ── HOME ─────────────────────────────────────────────────
    private VBox buildHome() {
        VBox panel = new VBox(20); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:" + UIUtils.BG_COLOR + ";");
        Label title = UIUtils.pageTitle("Dashboard Overview");
        Label sub = new Label("Welcome back, " + currentUser.getFullName()); sub.setTextFill(Color.web(UIUtils.TEXT_MUTED));

        int totalStudents = UserService.countByRole("STUDENT");
        int totalFaculty  = UserService.countByRole("FACULTY");
        int totalSubjects = SubjectService.getAllSubjects().size();
        int totalSections = SectionService.getAllSections().size();

        Map<String,Integer> studentsByYear = SectionService.countStudentsByYearLevel();
        Map<String,Integer> sectionsByYear = SectionService.countSectionsByYearLevel();

        VBox studCard = buildBreakdownCard("👨‍🎓  Total Students", String.valueOf(totalStudents), COLOR, studentsByYear);
        VBox secCard  = buildBreakdownCard("🏫  Total Sections",  String.valueOf(totalSections), "#8E44AD", sectionsByYear);
        VBox facCard  = UIUtils.statCard("Total Faculty",  String.valueOf(totalFaculty),  UIUtils.FACULTY_COLOR);
        VBox subjCard = UIUtils.statCard("Total Subjects", String.valueOf(totalSubjects), UIUtils.STUDENT_COLOR);

        HBox row1 = new HBox(16, studCard, secCard);
        HBox row2 = new HBox(16, facCard, subjCard);
        row1.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        row2.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        VBox annCard = UIUtils.card(UIUtils.sectionLabel("📢  Recent Announcements"));
        AnnouncementService.getAllAnnouncements().stream().limit(5).forEach(a -> {
            Label lbl = new Label("• [" + a.getType() + "] " + a.getTitle() + " — " + a.getPostedByName());
            lbl.setFont(Font.font("System",13)); annCard.getChildren().add(lbl);
        });
        if (AnnouncementService.getAllAnnouncements().isEmpty())
            annCard.getChildren().add(new Label("No announcements yet."));

        panel.getChildren().addAll(title,sub,row1,row2,annCard);
        return panel;
    }

    private VBox buildBreakdownCard(String title, String total, String color, Map<String,Integer> breakdown) {
        VBox card = new VBox(6); card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color:white;-fx-background-radius:10;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),8,0,0,2);");
        Label bar = new Label(); bar.setPrefHeight(4); bar.setPrefWidth(40); bar.setStyle("-fx-background-color:"+color+";-fx-background-radius:2;");
        Label val = new Label(total); val.setFont(Font.font("System",FontWeight.BOLD,28)); val.setTextFill(Color.web(color));
        Label titleLbl = new Label(title); titleLbl.setFont(Font.font("System",13)); titleLbl.setTextFill(Color.web(UIUtils.TEXT_MUTED));
        card.getChildren().addAll(bar,val,titleLbl,new Separator());
        if (breakdown.isEmpty()) {
            Label none = new Label("No data yet"); none.setTextFill(Color.web(UIUtils.TEXT_MUTED)); card.getChildren().add(none);
        } else {
            breakdown.forEach((year,count) -> {
                HBox row = new HBox(); row.setAlignment(Pos.CENTER_LEFT);
                Label yl = new Label(year!=null?year:"Unassigned"); yl.setFont(Font.font("System",12)); yl.setTextFill(Color.web(UIUtils.TEXT_MUTED));
                Region sp = new Region(); HBox.setHgrow(sp,Priority.ALWAYS);
                Label cl = new Label(String.valueOf(count)); cl.setFont(Font.font("System",FontWeight.BOLD,12)); cl.setTextFill(Color.web(color));
                row.getChildren().addAll(yl,sp,cl); card.getChildren().add(row);
            });
        }
        return card;
    }

    // ── STUDENTS ─────────────────────────────────────────────
    private VBox buildStudentsPanel() {
        VBox panel = new VBox(16); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";");
        panel.getChildren().add(UIUtils.pageTitle("Students"));

        // Workflow hint
        Label hint = new Label("📋 Workflow: Create Section first → Create Student → Assign Course & Section → Student is auto-enrolled in all subjects of that section.");
        hint.setWrapText(true); hint.setFont(Font.font("System",FontPosture.ITALIC,12)); hint.setTextFill(Color.web(UIUtils.TEXT_MUTED));

        HBox toolbar = new HBox(10); toolbar.setAlignment(Pos.CENTER_LEFT);
        TextField sf = UIUtils.styledField("Search by name, ID, section..."); sf.setPrefWidth(280);
        Button btnSearch = UIUtils.primaryButton("Search",COLOR);
        Button btnCreate = UIUtils.primaryButton("+ Create",COLOR);
        Button btnDelete = UIUtils.dangerButton("Delete");
        Button btnUpdate = UIUtils.outlineButton("Update");
        toolbar.getChildren().addAll(sf,btnSearch,new Separator(Orientation.VERTICAL),btnCreate,btnUpdate,btnDelete);

        TableView<Student> table = UIUtils.styledTable();
        addCol(table,"Student ID","studentId"); addCol(table,"Full Name","fullName");
        addCol(table,"Year Level","yearLevel"); addCol(table,"Course","course");
        addCol(table,"Section","section");     addCol(table,"Contact","contact");
        table.setPrefHeight(420);
        table.setItems(FXCollections.observableArrayList(StudentService.getAllStudents()));

        btnSearch.setOnAction(e -> { String q=sf.getText().trim();
            table.setItems(FXCollections.observableArrayList(q.isEmpty()?StudentService.getAllStudents():StudentService.searchStudents(q))); });
        sf.setOnAction(e -> btnSearch.fire());
        btnCreate.setOnAction(e -> showCreateStudentDialog(table));
        btnUpdate.setOnAction(e -> { Student sel=table.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Selection","Select a student.");return;}
            showUpdateStudentDialog(sel,table); });
        btnDelete.setOnAction(e -> { Student sel=table.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Selection","Select a student.");return;}
            if(UIUtils.showConfirm("Delete","Delete "+sel.getFullName()+"?")){
                UserService.deleteUser(sel.getUserId());
                ActivityLogService.log(currentUser.getId(),"DELETE_STUDENT",sel.getFullName());
                table.setItems(FXCollections.observableArrayList(StudentService.getAllStudents())); } });
        table.setRowFactory(tv -> { TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(e -> { if(e.getClickCount()==2&&!row.isEmpty()) showStudentDetail(row.getItem()); });
            return row; });

        panel.getChildren().addAll(hint,toolbar,table);
        return panel;
    }

    private void showCreateStudentDialog(TableView<Student> table) {
        Stage d = new Stage(); d.initModality(Modality.APPLICATION_MODAL); d.setTitle("Create Student Account");
        VBox form = new VBox(12); form.setPadding(new Insets(24)); form.setPrefWidth(520);
        Label titleLbl = new Label("New Student Account"); titleLbl.setFont(Font.font("System",FontWeight.BOLD,18));

        TextField fUser = UIUtils.styledField("Username");
        PasswordField fPass = UIUtils.styledPasswordField("Password");
        TextField fName = UIUtils.styledField("Full Name");
        TextField fEmail = UIUtils.styledField("Email");
        TextField fContact = UIUtils.styledField("Contact Number");
        TextField fStudId = UIUtils.styledField("Student ID (e.g. 2024-0001)");

        ComboBox<String> fYear = UIUtils.styledCombo("1st Year","2nd Year","3rd Year","4th Year");
        ComboBox<String> fCourse = CourseList.createComboBox();
        fCourse.setPromptText("Select Course...");

        ComboBox<Section> fSection = new ComboBox<>();
        fSection.getItems().addAll(SectionService.getAllSections());
        fSection.setPromptText("Select Section (create one first if empty)");
        fSection.setMaxWidth(Double.MAX_VALUE);

        TextField fGuard = UIUtils.styledField("Guardian Name");
        TextField fGuardC = UIUtils.styledField("Guardian Contact");
        TextArea fAddr = UIUtils.styledTextArea("Address");

        Label enrollInfo = new Label("ℹ️ Student will be auto-enrolled in all subjects assigned to the selected section.");
        enrollInfo.setWrapText(true); enrollInfo.setTextFill(Color.web(UIUtils.FACULTY_COLOR));
        enrollInfo.setFont(Font.font("System",FontPosture.ITALIC,12));

        Button btnSave = UIUtils.primaryButton("Save & Enroll",COLOR);
        Button btnCancel = UIUtils.outlineButton("Cancel");

        form.getChildren().addAll(titleLbl,
            UIUtils.formRow("Username:",    fUser),
            UIUtils.formRow("Password:",    fPass),
            UIUtils.formRow("Full Name:",   fName),
            UIUtils.formRow("Email:",       fEmail),
            UIUtils.formRow("Contact:",     fContact),
            UIUtils.formRow("Student ID:",  fStudId),
            UIUtils.formRow("Year Level:",  fYear),
            UIUtils.formRow("Course:",      fCourse),
            UIUtils.formRow("Section:",     fSection),
            UIUtils.formRow("Guardian:",    fGuard),
            UIUtils.formRow("Guardian Tel:",fGuardC),
            UIUtils.formRow("Address:",     fAddr),
            enrollInfo, new HBox(10,btnSave,btnCancel));

        btnSave.setOnAction(e -> {
            if(fUser.getText().isEmpty()||fPass.getText().isEmpty()||fName.getText().isEmpty()){
                UIUtils.showAlert(Alert.AlertType.WARNING,"Required","Username, password, and name are required."); return; }
            if(AuthService.usernameExists(fUser.getText())){
                UIUtils.showAlert(Alert.AlertType.ERROR,"Taken","Username already exists."); return; }
            if(fSection.getValue()==null){
                UIUtils.showAlert(Alert.AlertType.WARNING,"No Section","Please select a section. Create one first if needed."); return; }

            Section sec = fSection.getValue();
            String course = fCourse.getValue()!=null?fCourse.getValue():"";
            int userId = UserService.createUser(fUser.getText(),fPass.getText(),"STUDENT",
                    fName.getText(),fEmail.getText(),fContact.getText());
            if(userId>0){
                StudentService.createStudent(userId,fStudId.getText(),
                        fYear.getValue()!=null?fYear.getValue():"",
                        course, sec.getSectionName(),
                        fGuard.getText(),fGuardC.getText(),fAddr.getText());

                Student newSt = StudentService.getStudentByUserId(userId);
                int enrolled = 0;
                if(newSt!=null) enrolled = EnrollmentService.autoEnrollInSection(newSt.getId(), sec.getId());

                ActivityLogService.log(currentUser.getId(),"CREATE_STUDENT",fName.getText());
                table.setItems(FXCollections.observableArrayList(StudentService.getAllStudents()));
                d.close();
                if(enrolled>0)
                    UIUtils.showAlert(Alert.AlertType.INFORMATION,"Done",
                        fName.getText()+" created and auto-enrolled in "+enrolled+" subject(s) in "+sec.getSectionName()+".");
                else
                    UIUtils.showAlert(Alert.AlertType.INFORMATION,"Created",
                        fName.getText()+" created. No subjects assigned to "+sec.getSectionName()+" yet — enroll after assigning subjects.");
            }
        });
        btnCancel.setOnAction(e -> d.close());
        d.setScene(new Scene(new ScrollPane(form){{setFitToWidth(true);}})); d.show();
    }

    private void showUpdateStudentDialog(Student s, TableView<Student> table) {
        Stage d = new Stage(); d.initModality(Modality.APPLICATION_MODAL); d.setTitle("Update Student");
        VBox form = new VBox(12); form.setPadding(new Insets(24)); form.setPrefWidth(480);
        TextField fName = UIUtils.styledField("Full Name"); fName.setText(s.getFullName());
        TextField fEmail = UIUtils.styledField("Email"); fEmail.setText(s.getEmail()!=null?s.getEmail():"");
        TextField fContact = UIUtils.styledField("Contact"); fContact.setText(s.getContact()!=null?s.getContact():"");
        ComboBox<String> fCourse = CourseList.createComboBox(); fCourse.setValue(s.getCourse());
        ComboBox<String> fYear = UIUtils.styledCombo("1st Year","2nd Year","3rd Year","4th Year"); fYear.setValue(s.getYearLevel());
        TextField fGuard = UIUtils.styledField("Guardian"); fGuard.setText(s.getGuardianName()!=null?s.getGuardianName():"");
        TextField fGuardC = UIUtils.styledField("Guardian Contact"); fGuardC.setText(s.getGuardianContact()!=null?s.getGuardianContact():"");
        Button btnSave = UIUtils.primaryButton("Save",COLOR); Button btnReset = UIUtils.outlineButton("Reset Password"); Button btnCancel = UIUtils.outlineButton("Cancel");
        form.getChildren().addAll(new Label("Update: "+s.getFullName()),
            UIUtils.formRow("Full Name:",fName), UIUtils.formRow("Email:",fEmail),
            UIUtils.formRow("Contact:",fContact), UIUtils.formRow("Course:",fCourse),
            UIUtils.formRow("Year Level:",fYear), UIUtils.formRow("Guardian:",fGuard),
            UIUtils.formRow("Guardian Tel:",fGuardC), new HBox(10,btnSave,btnReset,btnCancel));
        btnSave.setOnAction(e -> {
            UserService.updateUser(s.getUserId(),fName.getText(),fEmail.getText(),fContact.getText());
            StudentService.updateStudent(s.getId(),fYear.getValue(),fCourse.getValue(),s.getSection(),fGuard.getText(),fGuardC.getText(),s.getAddress());
            ActivityLogService.log(currentUser.getId(),"UPDATE_STUDENT",s.getFullName());
            table.setItems(FXCollections.observableArrayList(StudentService.getAllStudents())); d.close(); });
        btnReset.setOnAction(e -> { TextInputDialog td=new TextInputDialog(); td.setTitle("Reset Password"); td.setHeaderText(null); td.setContentText("New password:");
            td.showAndWait().ifPresent(p -> {if(!p.isEmpty()) AuthService.updatePassword(s.getUserId(),p);}); });
        btnCancel.setOnAction(e -> d.close());
        d.setScene(new Scene(new ScrollPane(form){{setFitToWidth(true);}})); d.show();
    }

    private void showStudentDetail(Student s) {
        Stage w = new Stage(); w.initModality(Modality.APPLICATION_MODAL); w.setTitle("Student — "+s.getFullName());
        VBox content = new VBox(16); content.setPadding(new Insets(24)); content.setPrefWidth(520);
        Label title = new Label("👤  "+s.getFullName()); title.setFont(Font.font("System",FontWeight.BOLD,20));
        VBox info = UIUtils.card(UIUtils.sectionLabel("Profile"),
            new Label("Student ID: "+s.getStudentId()), new Label("Course: "+s.getCourse()),
            new Label("Year: "+s.getYearLevel()), new Label("Section: "+s.getSection()),
            new Label("Contact: "+s.getContact()), new Label("Guardian: "+s.getGuardianName()));
        VBox gradesCard = UIUtils.card(UIUtils.sectionLabel("Grades"));
        GradeService.getGradesByStudent(s.getId()).forEach(g ->
            gradesCard.getChildren().add(new Label(g.getSubjectCode()+": "+fmt(g.getComputedGrade())+" — "+g.getRemarks())));
        if(GradeService.getGradesByStudent(s.getId()).isEmpty()) gradesCard.getChildren().add(new Label("No grades yet."));
        content.getChildren().addAll(title,info,gradesCard);
        w.setScene(new Scene(new ScrollPane(content){{setFitToWidth(true);}})); w.show();
    }

    // ── FACULTY ──────────────────────────────────────────────
    private VBox buildFacultyPanel() {
        VBox panel = new VBox(16); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";");
        panel.getChildren().add(UIUtils.pageTitle("Faculty"));
        HBox toolbar = new HBox(10); toolbar.setAlignment(Pos.CENTER_LEFT);
        Button btnCreate = UIUtils.primaryButton("+ Create",COLOR);
        Button btnDelete = UIUtils.dangerButton("Delete");
        Button btnUpdate = UIUtils.outlineButton("Update");
        toolbar.getChildren().addAll(btnCreate,btnUpdate,btnDelete);
        TableView<Faculty> table = UIUtils.styledTable();
        addCol(table,"Faculty ID","facultyId"); addCol(table,"Full Name","fullName");
        addCol(table,"Department","department"); addCol(table,"Specialization","specialization"); addCol(table,"Contact","contact");
        table.setPrefHeight(400);
        table.setItems(FXCollections.observableArrayList(FacultyService.getAllFaculty()));
        btnCreate.setOnAction(e -> showCreateFacultyDialog(table));
        btnDelete.setOnAction(e -> { Faculty sel=table.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Selection","Select faculty.");return;}
            if(UIUtils.showConfirm("Delete","Delete "+sel.getFullName()+"?")){
                UserService.deleteUser(sel.getUserId()); table.setItems(FXCollections.observableArrayList(FacultyService.getAllFaculty())); } });
        btnUpdate.setOnAction(e -> { Faculty sel=table.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Selection","Select faculty.");return;}
            showUpdateFacultyDialog(sel,table); });
        panel.getChildren().addAll(toolbar,table);
        return panel;
    }

    private void showCreateFacultyDialog(TableView<Faculty> table) {
        Stage d = new Stage(); d.initModality(Modality.APPLICATION_MODAL); d.setTitle("Create Faculty");
        VBox form = new VBox(12); form.setPadding(new Insets(24)); form.setPrefWidth(460);
        TextField fUser=UIUtils.styledField("Username"); PasswordField fPass=UIUtils.styledPasswordField("Password");
        TextField fName=UIUtils.styledField("Full Name"); TextField fEmail=UIUtils.styledField("Email");
        TextField fContact=UIUtils.styledField("Contact"); TextField fFacId=UIUtils.styledField("Faculty ID (e.g. FAC-001)");
        TextField fDept=UIUtils.styledField("Department"); TextField fSpec=UIUtils.styledField("Specialization");
        Button btnSave=UIUtils.primaryButton("Save",COLOR); Button btnCancel=UIUtils.outlineButton("Cancel");
        form.getChildren().addAll(new Label("New Faculty Account"),
            UIUtils.formRow("Username:",fUser), UIUtils.formRow("Password:",fPass),
            UIUtils.formRow("Full Name:",fName), UIUtils.formRow("Email:",fEmail),
            UIUtils.formRow("Contact:",fContact), UIUtils.formRow("Faculty ID:",fFacId),
            UIUtils.formRow("Department:",fDept), UIUtils.formRow("Specialization:",fSpec),
            new HBox(10,btnSave,btnCancel));
        btnSave.setOnAction(e -> {
            if(fUser.getText().isEmpty()||fPass.getText().isEmpty()||fName.getText().isEmpty()){UIUtils.showAlert(Alert.AlertType.WARNING,"Required","Username, password, name required.");return;}
            if(AuthService.usernameExists(fUser.getText())){UIUtils.showAlert(Alert.AlertType.ERROR,"Taken","Username exists.");return;}
            int uid=UserService.createUser(fUser.getText(),fPass.getText(),"FACULTY",fName.getText(),fEmail.getText(),fContact.getText());
            if(uid>0){FacultyService.createFaculty(uid,fFacId.getText(),fDept.getText(),fSpec.getText());
                ActivityLogService.log(currentUser.getId(),"CREATE_FACULTY",fName.getText());
                table.setItems(FXCollections.observableArrayList(FacultyService.getAllFaculty())); d.close(); } });
        btnCancel.setOnAction(e -> d.close()); d.setScene(new Scene(form)); d.show();
    }

    private void showUpdateFacultyDialog(Faculty f, TableView<Faculty> table) {
        Stage d = new Stage(); d.initModality(Modality.APPLICATION_MODAL); d.setTitle("Update Faculty");
        VBox form = new VBox(12); form.setPadding(new Insets(24)); form.setPrefWidth(440);
        TextField fName=UIUtils.styledField("Full Name"); fName.setText(f.getFullName());
        TextField fEmail=UIUtils.styledField("Email"); fEmail.setText(f.getEmail()!=null?f.getEmail():"");
        TextField fContact=UIUtils.styledField("Contact"); fContact.setText(f.getContact()!=null?f.getContact():"");
        TextField fDept=UIUtils.styledField("Department"); fDept.setText(f.getDepartment()!=null?f.getDepartment():"");
        TextField fSpec=UIUtils.styledField("Specialization"); fSpec.setText(f.getSpecialization()!=null?f.getSpecialization():"");
        Button btnSave=UIUtils.primaryButton("Save",COLOR); Button btnReset=UIUtils.outlineButton("Reset Password"); Button btnCancel=UIUtils.outlineButton("Cancel");
        form.getChildren().addAll(UIUtils.formRow("Full Name:",fName), UIUtils.formRow("Email:",fEmail),
            UIUtils.formRow("Contact:",fContact), UIUtils.formRow("Department:",fDept),
            UIUtils.formRow("Specialization:",fSpec), new HBox(10,btnSave,btnReset,btnCancel));
        btnSave.setOnAction(e -> { UserService.updateUser(f.getUserId(),fName.getText(),fEmail.getText(),fContact.getText());
            FacultyService.updateFaculty(f.getId(),fDept.getText(),fSpec.getText());
            table.setItems(FXCollections.observableArrayList(FacultyService.getAllFaculty())); d.close(); });
        btnReset.setOnAction(e -> { TextInputDialog td=new TextInputDialog(); td.setTitle("Reset Password"); td.setHeaderText(null); td.setContentText("New password:");
            td.showAndWait().ifPresent(p -> {if(!p.isEmpty()) AuthService.updatePassword(f.getUserId(),p);}); });
        btnCancel.setOnAction(e -> d.close()); d.setScene(new Scene(form)); d.show();
    }

    // ── SUBJECTS ─────────────────────────────────────────────
    private VBox buildSubjectsPanel() {
        VBox panel = new VBox(16); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";");
        panel.getChildren().add(UIUtils.pageTitle("Subjects"));
        HBox toolbar = new HBox(10); toolbar.setAlignment(Pos.CENTER_LEFT);
        TextField sf=UIUtils.styledField("Search..."); sf.setPrefWidth(240);
        Button btnSearch=UIUtils.primaryButton("Search",COLOR); Button btnCreate=UIUtils.primaryButton("+ Create",COLOR);
        Button btnDelete=UIUtils.dangerButton("Delete"); Button btnUpdate=UIUtils.outlineButton("Update");
        toolbar.getChildren().addAll(sf,btnSearch,new Separator(Orientation.VERTICAL),btnCreate,btnUpdate,btnDelete);
        TableView<Subject> table = UIUtils.styledTable();
        addCol(table,"Code","subjectCode"); addCol(table,"Subject Name","subjectName"); addCol(table,"Units","units"); addCol(table,"Description","description");
        table.setPrefHeight(380);
        table.setItems(FXCollections.observableArrayList(SubjectService.getAllSubjects()));
        btnSearch.setOnAction(e -> { String q=sf.getText().trim(); table.setItems(FXCollections.observableArrayList(q.isEmpty()?SubjectService.getAllSubjects():SubjectService.searchSubjects(q))); });
        btnCreate.setOnAction(e -> {
            Stage d=new Stage(); d.initModality(Modality.APPLICATION_MODAL); d.setTitle("New Subject");
            VBox form=new VBox(12); form.setPadding(new Insets(20)); form.setPrefWidth(400);
            TextField fCode=UIUtils.styledField("Code (e.g. IT101)"); TextField fName=UIUtils.styledField("Subject Name");
            TextField fUnits=UIUtils.styledField("Units"); fUnits.setText("3"); TextArea fDesc=UIUtils.styledTextArea("Description (optional)");
            Button save=UIUtils.primaryButton("Create",COLOR); Button cancel=UIUtils.outlineButton("Cancel");
            form.getChildren().addAll(UIUtils.formRow("Code:",fCode), UIUtils.formRow("Name:",fName),
                UIUtils.formRow("Units:",fUnits), UIUtils.formRow("Description:",fDesc), new HBox(10,save,cancel));
            save.setOnAction(ev -> { if(fCode.getText().isEmpty()||fName.getText().isEmpty()){UIUtils.showAlert(Alert.AlertType.WARNING,"Required","Code and name required.");return;}
                int u=3; try{u=Integer.parseInt(fUnits.getText());}catch(Exception ignored){}
                SubjectService.createSubject(fCode.getText(),fName.getText(),u,fDesc.getText());
                table.setItems(FXCollections.observableArrayList(SubjectService.getAllSubjects())); d.close(); });
            cancel.setOnAction(ev -> d.close()); d.setScene(new Scene(form)); d.show(); });
        btnDelete.setOnAction(e -> { Subject sel=table.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Selection","Select subject.");return;}
            if(UIUtils.showConfirm("Delete","Delete "+sel.getSubjectName()+"?")){SubjectService.deleteSubject(sel.getId()); table.setItems(FXCollections.observableArrayList(SubjectService.getAllSubjects())); } });
        btnUpdate.setOnAction(e -> { Subject sel=table.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Selection","Select subject.");return;}
            Stage d=new Stage(); d.initModality(Modality.APPLICATION_MODAL); d.setTitle("Update Subject");
            VBox form=new VBox(12); form.setPadding(new Insets(20)); form.setPrefWidth(400);
            TextField fCode=UIUtils.styledField("Code"); fCode.setText(sel.getSubjectCode());
            TextField fName=UIUtils.styledField("Name"); fName.setText(sel.getSubjectName());
            TextField fUnits=UIUtils.styledField("Units"); fUnits.setText(String.valueOf(sel.getUnits()));
            TextArea fDesc=UIUtils.styledTextArea("Desc"); fDesc.setText(sel.getDescription()!=null?sel.getDescription():"");
            Button save=UIUtils.primaryButton("Save",COLOR); Button cancel=UIUtils.outlineButton("Cancel");
            form.getChildren().addAll(UIUtils.formRow("Code:",fCode), UIUtils.formRow("Name:",fName),
                UIUtils.formRow("Units:",fUnits), UIUtils.formRow("Desc:",fDesc), new HBox(10,save,cancel));
            save.setOnAction(ev -> { int u=sel.getUnits(); try{u=Integer.parseInt(fUnits.getText());}catch(Exception ignored){}
                SubjectService.updateSubject(sel.getId(),fCode.getText(),fName.getText(),u,fDesc.getText());
                table.setItems(FXCollections.observableArrayList(SubjectService.getAllSubjects())); d.close(); });
            cancel.setOnAction(ev -> d.close()); d.setScene(new Scene(form)); d.show(); });
        panel.getChildren().addAll(toolbar,table);
        return panel;
    }

    // ── SECTIONS ─────────────────────────────────────────────
    private VBox buildSectionsPanel() {
        VBox panel = new VBox(16); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";");
        panel.getChildren().add(UIUtils.pageTitle("Sections"));

        Label hint = new Label("💡 Click a section to see enrolled students. Use 'Assign Subject' to add subjects to a section before enrolling students.");
        hint.setWrapText(true); hint.setFont(Font.font("System",FontPosture.ITALIC,12)); hint.setTextFill(Color.web(UIUtils.TEXT_MUTED));

        HBox toolbar = new HBox(10); toolbar.setAlignment(Pos.CENTER_LEFT);
        Button btnCreate = UIUtils.primaryButton("+ Create Section",COLOR);
        Button btnDelete = UIUtils.dangerButton("Delete Section");
        Button btnAssignSubj = UIUtils.primaryButton("Assign Subject to Section", UIUtils.FACULTY_COLOR);
        Button btnEnrollStud = UIUtils.primaryButton("Enroll Student to Section", UIUtils.STUDENT_COLOR);
        toolbar.getChildren().addAll(btnCreate,btnDelete,btnAssignSubj,btnEnrollStud);

        TableView<Section> secTable = UIUtils.styledTable();
        addCol(secTable,"Section","sectionName"); addCol(secTable,"Year Level","yearLevel");
        addCol(secTable,"Course","course"); addCol(secTable,"School Year","schoolYear"); addCol(secTable,"Semester","semester");
        secTable.setPrefHeight(230);
        secTable.setItems(FXCollections.observableArrayList(SectionService.getAllSections()));

        // Subjects assigned to this section
        Label subjHeader = UIUtils.sectionLabel("Subjects in Selected Section");
        TableView<FacultyAssignment> subjTable = UIUtils.styledTable();
        addCol(subjTable,"Subject Code","subjectCode"); addCol(subjTable,"Subject Name","subjectName");
        addCol(subjTable,"Teacher","facultyName"); addCol(subjTable,"Schedule","schedule"); addCol(subjTable,"Room","room");
        subjTable.setPrefHeight(160);

        // Students enrolled in this section
        Label studHeader = UIUtils.sectionLabel("Students in Selected Section");
        TableView<Student> studTable = UIUtils.styledTable();
        addCol(studTable,"Student ID","studentId"); addCol(studTable,"Full Name","fullName");
        addCol(studTable,"Year Level","yearLevel"); addCol(studTable,"Course","course");
        studTable.setPrefHeight(200);

        secTable.getSelectionModel().selectedItemProperty().addListener((obs,old,sel) -> {
            if(sel==null) return;
            subjHeader.setText("Subjects in: "+sel.getSectionName());
            subjTable.setItems(FXCollections.observableArrayList(AssignmentService.getAssignmentsBySection(sel.getId())));
            studHeader.setText("Students in: "+sel.getSectionName());
            studTable.setItems(FXCollections.observableArrayList(SectionService.getStudentsBySection(sel.getId())));
        });

        btnCreate.setOnAction(e -> {
            Stage d=new Stage(); d.initModality(Modality.APPLICATION_MODAL); d.setTitle("New Section");
            VBox form=new VBox(12); form.setPadding(new Insets(20)); form.setPrefWidth(420);
            TextField fName=UIUtils.styledField("Section Name (e.g. BSIT-1A)");
            ComboBox<String> fYear=UIUtils.styledCombo("1st Year","2nd Year","3rd Year","4th Year");
            ComboBox<String> fCourse=CourseList.createComboBox(); fCourse.setPromptText("Select Course...");
            TextField fSY=UIUtils.styledField("School Year (e.g. 2024-2025)");
            ComboBox<String> fSem=UIUtils.styledCombo("1ST","2ND","SUMMER");
            Button save=UIUtils.primaryButton("Create",COLOR); Button cancel=UIUtils.outlineButton("Cancel");
            form.getChildren().addAll(UIUtils.formRow("Section Name:",fName), UIUtils.formRow("Year Level:",fYear),
                UIUtils.formRow("Course:",fCourse), UIUtils.formRow("School Year:",fSY),
                UIUtils.formRow("Semester:",fSem), new HBox(10,save,cancel));
            save.setOnAction(ev -> {
                if(fName.getText().isEmpty()){UIUtils.showAlert(Alert.AlertType.WARNING,"Required","Section name required.");return;}
                SectionService.createSection(fName.getText(),fYear.getValue(),fCourse.getValue(),fSY.getText(),fSem.getValue()!=null?fSem.getValue():"1ST");
                secTable.setItems(FXCollections.observableArrayList(SectionService.getAllSections())); d.close(); });
            cancel.setOnAction(ev -> d.close()); d.setScene(new Scene(form)); d.show(); });

        btnDelete.setOnAction(e -> { Section sel=secTable.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Selection","Select a section.");return;}
            if(UIUtils.showConfirm("Delete","Delete section "+sel.getSectionName()+"?")){
                SectionService.deleteSection(sel.getId());
                secTable.setItems(FXCollections.observableArrayList(SectionService.getAllSections()));
                subjTable.getItems().clear(); studTable.getItems().clear(); } });

        btnAssignSubj.setOnAction(e -> { Section sel=secTable.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Section","Select a section first.");return;}
            showAssignSubjectToSectionDialog(sel,subjTable); });

        btnEnrollStud.setOnAction(e -> { Section sel=secTable.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Section","Select a section first.");return;}
            showEnrollStudentDialog(sel,studTable); });

        panel.getChildren().addAll(hint,toolbar,secTable,subjHeader,subjTable,studHeader,studTable);
        return panel;
    }

    private void showAssignSubjectToSectionDialog(Section section, TableView<FacultyAssignment> subjTable) {
        Stage d=new Stage(); d.initModality(Modality.APPLICATION_MODAL);
        d.setTitle("Assign Subject to Section: "+section.getSectionName());
        VBox form=new VBox(14); form.setPadding(new Insets(24)); form.setPrefWidth(460);
        Label titleLbl=new Label("Assign Subject → "+section.getSectionName()); titleLbl.setFont(Font.font("System",FontWeight.BOLD,16));
        Label note=new Label("This adds a subject to the section. A teacher will be assigned in the Assignments panel.");
        note.setWrapText(true); note.setTextFill(Color.web(UIUtils.TEXT_MUTED)); note.setFont(Font.font("System",FontPosture.ITALIC,12));
        ComboBox<Subject> fSubj=new ComboBox<>(FXCollections.observableArrayList(SubjectService.getAllSubjects()));
        fSubj.setPromptText("Select Subject..."); fSubj.setMaxWidth(Double.MAX_VALUE);
        ComboBox<Faculty> fFac=new ComboBox<>(FXCollections.observableArrayList(FacultyService.getAllFaculty()));
        fFac.setPromptText("Assign Teacher (optional now)..."); fFac.setMaxWidth(Double.MAX_VALUE);
        TextField fSched=UIUtils.styledField("Schedule (e.g. MWF 8:00-9:00 AM)");
        TextField fRoom=UIUtils.styledField("Room (e.g. Room 101)");
        TextField fSY=UIUtils.styledField("School Year"); fSY.setText("2024-2025");
        ComboBox<String> fSem=UIUtils.styledCombo("1ST","2ND","SUMMER"); fSem.setValue("1ST");
        Button btnSave=UIUtils.primaryButton("Assign Subject",UIUtils.FACULTY_COLOR); Button btnCancel=UIUtils.outlineButton("Cancel");
        form.getChildren().addAll(titleLbl,note,
            UIUtils.formRow("Subject:",fSubj), UIUtils.formRow("Teacher:",fFac),
            UIUtils.formRow("Schedule:",fSched), UIUtils.formRow("Room:",fRoom),
            UIUtils.formRow("School Year:",fSY), UIUtils.formRow("Semester:",fSem),
            new HBox(10,btnSave,btnCancel));
        btnSave.setOnAction(ev -> {
            if(fSubj.getValue()==null){UIUtils.showAlert(Alert.AlertType.WARNING,"Required","Select a subject.");return;}
            if(fFac.getValue()==null){UIUtils.showAlert(Alert.AlertType.WARNING,"Required","Select a teacher.");return;}
            boolean ok=AssignmentService.createAssignment(fFac.getValue().getId(),fSubj.getValue().getId(),
                section.getId(),fSched.getText(),fRoom.getText(),fSY.getText(),fSem.getValue()!=null?fSem.getValue():"1ST");
            if(ok){
                ActivityLogService.log(currentUser.getId(),"ASSIGN_SUBJECT",
                    fSubj.getValue().getSubjectCode()+" → "+section.getSectionName());
                subjTable.setItems(FXCollections.observableArrayList(AssignmentService.getAssignmentsBySection(section.getId())));
                // Re-enroll any students already in this section into the new subject
                List<Student> existing=SectionService.getStudentsBySection(section.getId());
                existing.forEach(st -> EnrollmentService.enrollStudent(st.getId(),
                    AssignmentService.getAssignment(section.getId(),fSubj.getValue().getId()).getId()));
                UIUtils.showAlert(Alert.AlertType.INFORMATION,"Done","Subject assigned. "+existing.size()+" existing student(s) enrolled.");
                d.close();
            }
        });
        btnCancel.setOnAction(ev -> d.close());
        d.setScene(new Scene(new ScrollPane(form){{setFitToWidth(true);}})); d.show();
    }

    private void showEnrollStudentDialog(Section section, TableView<Student> studTable) {
        Stage d=new Stage(); d.initModality(Modality.APPLICATION_MODAL);
        d.setTitle("Enroll Student to: "+section.getSectionName());
        VBox form=new VBox(14); form.setPadding(new Insets(24)); form.setPrefWidth(440);
        Label titleLbl=new Label("Enroll Student → "+section.getSectionName()); titleLbl.setFont(Font.font("System",FontWeight.BOLD,16));
        List<FacultyAssignment> sectionSubjects=AssignmentService.getAssignmentsBySection(section.getId());
        if(sectionSubjects.isEmpty()){
            form.getChildren().addAll(titleLbl,
                new Label("⚠️ No subjects assigned to this section yet.\nPlease assign subjects first using 'Assign Subject to Section'."));
            Button close=UIUtils.outlineButton("Close"); close.setOnAction(ev->d.close());
            form.getChildren().add(close); d.setScene(new Scene(form)); d.show(); return;
        }
        Label info=new Label("This student will be auto-enrolled in "+sectionSubjects.size()+" subject(s):\n"+
            sectionSubjects.stream().map(a->a.getSubjectCode()+" — "+a.getSubjectName()).reduce("",(a,b)->a+(a.isEmpty()?"":"  |  ")+b));
        info.setWrapText(true); info.setTextFill(Color.web(UIUtils.FACULTY_COLOR)); info.setFont(Font.font("System",FontPosture.ITALIC,12));
        ComboBox<Student> fStud=new ComboBox<>(FXCollections.observableArrayList(StudentService.getAllStudents()));
        fStud.setPromptText("Select Student..."); fStud.setMaxWidth(Double.MAX_VALUE);
        Button btnEnroll=UIUtils.primaryButton("Enroll",COLOR); Button btnCancel=UIUtils.outlineButton("Cancel");
        form.getChildren().addAll(titleLbl,info,UIUtils.formRow("Student:",fStud),new HBox(10,btnEnroll,btnCancel));
        btnEnroll.setOnAction(ev -> {
            if(fStud.getValue()==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Student","Select a student.");return;}
            int count=EnrollmentService.autoEnrollInSection(fStud.getValue().getId(),section.getId());
            ActivityLogService.log(currentUser.getId(),"ENROLL_STUDENT",fStud.getValue().getFullName()+" → "+section.getSectionName());
            studTable.setItems(FXCollections.observableArrayList(SectionService.getStudentsBySection(section.getId())));
            UIUtils.showAlert(Alert.AlertType.INFORMATION,"Enrolled",fStud.getValue().getFullName()+" enrolled in "+count+" subject(s)."); d.close(); });
        btnCancel.setOnAction(ev -> d.close()); d.setScene(new Scene(form)); d.show();
    }

    // ── ASSIGNMENTS ──────────────────────────────────────────
    private VBox buildAssignmentsPanel() {
        VBox panel = new VBox(16); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";");
        panel.getChildren().add(UIUtils.pageTitle("Faculty Assignments"));

        Label hint=new Label("📋 Workflow: Select Section → subjects in that section appear → select subject → select teacher → save.");
        hint.setWrapText(true); hint.setFont(Font.font("System",FontPosture.ITALIC,12)); hint.setTextFill(Color.web(UIUtils.TEXT_MUTED));

        // Step 1: Section picker
        Label step1=new Label("Step 1 — Select Section:"); step1.setFont(Font.font("System",FontWeight.BOLD,13));
        ComboBox<Section> sectionPicker=new ComboBox<>(FXCollections.observableArrayList(SectionService.getAllSections()));
        sectionPicker.setPromptText("Select section..."); sectionPicker.setMaxWidth(400);

        // Step 2: Shows subjects in that section
        Label step2=new Label("Step 2 — Subjects in this section (select one):"); step2.setFont(Font.font("System",FontWeight.BOLD,13));
        TableView<FacultyAssignment> sectionSubjTable=UIUtils.styledTable();
        addCol(sectionSubjTable,"Subject Code","subjectCode"); addCol(sectionSubjTable,"Subject Name","subjectName");
        addCol(sectionSubjTable,"Current Teacher","facultyName"); addCol(sectionSubjTable,"Schedule","schedule");
        sectionSubjTable.setPrefHeight(180);

        sectionPicker.setOnAction(e -> {
            Section sel=sectionPicker.getValue();
            if(sel!=null) sectionSubjTable.setItems(FXCollections.observableArrayList(AssignmentService.getAssignmentsBySection(sel.getId())));
        });

        // Step 3: Change/assign teacher + schedule
        Label step3=new Label("Step 3 — Assign/Update Teacher & Schedule:"); step3.setFont(Font.font("System",FontWeight.BOLD,13));
        ComboBox<Faculty> facPicker=new ComboBox<>(FXCollections.observableArrayList(FacultyService.getAllFaculty()));
        facPicker.setPromptText("Select teacher..."); facPicker.setMaxWidth(400);
        TextField fSched=UIUtils.styledField("Schedule (e.g. MWF 8:00-9:00 AM)"); fSched.setMaxWidth(400);
        TextField fRoom=UIUtils.styledField("Room"); fRoom.setMaxWidth(400);

        // Auto-fill when assignment is selected
        sectionSubjTable.getSelectionModel().selectedItemProperty().addListener((obs,old,sel) -> {
            if(sel==null) return;
            if(sel.getSchedule()!=null) fSched.setText(sel.getSchedule());
            if(sel.getRoom()!=null) fRoom.setText(sel.getRoom());
        });

        Button btnSave=UIUtils.primaryButton("Save Assignment",COLOR);
        Button btnDelete=UIUtils.dangerButton("Remove Assignment");
        HBox btnRow=new HBox(10,btnSave,btnDelete); btnRow.setAlignment(Pos.CENTER_LEFT);

        // Full assignments list
        Label allHeader=UIUtils.sectionLabel("All Current Assignments");
        TableView<FacultyAssignment> allTable=UIUtils.styledTable();
        addCol(allTable,"Faculty","facultyName"); addCol(allTable,"Subject","subjectName");
        addCol(allTable,"Section","sectionName"); addCol(allTable,"Schedule","schedule"); addCol(allTable,"Room","room");
        allTable.setPrefHeight(280);
        allTable.setItems(FXCollections.observableArrayList(AssignmentService.getAllAssignments()));

        btnSave.setOnAction(e -> {
            Section sec=sectionPicker.getValue();
            FacultyAssignment selAssign=sectionSubjTable.getSelectionModel().getSelectedItem();
            Faculty fac=facPicker.getValue();
            if(sec==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Section","Select a section.");return;}
            if(selAssign==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Subject","Select a subject from the list.");return;}
            if(fac==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Teacher","Select a teacher.");return;}
            // Update the existing assignment with the new teacher + schedule
            String sql="UPDATE faculty_assignments SET faculty_id=?,schedule=?,room=? WHERE id=?";
            try(java.sql.Connection conn=com.smis.database.DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps=conn.prepareStatement(sql)){
                ps.setInt(1,fac.getId()); ps.setString(2,fSched.getText());
                ps.setString(3,fRoom.getText()); ps.setInt(4,selAssign.getId());
                ps.executeUpdate();
            }catch(Exception ex){System.err.println("[Assign] "+ex.getMessage());}
            ActivityLogService.log(currentUser.getId(),"UPDATE_ASSIGNMENT",
                fac.getFullName()+" → "+selAssign.getSubjectCode()+" ("+sec.getSectionName()+")");
            sectionSubjTable.setItems(FXCollections.observableArrayList(AssignmentService.getAssignmentsBySection(sec.getId())));
            allTable.setItems(FXCollections.observableArrayList(AssignmentService.getAllAssignments()));
            UIUtils.showAlert(Alert.AlertType.INFORMATION,"Saved",
                fac.getFullName()+" assigned to "+selAssign.getSubjectCode()+" in "+sec.getSectionName()+". Faculty dashboard will auto-update.");
        });

        btnDelete.setOnAction(e -> {
            FacultyAssignment sel=sectionSubjTable.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Selection","Select an assignment.");return;}
            if(UIUtils.showConfirm("Remove","Remove this assignment?")){
                AssignmentService.deleteAssignment(sel.getId());
                Section sec=sectionPicker.getValue();
                if(sec!=null) sectionSubjTable.setItems(FXCollections.observableArrayList(AssignmentService.getAssignmentsBySection(sec.getId())));
                allTable.setItems(FXCollections.observableArrayList(AssignmentService.getAllAssignments()));
            }
        });

        panel.getChildren().addAll(hint,step1,sectionPicker,step2,sectionSubjTable,step3,
            UIUtils.formRow("Teacher:",facPicker), UIUtils.formRow("Schedule:",fSched),
            UIUtils.formRow("Room:",fRoom), btnRow, allHeader, allTable);
        return panel;
    }

    // ── ANNOUNCEMENTS ─────────────────────────────────────────
    private VBox buildAnnouncementsPanel() {
        VBox panel=new VBox(16); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";");
        panel.getChildren().add(UIUtils.pageTitle("Announcements"));
        Button btnPost=UIUtils.primaryButton("+ Post",COLOR); Button btnEdit=UIUtils.outlineButton("Edit"); Button btnDelete=UIUtils.dangerButton("Delete");
        HBox toolbar=new HBox(10,btnPost,btnEdit,btnDelete); toolbar.setAlignment(Pos.CENTER_LEFT);
        TableView<Announcement> table=UIUtils.styledTable();
        addCol(table,"Type","type"); addCol(table,"Title","title"); addCol(table,"Posted By","postedByName");
        table.setPrefHeight(400);
        table.setItems(FXCollections.observableArrayList(AnnouncementService.getAllAnnouncements()));
        table.getSelectionModel().selectedItemProperty().addListener((obs,old,sel)->{if(sel!=null) showAnnouncementDetail(sel);});
        btnPost.setOnAction(e -> showPostAnnouncementDialog(table,null));
        btnEdit.setOnAction(e -> { Announcement sel=table.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Selection","Select announcement.");return;}
            if(sel.getPostedBy()!=currentUser.getId()){UIUtils.showAlert(Alert.AlertType.ERROR,"Not Allowed","Only the poster can edit.");return;}
            showPostAnnouncementDialog(table,sel); });
        btnDelete.setOnAction(e -> { Announcement sel=table.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Selection","Select announcement.");return;}
            if(sel.getPostedBy()!=currentUser.getId()){UIUtils.showAlert(Alert.AlertType.ERROR,"Not Allowed","Only the poster can delete.");return;}
            if(UIUtils.showConfirm("Delete","Delete this announcement?")){
                AnnouncementService.deleteAnnouncement(sel.getId()); table.setItems(FXCollections.observableArrayList(AnnouncementService.getAllAnnouncements())); } });
        panel.getChildren().addAll(toolbar,table);
        return panel;
    }

    private void showAnnouncementDetail(Announcement a){
        Stage w=new Stage(); w.initModality(Modality.NONE); w.setTitle("📢 "+a.getTitle());
        VBox content=new VBox(16); content.setPadding(new Insets(30)); content.setPrefWidth(560);
        Label titleLbl=new Label(a.getTitle()); titleLbl.setFont(Font.font("System",FontWeight.BOLD,22)); titleLbl.setWrapText(true);
        try{titleLbl.setTextFill(Color.web(a.getTextColor()!=null?a.getTextColor():"#2C3E50"));}catch(Exception ignored){}
        Label meta=new Label("Posted by: "+a.getPostedByName()+(a.getCreatedAt()!=null?"   |   "+a.getCreatedAt().toLocalDate():""));
        meta.setTextFill(Color.web(UIUtils.TEXT_MUTED)); meta.setFont(Font.font("System",12));
        if(a.getImageBase64()!=null&&!a.getImageBase64().isEmpty()){
            try{ImageView iv=new ImageView(new Image(new ByteArrayInputStream(Base64.getDecoder().decode(a.getImageBase64()))));
                iv.setFitWidth(500); iv.setPreserveRatio(true); content.getChildren().add(iv);}catch(Exception ignored){}
        }
        Label body=new Label(a.getContent()); body.setWrapText(true);
        body.setFont(Font.font("System",a.getFontSize()>0?a.getFontSize():14));
        try{body.setTextFill(Color.web(a.getTextColor()!=null?a.getTextColor():"#2C3E50"));}catch(Exception ignored){}
        content.getChildren().addAll(titleLbl,meta,new Separator(),body);
        w.setScene(new Scene(new ScrollPane(content){{setFitToWidth(true);}})); w.show();
    }

    private void showPostAnnouncementDialog(TableView<Announcement> table,Announcement existing){
        boolean isEdit=existing!=null;
        Stage d=new Stage(); d.initModality(Modality.APPLICATION_MODAL); d.setTitle(isEdit?"Edit Announcement":"Post Announcement");
        VBox form=new VBox(14); form.setPadding(new Insets(24)); form.setPrefWidth(520);
        TextField fTitle=UIUtils.styledField("Title"); if(isEdit) fTitle.setText(existing.getTitle());
        TextArea fContent=UIUtils.styledTextArea("Content..."); fContent.setPrefRowCount(6); if(isEdit) fContent.setText(existing.getContent());
        ComboBox<Integer> fFont=new ComboBox<>(); fFont.getItems().addAll(10,12,13,14,16,18,20,22,24,28,32); fFont.setValue(isEdit?existing.getFontSize():14);
        ComboBox<String> fColor=new ComboBox<>(); fColor.getItems().addAll("#2C3E50","#1A73E8","#E74C3C","#1E8449","#E67E22","#8E44AD","#FFFFFF"); fColor.setValue(isEdit&&existing.getTextColor()!=null?existing.getTextColor():"#2C3E50");
        final String[] imgHolder={isEdit?existing.getImageBase64():null};
        Label imgName=new Label(isEdit&&existing.getImageBase64()!=null?"Image attached":"No image"); imgName.setTextFill(Color.web(UIUtils.TEXT_MUTED));
        Button btnImg=UIUtils.outlineButton("Choose Image...");
        btnImg.setOnAction(ev -> {
            FileChooser fc=new FileChooser(); fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images","*.png","*.jpg","*.jpeg"));
            File file=fc.showOpenDialog(d);
            if(file!=null){try{imgHolder[0]=Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath())); imgName.setText(file.getName());}catch(Exception ex){UIUtils.showAlert(Alert.AlertType.ERROR,"Error","Could not load image.");}} });
        Button btnSave=UIUtils.primaryButton(isEdit?"Save":"Post",COLOR); Button btnCancel=UIUtils.outlineButton("Cancel");
        form.getChildren().addAll(UIUtils.formRow("Title:",fTitle), UIUtils.formRow("Content:",fContent),
            UIUtils.formRow("Font Size:",fFont), UIUtils.formRow("Text Color:",fColor),
            new HBox(10,btnImg,imgName), new HBox(10,btnSave,btnCancel));
        btnSave.setOnAction(ev -> {
            if(fTitle.getText().isEmpty()){UIUtils.showAlert(Alert.AlertType.WARNING,"Required","Title required.");return;}
            if(isEdit) AnnouncementService.updateAnnouncement(existing.getId(),fTitle.getText(),fContent.getText(),fFont.getValue(),fColor.getValue(),imgHolder[0]);
            else{ AnnouncementService.createAnnouncement(fTitle.getText(),fContent.getText(),fFont.getValue(),fColor.getValue(),imgHolder[0],currentUser.getId(),"GENERAL",null);
                ActivityLogService.log(currentUser.getId(),"POST_ANNOUNCEMENT",fTitle.getText()); }
            table.setItems(FXCollections.observableArrayList(AnnouncementService.getAllAnnouncements())); d.close(); });
        btnCancel.setOnAction(ev -> d.close());
        d.setScene(new Scene(new ScrollPane(form){{setFitToWidth(true);}})); d.show();
    }

    // ── LOGS ─────────────────────────────────────────────────
    private VBox buildLogsPanel(){
        VBox panel=new VBox(16); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";");
        panel.getChildren().add(UIUtils.pageTitle("Activity Logs"));
        TableView<ActivityLog> table=UIUtils.styledTable();
        addCol(table,"User","username"); addCol(table,"Action","action"); addCol(table,"Details","details"); addCol(table,"Time","loggedAt");
        table.setPrefHeight(520);
        table.setItems(FXCollections.observableArrayList(ActivityLogService.getRecentLogs(300)));
        panel.getChildren().add(table); return panel;
    }

    private <T> void addCol(TableView<T> t,String h,String p){
        TableColumn<T,String> c=new TableColumn<>(h); c.setCellValueFactory(new PropertyValueFactory<>(p)); t.getColumns().add(c); }
    private String fmt(Double d){return d!=null?String.valueOf(d):"—";}
}
