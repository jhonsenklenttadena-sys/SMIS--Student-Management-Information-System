package com.smis.views.common;

import com.smis.models.User;
import com.smis.services.AuthService;
import com.smis.utils.SessionManager;
import com.smis.utils.UIUtils;
import com.smis.views.admin.AdminDashboard;
import com.smis.views.faculty.FacultyDashboard;
import com.smis.views.student.StudentDashboard;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class LoginView {

    private Stage stage;

    public LoginView(Stage stage) { this.stage = stage; }

    public Scene getScene() {
        HBox root = new HBox();
        root.setPrefSize(900, 600);

        // Left branding panel
        VBox left = new VBox(20);
        left.setPrefWidth(400);
        left.setAlignment(Pos.CENTER);
        left.setPadding(new Insets(60));
        left.setStyle("-fx-background-color:" + UIUtils.SIDEBAR_DARK + ";");

        Label logo = new Label("SMIS");
        logo.setFont(Font.font("System", FontWeight.BOLD, 52));
        logo.setTextFill(Color.WHITE);

        Label sub = new Label("Student Management\nInformation System");
        sub.setFont(Font.font("System", 16));
        sub.setTextFill(Color.web("rgba(255,255,255,0.65)"));
        sub.setTextAlignment(TextAlignment.CENTER);

        Label tagline = new Label("Centralized · Digitized · Efficient");
        tagline.setFont(Font.font("System", FontPosture.ITALIC, 13));
        tagline.setTextFill(Color.web("rgba(255,255,255,0.35)"));

        left.getChildren().addAll(logo, sub, tagline);

        // Right login panel
        VBox right = new VBox(20);
        right.setAlignment(Pos.CENTER);
        right.setPadding(new Insets(60));
        right.setStyle("-fx-background-color:" + UIUtils.BG_COLOR + ";");

        Label welcome = new Label("Welcome Back");
        welcome.setFont(Font.font("System", FontWeight.BOLD, 28));
        welcome.setTextFill(Color.web(UIUtils.TEXT_DARK));

        Label subLabel = new Label("Sign in to your account");
        subLabel.setTextFill(Color.web(UIUtils.TEXT_MUTED));

        VBox card = new VBox(14);
        card.setPadding(new Insets(30));
        card.setMaxWidth(360);
        card.setStyle("-fx-background-color:white;-fx-background-radius:12;" +
                      "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),12,0,0,3);");

        Label uLbl = new Label("Username");
        uLbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        TextField usernameField = UIUtils.styledField("Enter username");

        Label pLbl = new Label("Password");
        pLbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        PasswordField passwordField = UIUtils.styledPasswordField("Enter password");

        Label errorLbl = new Label();
        errorLbl.setTextFill(Color.web(UIUtils.DANGER_RED));
        errorLbl.setFont(Font.font("System", 12));
        errorLbl.setVisible(false);
        errorLbl.setManaged(false);

        Button loginBtn = UIUtils.primaryButton("Sign In", UIUtils.ADMIN_PRIMARY);
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        Label hint = new Label("Default: admin / admin123");
        hint.setFont(Font.font("System", FontPosture.ITALIC, 11));
        hint.setTextFill(Color.web(UIUtils.TEXT_MUTED));

        card.getChildren().addAll(uLbl, usernameField, pLbl, passwordField, errorLbl, loginBtn, hint);
        right.getChildren().addAll(welcome, subLabel, card);
        HBox.setHgrow(right, Priority.ALWAYS);

        Runnable doLogin = () -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                errorLbl.setText("Please enter username and password.");
                errorLbl.setVisible(true); errorLbl.setManaged(true); return;
            }
            loginBtn.setDisable(true); loginBtn.setText("Signing in...");
            User user = AuthService.login(username, password);
            if (user == null) {
                errorLbl.setText("Invalid username or password.");
                errorLbl.setVisible(true); errorLbl.setManaged(true);
                loginBtn.setDisable(false); loginBtn.setText("Sign In"); return;
            }
            SessionManager.setCurrentUser(user);
            switch (user.getRole()) {
                case ADMIN   -> new AdminDashboard(stage).show();
                case FACULTY -> new FacultyDashboard(stage).show();
                case STUDENT -> new StudentDashboard(stage).show();
            }
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passwordField.setOnAction(e -> doLogin.run());
        root.getChildren().addAll(left, right);
        return new Scene(root);
    }
}
