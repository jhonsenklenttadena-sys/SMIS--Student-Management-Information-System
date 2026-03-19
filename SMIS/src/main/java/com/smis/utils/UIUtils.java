package com.smis.utils;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Optional;

/**
 * Reusable UI building blocks used across all dashboards.
 */
public class UIUtils {

    // ── Brand Colors ──────────────────────────────────────────
    public static final String ADMIN_PRIMARY   = "#1A73E8";
    public static final String FACULTY_PRIMARY = "#1E8449";
    public static final String STUDENT_PRIMARY = "#E67E22";
    public static final String BG_COLOR        = "#F5F7FA";
    public static final String SIDEBAR_DARK    = "#1C1C2E";
    public static final String CARD_WHITE      = "#FFFFFF";
    public static final String TEXT_DARK       = "#2C3E50";
    public static final String TEXT_MUTED      = "#7F8C8D";
    public static final String DANGER_RED     = "#E74C3C";
    public static final String DANGER_COLOR   = "#E74C3C";
    public static final String SUCCESS_GREEN  = "#27AE60";
    public static final String FACULTY_COLOR  = "#1E8449";
    public static final String ADMIN_COLOR    = "#1A73E8";
    public static final String STUDENT_COLOR  = "#E67E22";

    // ── Styled Buttons ────────────────────────────────────────

    public static Button primaryButton(String text, String hexColor) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + hexColor + "; -fx-text-fill: white; " +
                     "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8 20; " +
                     "-fx-background-radius: 6; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + hexColor + ",-15%); " +
                "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
                "-fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + hexColor + "; " +
                "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
                "-fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;"));
        return btn;
    }

    public static Button dangerButton(String text) {
        return primaryButton(text, DANGER_RED);
    }

    public static Button outlineButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_DARK + "; " +
                     "-fx-font-size: 13px; -fx-padding: 8 20; -fx-background-radius: 6; " +
                     "-fx-border-color: #BDC3C7; -fx-border-radius: 6; -fx-cursor: hand;");
        return btn;
    }

    // ── Labels ────────────────────────────────────────────────

    public static Label pageTitle(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 22));
        lbl.setTextFill(Color.web(TEXT_DARK));
        return lbl;
    }

    public static Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 14));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        return lbl;
    }

    // ── Stat Card ─────────────────────────────────────────────

    public static VBox statCard(String title, String value, String color) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + CARD_WHITE + "; -fx-background-radius: 10; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        Label topBar = new Label();
        topBar.setPrefHeight(4);
        topBar.setPrefWidth(40);
        topBar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 2;");

        Label valLabel = new Label(value);
        valLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        valLabel.setTextFill(Color.web(color));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", 13));
        titleLabel.setTextFill(Color.web(TEXT_MUTED));

        card.getChildren().addAll(topBar, valLabel, titleLabel);
        return card;
    }

    // ── Sidebar Nav Button ────────────────────────────────────

    public static Button navButton(String text, String icon) {
        Button btn = new Button(icon + "  " + text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255,255,255,0.75); " +
                     "-fx-font-size: 13px; -fx-padding: 12 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.1); " +
                "-fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 12 20; " +
                "-fx-background-radius: 6; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: rgba(255,255,255,0.75); -fx-font-size: 13px; " +
                "-fx-padding: 12 20; -fx-background-radius: 6; -fx-cursor: hand;"));
        return btn;
    }

    public static Button navButtonActive(String text, String icon, String color) {
        Button btn = new Button(icon + "  " + text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                     "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 12 20; " +
                     "-fx-background-radius: 6; -fx-cursor: hand;");
        return btn;
    }

    // ── Table Styling ─────────────────────────────────────────

    public static <T> TableView<T> styledTable() {
        TableView<T> table = new TableView<>();
        table.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    // ── Dialogs ───────────────────────────────────────────────

    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // ── Card Container ────────────────────────────────────────

    public static VBox card(javafx.scene.Node... children) {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: " + CARD_WHITE + "; -fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 8, 0, 0, 2);");
        box.getChildren().addAll(children);
        return box;
    }

    // ── Text Field ────────────────────────────────────────────

    public static TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #DEE2E6; " +
                    "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12; -fx-font-size: 13px;");
        return tf;
    }

    public static PasswordField styledPasswordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #DEE2E6; " +
                    "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12; -fx-font-size: 13px;");
        return pf;
    }

    public static TextArea styledTextArea(String prompt) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setWrapText(true);
        ta.setPrefRowCount(4);
        ta.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #DEE2E6; " +
                    "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12; -fx-font-size: 13px;");
        return ta;
    }

    public static ComboBox<String> styledCombo(String... items) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(items);
        cb.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #DEE2E6; " +
                    "-fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 13px;");
        cb.setMaxWidth(Double.MAX_VALUE);
        return cb;
    }

    // ── Form Row ──────────────────────────────────────────────

    public static HBox formRow(String labelText, javafx.scene.Node field) {
        Label lbl = new Label(labelText);
        lbl.setMinWidth(130);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web(TEXT_DARK));
        HBox row = new HBox(12, lbl, field);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(field, Priority.ALWAYS);
        return row;
    }
}
