package com.smis.views.faculty;

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
import java.time.LocalDate;
import java.util.*;
import java.util.Base64;
import java.util.stream.Collectors;

public class FacultyDashboard {

    private Stage stage;
    private BorderPane root;
    private User currentUser;
    private Faculty faculty;

    private static final String COLOR = UIUtils.FACULTY_COLOR;

    public FacultyDashboard(Stage stage) {
        this.stage = stage;
        this.currentUser = SessionManager.getCurrentUser();
        this.faculty = FacultyService.getFacultyByUserId(currentUser.getId());
    }

    /** Always fetch fresh assignments from DB */
    private List<FacultyAssignment> getMyAssignments() {
        return faculty != null ? AssignmentService.getAssignmentsByFaculty(faculty.getId()) : List.of();
    }

    /** Get distinct sections from my assignments */
    private List<Section> getMySections() {
        List<FacultyAssignment> assignments = getMyAssignments();
        Map<Integer,Section> seen = new LinkedHashMap<>();
        for (FacultyAssignment a : assignments) {
            if (!seen.containsKey(a.getSectionId())) {
                Section s = SectionService.getSectionById(a.getSectionId());
                if (s != null) seen.put(a.getSectionId(), s);
            }
        }
        return new ArrayList<>(seen.values());
    }

    public void show() {
        root = new BorderPane();
        root.setLeft(buildSidebar());
        root.setCenter(wrap(buildHome()));
        stage.setScene(new Scene(root, 1280, 760));
        stage.setTitle("SMIS — Faculty Dashboard");
        stage.setMaximized(true);
        stage.show();
    }

    private ScrollPane wrap(javafx.scene.Node n) {
        ScrollPane sp = new ScrollPane(n); sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";-fx-background:"+UIUtils.BG_COLOR+";");
        return sp;
    }

    private VBox buildSidebar() {
        VBox sb = new VBox(4); sb.setPrefWidth(240); sb.setPadding(new Insets(0,10,20,10));
        sb.setStyle("-fx-background-color:"+UIUtils.SIDEBAR_DARK+";");
        VBox logo=new VBox(4); logo.setPadding(new Insets(24,10,20,10));
        Label lt=new Label("SMIS"); lt.setFont(Font.font("System",FontWeight.BOLD,26)); lt.setTextFill(Color.WHITE);
        Label lr=new Label("Faculty"); lr.setTextFill(Color.web("rgba(255,255,255,0.5)")); lr.setFont(Font.font("System",12));
        logo.getChildren().addAll(lt,lr);
        Separator sep=new Separator(); sep.setStyle("-fx-background-color:rgba(255,255,255,0.1);");
        Button btnHome=UIUtils.navButton("Dashboard","🏠");
        Button btnSubj=UIUtils.navButton("My Subjects","📚");
        Button btnStuds=UIUtils.navButton("My Students","👨‍🎓");
        Button btnGrades=UIUtils.navButton("Class Records","📊");
        Button btnAttend=UIUtils.navButton("Attendance","✅");
        Button btnAnn=UIUtils.navButton("Announcements","📢");
        Button btnProfile=UIUtils.navButton("My Profile","👤");
        Region spacer=new Region(); VBox.setVgrow(spacer,Priority.ALWAYS);
        Label uInfo=new Label("👤  "+currentUser.getFullName());
        uInfo.setFont(Font.font("System",12)); uInfo.setTextFill(Color.web("rgba(255,255,255,0.6)")); uInfo.setPadding(new Insets(0,0,6,10));
        Button btnOut=UIUtils.primaryButton("Log Out",UIUtils.DANGER_COLOR); btnOut.setMaxWidth(Double.MAX_VALUE);
        sb.getChildren().addAll(logo,sep,btnHome,btnSubj,btnStuds,btnGrades,btnAttend,btnAnn,btnProfile,spacer,uInfo,btnOut);
        btnHome.setOnAction(e   -> root.setCenter(wrap(buildHome())));
        btnSubj.setOnAction(e   -> root.setCenter(wrap(buildSubjectsPanel())));
        btnStuds.setOnAction(e  -> root.setCenter(wrap(buildStudentsPanel())));
        btnGrades.setOnAction(e -> root.setCenter(wrap(buildClassRecordsPanel())));
        btnAttend.setOnAction(e -> root.setCenter(wrap(buildAttendancePanel())));
        btnAnn.setOnAction(e    -> root.setCenter(wrap(buildAnnouncementsPanel())));
        btnProfile.setOnAction(e-> root.setCenter(wrap(buildProfilePanel())));
        btnOut.setOnAction(e -> {
            if(UIUtils.showConfirm("Log Out","Are you sure?")){
                SessionManager.clearSession(); stage.setScene(new LoginView(stage).getScene());
                stage.setMaximized(false); stage.setWidth(900); stage.setHeight(600); stage.centerOnScreen(); } });
        return sb;
    }

    // ── HOME ─────────────────────────────────────────────────
    private VBox buildHome() {
        VBox panel=new VBox(24); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";");
        Label title=UIUtils.pageTitle("Faculty Dashboard");
        Label sub=new Label("Welcome, "+currentUser.getFullName()); sub.setTextFill(Color.web(UIUtils.TEXT_MUTED));

        List<FacultyAssignment> myAssignments=getMyAssignments();
        List<Section> mySections=getMySections();
        Set<Integer> studentIds=new HashSet<>();
        myAssignments.forEach(a -> EnrollmentService.getEnrollmentsByAssignment(a.getId())
                .forEach(en -> studentIds.add(en.getStudentId())));

        HBox stats=new HBox(16);
        stats.getChildren().addAll(
            UIUtils.statCard("Assigned Subjects",String.valueOf(myAssignments.size()),COLOR),
            UIUtils.statCard("My Sections",String.valueOf(mySections.size()),"#8E44AD"),
            UIUtils.statCard("Total Students",String.valueOf(studentIds.size()),UIUtils.ADMIN_COLOR),
            UIUtils.statCard("Faculty ID",faculty!=null?faculty.getFacultyId():"—",UIUtils.STUDENT_COLOR)
        );
        stats.getChildren().forEach(n -> HBox.setHgrow(n,Priority.ALWAYS));

        // Sections + subjects summary
        VBox summCard=UIUtils.card(UIUtils.sectionLabel("📚  My Sections & Subjects"));
        if(myAssignments.isEmpty()){
            Label none=new Label("No assignments yet. Your dashboard will populate once admin assigns subjects to your sections.");
            none.setWrapText(true); none.setTextFill(Color.web(UIUtils.TEXT_MUTED));
            summCard.getChildren().add(none);
        } else {
            mySections.forEach(sec -> {
                Label secLbl=new Label("🏫  "+sec.getSectionName()+" — "+sec.getCourse()+" ("+sec.getYearLevel()+")");
                secLbl.setFont(Font.font("System",FontWeight.BOLD,13));
                summCard.getChildren().add(secLbl);
                myAssignments.stream().filter(a -> a.getSectionId()==sec.getId()).forEach(a -> {
                    Label subjLbl=new Label("    • "+a.getSubjectCode()+" — "+a.getSubjectName()+
                        (a.getSchedule()!=null?"  |  "+a.getSchedule():""));
                    subjLbl.setFont(Font.font("System",12)); subjLbl.setTextFill(Color.web(UIUtils.TEXT_MUTED));
                    summCard.getChildren().add(subjLbl);
                });
            });
        }

        VBox annCard=UIUtils.card(UIUtils.sectionLabel("📢  Announcements"));
        if(faculty!=null){
            List<Announcement> anns=AnnouncementService.getAnnouncementsForFaculty(faculty.getId());
            anns.stream().limit(4).forEach(a -> {
                Label lbl=new Label("• ["+a.getType()+"] "+a.getTitle()+" — "+a.getPostedByName());
                lbl.setFont(Font.font("System",13)); annCard.getChildren().add(lbl); });
            if(anns.isEmpty()) annCard.getChildren().add(new Label("No announcements."));
        }

        panel.getChildren().addAll(title,sub,stats,summCard,annCard);
        return panel;
    }

    // ── SUBJECTS — click subject → see section info + students with remarks ──
    private VBox buildSubjectsPanel() {
        VBox panel=new VBox(16); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";");
        panel.getChildren().add(UIUtils.pageTitle("My Subjects"));

        List<FacultyAssignment> myAssignments=getMyAssignments();
        if(myAssignments.isEmpty()){
            Label none=new Label("No subjects assigned yet. Once admin assigns subjects to your sections, they will appear here.");
            none.setWrapText(true); none.setTextFill(Color.web(UIUtils.TEXT_MUTED));
            panel.getChildren().add(none); return panel;
        }

        Label hint=new Label("💡 Click a subject to see the section details and the list of students with their grades and remarks.");
        hint.setFont(Font.font("System",FontPosture.ITALIC,12)); hint.setTextFill(Color.web(UIUtils.TEXT_MUTED));

        TableView<FacultyAssignment> subjTable=UIUtils.styledTable();
        addCol(subjTable,"Code","subjectCode"); addCol(subjTable,"Subject","subjectName");
        addCol(subjTable,"Section","sectionName"); addCol(subjTable,"Schedule","schedule"); addCol(subjTable,"Room","room");
        subjTable.setPrefHeight(200);
        subjTable.setItems(FXCollections.observableArrayList(myAssignments));

        Label sectionInfo=new Label("Select a subject above to see details.");
        sectionInfo.setFont(Font.font("System",13)); sectionInfo.setTextFill(Color.web(UIUtils.TEXT_MUTED));

        TableView<Grade> studRemarksTable=UIUtils.styledTable();
        addCol(studRemarksTable,"Student ID","studentIdNo"); addCol(studRemarksTable,"Name","studentName");
        addCol(studRemarksTable,"Prelim","prelim"); addCol(studRemarksTable,"Midterm","midterm");
        addCol(studRemarksTable,"Pre-Final","prefinal"); addCol(studRemarksTable,"Final","finalGrade");
        addCol(studRemarksTable,"Computed","computedGrade"); addCol(studRemarksTable,"Grade","remarks");
        addCol(studRemarksTable,"Notes","performanceNotes");
        studRemarksTable.setPrefHeight(300);

        // Color code grade remarks
        colorCodeRemarksCol(studRemarksTable, 7);

        subjTable.getSelectionModel().selectedItemProperty().addListener((obs,old,sel) -> {
            if(sel==null) return;
            sectionInfo.setText("Section: "+sel.getSectionName()+"   |   "+sel.getSubjectCode()+" — "+sel.getSubjectName()+
                "   |   "+(sel.getSchedule()!=null?sel.getSchedule():"No schedule set")+"   |   Room: "+(sel.getRoom()!=null?sel.getRoom():"—"));
            sectionInfo.setFont(Font.font("System",FontWeight.BOLD,13));
            sectionInfo.setTextFill(Color.web(COLOR));
            studRemarksTable.setItems(FXCollections.observableArrayList(GradeService.getGradesByAssignment(sel.getId())));
        });

        panel.getChildren().addAll(hint,subjTable,sectionInfo,studRemarksTable);
        return panel;
    }

    // ── STUDENTS — per section → click section → see students ──
    private VBox buildStudentsPanel() {
        VBox panel=new VBox(16); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";");
        panel.getChildren().add(UIUtils.pageTitle("My Students"));

        List<Section> mySections=getMySections();
        if(mySections.isEmpty()){
            Label none=new Label("No students yet. Admin needs to assign subjects to sections and enroll students first.");
            none.setWrapText(true); none.setTextFill(Color.web(UIUtils.TEXT_MUTED));
            panel.getChildren().add(none); return panel;
        }

        Label hint=new Label("💡 Select a section to see all students enrolled in it.");
        hint.setFont(Font.font("System",FontPosture.ITALIC,12)); hint.setTextFill(Color.web(UIUtils.TEXT_MUTED));

        // Section picker as tab-like buttons
        HBox sectionBtns=new HBox(8); sectionBtns.setAlignment(Pos.CENTER_LEFT);
        Label secLabel=UIUtils.sectionLabel("My Sections:");

        // Student table
        TableView<Enrollment> studTable=UIUtils.styledTable();
        addCol(studTable,"Student ID","studentIdNo"); addCol(studTable,"Full Name","studentName");
        addCol(studTable,"Subject","subjectCode"); addCol(studTable,"Schedule","schedule"); addCol(studTable,"Status","status");
        studTable.setPrefHeight(400);

        Label studHeader=UIUtils.sectionLabel("Select a section above");

        // Build section buttons
        mySections.forEach(sec -> {
            Button btn=UIUtils.primaryButton(sec.getSectionName(),COLOR);
            btn.setOnAction(e -> {
                studHeader.setText("Students in: "+sec.getSectionName()+" — "+sec.getCourse()+" ("+sec.getYearLevel()+")");
                // Get all enrollments for this section under my assignments
                List<Enrollment> sectionStudents=new ArrayList<>();
                getMyAssignments().stream()
                    .filter(a -> a.getSectionId()==sec.getId())
                    .forEach(a -> sectionStudents.addAll(EnrollmentService.getEnrollmentsByAssignment(a.getId())));
                // Deduplicate by student for the display
                studTable.setItems(FXCollections.observableArrayList(sectionStudents));
            });
            sectionBtns.getChildren().add(btn);
        });

        // Double-click to see full student grade details
        studTable.setRowFactory(tv -> {
            TableRow<Enrollment> row=new TableRow<>();
            row.setOnMouseClicked(e -> {if(e.getClickCount()==2&&!row.isEmpty()) showStudentGradePreview(row.getItem());});
            return row;
        });
        Label dblHint=new Label("💡 Double-click a student to preview their grade.");
        dblHint.setFont(Font.font("System",FontPosture.ITALIC,12)); dblHint.setTextFill(Color.web(UIUtils.TEXT_MUTED));

        panel.getChildren().addAll(hint,secLabel,sectionBtns,studHeader,dblHint,studTable);
        return panel;
    }

    private void showStudentGradePreview(Enrollment en) {
        Stage w=new Stage(); w.initModality(Modality.APPLICATION_MODAL); w.setTitle("Grade Preview — "+en.getStudentName());
        VBox content=new VBox(14); content.setPadding(new Insets(24)); content.setPrefWidth(460);
        Label title=new Label("📊 "+en.getStudentName()); title.setFont(Font.font("System",FontWeight.BOLD,18));
        Label note=new Label("Showing: "+en.getSubjectCode()+" — "+en.getSubjectName()+"   |   Section: "+en.getSectionName());
        note.setTextFill(Color.web(UIUtils.TEXT_MUTED));
        Grade grade=GradeService.getGradeByEnrollment(en.getId());
        VBox card;
        if(grade==null){ card=UIUtils.card(new Label("No grade record found.")); }
        else {
            card=UIUtils.card(UIUtils.sectionLabel("Grade Details"),
                row("Prelim:",   fmt(grade.getPrelim())),
                row("Midterm:",  fmt(grade.getMidterm())),
                row("Pre-Final:",fmt(grade.getPrefinal())),
                row("Final:",    fmt(grade.getFinalGrade())),
                new Separator(),
                row("Computed:", fmt(grade.getComputedGrade())),
                row("Grade:",    grade.getRemarks()!=null?grade.getRemarks():"IN PROGRESS"),
                row("Notes:",    grade.getPerformanceNotes()!=null?grade.getPerformanceNotes():"—"));
        }
        content.getChildren().addAll(title,note,card);
        w.setScene(new Scene(content)); w.show();
    }

    // ── CLASS RECORDS ─────────────────────────────────────────
    private VBox buildClassRecordsPanel() {
        VBox panel=new VBox(16); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";");
        panel.getChildren().add(UIUtils.pageTitle("Class Records — Grades"));

        List<FacultyAssignment> myAssignments=getMyAssignments();
        ComboBox<FacultyAssignment> subjectFilter=new ComboBox<>(FXCollections.observableArrayList(myAssignments));
        subjectFilter.setPromptText("Select subject/section..."); subjectFilter.setMinWidth(340);
        Button btnLoad=UIUtils.primaryButton("Load",COLOR);
        HBox toolbar=new HBox(10,new Label("Subject:"),subjectFilter,btnLoad); toolbar.setAlignment(Pos.CENTER_LEFT);

        TableView<Grade> table=UIUtils.styledTable();
        addCol(table,"Student ID","studentIdNo"); addCol(table,"Name","studentName");
        addCol(table,"Prelim","prelim"); addCol(table,"Midterm","midterm");
        addCol(table,"Pre-Final","prefinal"); addCol(table,"Final","finalGrade");
        addCol(table,"Computed","computedGrade"); addCol(table,"Grade","remarks"); addCol(table,"Notes","performanceNotes");
        table.setPrefHeight(380);
        colorCodeRemarksCol(table,7);

        Button btnEdit=UIUtils.primaryButton("Edit Selected Grade",COLOR);
        btnLoad.setOnAction(e -> { FacultyAssignment sel=subjectFilter.getValue();
            if(sel!=null) table.setItems(FXCollections.observableArrayList(GradeService.getGradesByAssignment(sel.getId()))); });
        btnEdit.setOnAction(e -> { Grade sel=table.getSelectionModel().getSelectedItem();
            if(sel!=null) showEditGradeDialog(sel,table,subjectFilter.getValue());
            else UIUtils.showAlert(Alert.AlertType.WARNING,"No Selection","Select a student to edit grades."); });

        panel.getChildren().addAll(toolbar,btnEdit,table);
        return panel;
    }

    private void showEditGradeDialog(Grade g,TableView<Grade> table,FacultyAssignment assignment){
        Stage d=new Stage(); d.initModality(Modality.APPLICATION_MODAL); d.setTitle("Edit Grade — "+g.getStudentName());
        VBox form=new VBox(12); form.setPadding(new Insets(24)); form.setPrefWidth(440);
        Label titleLbl=new Label("Grade Entry: "+g.getStudentName()); titleLbl.setFont(Font.font("System",FontWeight.BOLD,16));
        Label subjLbl=new Label("Subject: "+g.getSubjectCode()+" — "+g.getSubjectName()); subjLbl.setTextFill(Color.web(UIUtils.TEXT_MUTED));
        TextField fP=UIUtils.styledField("0.00"); if(g.getPrelim()!=null) fP.setText(String.valueOf(g.getPrelim()));
        TextField fM=UIUtils.styledField("0.00"); if(g.getMidterm()!=null) fM.setText(String.valueOf(g.getMidterm()));
        TextField fPf=UIUtils.styledField("0.00"); if(g.getPrefinal()!=null) fPf.setText(String.valueOf(g.getPrefinal()));
        TextField fF=UIUtils.styledField("0.00"); if(g.getFinalGrade()!=null) fF.setText(String.valueOf(g.getFinalGrade()));
        TextArea fNotes=UIUtils.styledTextArea("Performance notes..."); fNotes.setPrefRowCount(3);
        if(g.getPerformanceNotes()!=null) fNotes.setText(g.getPerformanceNotes());
        Label lblComp=new Label("Computed: —"); lblComp.setFont(Font.font("System",FontWeight.BOLD,14));
        Runnable compute=()->{
            double sum=0; int cnt=0;
            try{sum+=Double.parseDouble(fP.getText()); cnt++;}catch(Exception ignored){}
            try{sum+=Double.parseDouble(fM.getText()); cnt++;}catch(Exception ignored){}
            try{sum+=Double.parseDouble(fPf.getText()); cnt++;}catch(Exception ignored){}
            try{sum+=Double.parseDouble(fF.getText()); cnt++;}catch(Exception ignored){}
            if(cnt>0){ double avg=Math.round((sum/cnt)*100.0)/100.0;
                lblComp.setText("Computed: "+avg+" — "+(avg>=75?"✅ PASSED":"❌ FAILED"));
                lblComp.setTextFill(avg>=75?Color.web(UIUtils.SUCCESS_GREEN):Color.web(UIUtils.DANGER_COLOR)); }
        };
        fP.textProperty().addListener((o,v,n)->compute.run()); fM.textProperty().addListener((o,v,n)->compute.run());
        fPf.textProperty().addListener((o,v,n)->compute.run()); fF.textProperty().addListener((o,v,n)->compute.run());
        compute.run();
        Button btnSave=UIUtils.primaryButton("Save",COLOR); Button btnCancel=UIUtils.outlineButton("Cancel");
        form.getChildren().addAll(titleLbl,subjLbl,
            UIUtils.formRow("Prelim:",fP), UIUtils.formRow("Midterm:",fM),
            UIUtils.formRow("Pre-Final:",fPf), UIUtils.formRow("Final:",fF),
            lblComp, new Label("Performance Notes:"), fNotes, new HBox(10,btnSave,btnCancel));
        btnSave.setOnAction(e -> {
            GradeService.updateGrade(g.getEnrollmentId(),parseDouble(fP.getText()),parseDouble(fM.getText()),
                parseDouble(fPf.getText()),parseDouble(fF.getText()),fNotes.getText().isEmpty()?null:fNotes.getText());
            ActivityLogService.log(currentUser.getId(),"UPDATE_GRADE",g.getStudentName()+" — "+g.getSubjectCode());
            if(assignment!=null) table.setItems(FXCollections.observableArrayList(GradeService.getGradesByAssignment(assignment.getId())));
            d.close(); });
        btnCancel.setOnAction(e -> d.close()); d.setScene(new Scene(form)); d.show();
    }

    // ── ATTENDANCE ────────────────────────────────────────────
    private VBox buildAttendancePanel() {
        VBox panel=new VBox(16); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";");
        panel.getChildren().add(UIUtils.pageTitle("Attendance"));

        List<FacultyAssignment> myAssignments=getMyAssignments();
        ComboBox<FacultyAssignment> subjectFilter=new ComboBox<>(FXCollections.observableArrayList(myAssignments));
        subjectFilter.setPromptText("Select subject/section..."); subjectFilter.setMinWidth(300);
        DatePicker datePicker=new DatePicker(LocalDate.now());
        Button btnLoad=UIUtils.primaryButton("Load Students",COLOR);
        HBox toolbar=new HBox(10,new Label("Subject:"),subjectFilter,new Label("Date:"),datePicker,btnLoad);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TableView<AttendanceRow> table=UIUtils.styledTable();
        addCol(table,"Student ID","studentIdNo"); addCol(table,"Name","studentName");
        TableColumn<AttendanceRow,String> statusCol=new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(col -> new TableCell<>(){
            final ComboBox<String> combo=new ComboBox<>(FXCollections.observableArrayList("PRESENT","ABSENT","LATE","EXCUSED"));
            @Override protected void updateItem(String item,boolean empty){
                super.updateItem(item,empty); if(empty){setGraphic(null);return;}
                combo.setValue(item!=null?item:"PRESENT");
                combo.setOnAction(e -> getTableView().getItems().get(getIndex()).setStatus(combo.getValue()));
                setGraphic(combo); }
        });
        TableColumn<AttendanceRow,String> remarkCol=new TableColumn<>("Remarks");
        remarkCol.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        remarkCol.setCellFactory(col -> new TableCell<>(){
            final TextField tf=UIUtils.styledField("optional");
            @Override protected void updateItem(String item,boolean empty){
                super.updateItem(item,empty); if(empty){setGraphic(null);return;}
                tf.setText(item!=null?item:"");
                tf.textProperty().addListener((o,v,n)->getTableView().getItems().get(getIndex()).setRemarks(n));
                setGraphic(tf); }
        });
        table.getColumns().addAll(statusCol,remarkCol); table.setPrefHeight(380);
        ObservableList<AttendanceRow> data=FXCollections.observableArrayList(); table.setItems(data);

        btnLoad.setOnAction(e -> { FacultyAssignment sel=subjectFilter.getValue();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Subject","Select a subject.");return;}
            data.clear();
            EnrollmentService.getEnrollmentsByAssignment(sel.getId()).forEach(en -> {
                AttendanceRow row=new AttendanceRow(); row.setEnrollmentId(en.getId());
                row.setStudentIdNo(en.getStudentIdNo()); row.setStudentName(en.getStudentName()); row.setStatus("PRESENT");
                data.add(row); });
        });
        Button btnSave=UIUtils.primaryButton("Save Attendance",COLOR);
        btnSave.setOnAction(e -> { LocalDate date=datePicker.getValue();
            if(date==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Date","Pick a date.");return;}
            int saved=0; for(AttendanceRow r:data) if(AttendanceService.markAttendance(r.getEnrollmentId(),date,r.getStatus(),r.getRemarks())) saved++;
            ActivityLogService.log(currentUser.getId(),"SAVE_ATTENDANCE","Date: "+date+" — "+saved+" records");
            UIUtils.showAlert(Alert.AlertType.INFORMATION,"Saved",saved+" attendance records saved."); });

        panel.getChildren().addAll(toolbar,btnSave,table);
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
        addCol(table,"Type","type"); addCol(table,"Title","title"); addCol(table,"Subject","subjectName"); addCol(table,"Section","sectionName");
        table.setPrefHeight(360);
        if(faculty!=null) table.setItems(FXCollections.observableArrayList(AnnouncementService.getAnnouncementsForFaculty(faculty.getId())));
        table.getSelectionModel().selectedItemProperty().addListener((obs,old,sel)->{if(sel!=null) showAnnouncementDetail(sel);});
        btnPost.setOnAction(e -> showPostAnnouncementDialog(table,null));
        btnEdit.setOnAction(e -> { Announcement sel=table.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Selection","Select announcement.");return;}
            if(sel.getPostedBy()!=currentUser.getId()){UIUtils.showAlert(Alert.AlertType.ERROR,"Not Allowed","Only the poster can edit.");return;}
            showPostAnnouncementDialog(table,sel); });
        btnDelete.setOnAction(e -> { Announcement sel=table.getSelectionModel().getSelectedItem();
            if(sel==null){UIUtils.showAlert(Alert.AlertType.WARNING,"No Selection","Select announcement.");return;}
            if(sel.getPostedBy()!=currentUser.getId()){UIUtils.showAlert(Alert.AlertType.ERROR,"Not Allowed","Only poster can delete.");return;}
            if(UIUtils.showConfirm("Delete","Delete this announcement?")){
                AnnouncementService.deleteAnnouncement(sel.getId());
                if(faculty!=null) table.setItems(FXCollections.observableArrayList(AnnouncementService.getAnnouncementsForFaculty(faculty.getId()))); } });
        panel.getChildren().addAll(toolbar,table); return panel;
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
                iv.setFitWidth(500); iv.setPreserveRatio(true); content.getChildren().add(iv);}catch(Exception ignored){} }
        Label body=new Label(a.getContent()); body.setWrapText(true); body.setFont(Font.font("System",a.getFontSize()>0?a.getFontSize():14));
        try{body.setTextFill(Color.web(a.getTextColor()!=null?a.getTextColor():"#2C3E50"));}catch(Exception ignored){}
        content.getChildren().addAll(titleLbl,meta,new Separator(),body);
        w.setScene(new Scene(new ScrollPane(content){{setFitToWidth(true);}})); w.show();
    }

    private void showPostAnnouncementDialog(TableView<Announcement> table,Announcement existing){
        boolean isEdit=existing!=null;
        Stage d=new Stage(); d.initModality(Modality.APPLICATION_MODAL); d.setTitle(isEdit?"Edit":"Post Announcement");
        VBox form=new VBox(14); form.setPadding(new Insets(24)); form.setPrefWidth(520);
        TextField fTitle=UIUtils.styledField("Title"); if(isEdit) fTitle.setText(existing.getTitle());
        TextArea fContent=UIUtils.styledTextArea("Content..."); fContent.setPrefRowCount(6); if(isEdit) fContent.setText(existing.getContent());
        ComboBox<Integer> fFont=new ComboBox<>(); fFont.getItems().addAll(10,12,13,14,16,18,20,22,24,28,32); fFont.setValue(isEdit?existing.getFontSize():14);
        ComboBox<String> fColor=new ComboBox<>(); fColor.getItems().addAll("#2C3E50","#1A73E8","#E74C3C","#1E8449","#E67E22","#8E44AD"); fColor.setValue(isEdit&&existing.getTextColor()!=null?existing.getTextColor():"#2C3E50");
        ComboBox<String> fType=UIUtils.styledCombo("GENERAL (all students)","SUBJECT (specific class)");
        ComboBox<FacultyAssignment> fSubj=new ComboBox<>(FXCollections.observableArrayList(getMyAssignments()));
        fSubj.setPromptText("Select subject/section..."); fSubj.setDisable(true);
        fType.setOnAction(e -> fSubj.setDisable(fType.getValue()==null||fType.getValue().startsWith("GENERAL")));
        final String[] imgHolder={isEdit?existing.getImageBase64():null};
        Label imgName=new Label(isEdit&&existing.getImageBase64()!=null?"Image attached":"No image"); imgName.setTextFill(Color.web(UIUtils.TEXT_MUTED));
        Button btnImg=UIUtils.outlineButton("Choose Image...");
        btnImg.setOnAction(ev -> { FileChooser fc=new FileChooser(); fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images","*.png","*.jpg","*.jpeg"));
            File file=fc.showOpenDialog(d); if(file!=null){try{imgHolder[0]=Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath())); imgName.setText(file.getName());}catch(Exception ex){}} });
        Button btnSave=UIUtils.primaryButton(isEdit?"Save":"Post",COLOR); Button btnCancel=UIUtils.outlineButton("Cancel");
        form.getChildren().addAll(UIUtils.formRow("Title:",fTitle), UIUtils.formRow("Content:",fContent),
            UIUtils.formRow("Font Size:",fFont), UIUtils.formRow("Text Color:",fColor),
            UIUtils.formRow("Audience:",fType), UIUtils.formRow("Subject:",fSubj),
            new HBox(10,btnImg,imgName), new HBox(10,btnSave,btnCancel));
        btnSave.setOnAction(ev -> {
            if(fTitle.getText().isEmpty()){UIUtils.showAlert(Alert.AlertType.WARNING,"Required","Title required.");return;}
            String type=fType.getValue()!=null&&fType.getValue().startsWith("SUBJECT")?"SUBJECT":"GENERAL";
            Integer assignId=type.equals("SUBJECT")&&fSubj.getValue()!=null?fSubj.getValue().getId():null;
            if(isEdit) AnnouncementService.updateAnnouncement(existing.getId(),fTitle.getText(),fContent.getText(),fFont.getValue(),fColor.getValue(),imgHolder[0]);
            else{ AnnouncementService.createAnnouncement(fTitle.getText(),fContent.getText(),fFont.getValue(),fColor.getValue(),imgHolder[0],currentUser.getId(),type,assignId);
                ActivityLogService.log(currentUser.getId(),"POST_ANNOUNCEMENT",fTitle.getText()); }
            if(faculty!=null) table.setItems(FXCollections.observableArrayList(AnnouncementService.getAnnouncementsForFaculty(faculty.getId())));
            d.close(); });
        btnCancel.setOnAction(ev -> d.close());
        d.setScene(new Scene(new ScrollPane(form){{setFitToWidth(true);}})); d.show();
    }

    // ── PROFILE ───────────────────────────────────────────────
    private VBox buildProfilePanel(){
        VBox panel=new VBox(16); panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color:"+UIUtils.BG_COLOR+";");
        panel.getChildren().add(UIUtils.pageTitle("My Profile"));
        TextField fName=UIUtils.styledField("Full Name"); fName.setText(currentUser.getFullName());
        TextField fEmail=UIUtils.styledField("Email"); fEmail.setText(currentUser.getEmail()!=null?currentUser.getEmail():"");
        TextField fContact=UIUtils.styledField("Contact"); fContact.setText(currentUser.getContact()!=null?currentUser.getContact():"");
        TextField fDept=UIUtils.styledField("Department"); if(faculty!=null&&faculty.getDepartment()!=null) fDept.setText(faculty.getDepartment());
        TextField fSpec=UIUtils.styledField("Specialization"); if(faculty!=null&&faculty.getSpecialization()!=null) fSpec.setText(faculty.getSpecialization());
        Label uNote=new Label("Username: "+currentUser.getUsername()+"  (cannot be changed)"); uNote.setTextFill(Color.web(UIUtils.TEXT_MUTED));
        Button btnSave=UIUtils.primaryButton("Save Changes",COLOR);
        btnSave.setOnAction(e -> { UserService.updateUser(currentUser.getId(),fName.getText(),fEmail.getText(),fContact.getText());
            if(faculty!=null) FacultyService.updateFaculty(faculty.getId(),fDept.getText(),fSpec.getText());
            currentUser.setFullName(fName.getText()); UIUtils.showAlert(Alert.AlertType.INFORMATION,"Saved","Profile updated."); });
        VBox card=UIUtils.card(uNote,UIUtils.formRow("Full Name:",fName),UIUtils.formRow("Email:",fEmail),
            UIUtils.formRow("Contact:",fContact),UIUtils.formRow("Department:",fDept),UIUtils.formRow("Specialization:",fSpec),btnSave);
        card.setMaxWidth(520); panel.getChildren().add(card); return panel;
    }

    // ── Helpers ───────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private void colorCodeRemarksCol(TableView<Grade> table, int colIndex) {
        TableColumn<Grade,String> col=(TableColumn<Grade,String>)table.getColumns().get(colIndex);
        col.setCellFactory(c -> new TableCell<>(){
            @Override protected void updateItem(String item,boolean empty){
                super.updateItem(item,empty); if(empty||item==null){setText(null);setStyle("");return;}
                setText(item);
                if("PASSED".equals(item)) setStyle("-fx-text-fill:"+UIUtils.SUCCESS_GREEN+";-fx-font-weight:bold;");
                else if("FAILED".equals(item)) setStyle("-fx-text-fill:"+UIUtils.DANGER_COLOR+";-fx-font-weight:bold;");
                else setStyle("-fx-text-fill:"+UIUtils.TEXT_MUTED+";"); }
        });
    }

    private <T> void addCol(TableView<T> t,String h,String p){
        TableColumn<T,String> c=new TableColumn<>(h); c.setCellValueFactory(new PropertyValueFactory<>(p)); t.getColumns().add(c); }
    private HBox row(String label,String value){
        Label lbl=new Label(label); lbl.setMinWidth(140); lbl.setFont(Font.font("System",FontWeight.BOLD,13)); lbl.setTextFill(Color.web(UIUtils.TEXT_MUTED));
        Label val=new Label(value!=null?value:"—"); val.setFont(Font.font("System",13));
        HBox r=new HBox(10,lbl,val); r.setAlignment(Pos.CENTER_LEFT); return r; }
    private static Double parseDouble(String s){try{return Double.parseDouble(s.trim());}catch(Exception e){return null;}}
    private static String fmt(Double d){return d!=null?String.valueOf(d):"—";}

    public static class AttendanceRow {
        private int enrollmentId; private String studentIdNo,studentName,status="PRESENT",remarks="";
        public int getEnrollmentId(){return enrollmentId;} public void setEnrollmentId(int e){enrollmentId=e;}
        public String getStudentIdNo(){return studentIdNo;} public void setStudentIdNo(String s){studentIdNo=s;}
        public String getStudentName(){return studentName;} public void setStudentName(String s){studentName=s;}
        public String getStatus(){return status;} public void setStatus(String s){status=s;}
        public String getRemarks(){return remarks;} public void setRemarks(String r){remarks=r;}
    }
}
