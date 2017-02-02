package com.antenna3mt;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.*;

public class DanmakuController {
    @FXML
    public TextField serverText;
    @FXML
    public TextField portText;
    @FXML
    public TextField danmakuText;
    @FXML
    public TextField fontSizeText;
    @FXML
    public TextField durationText;
    @FXML
    public VBox statusPane;

    DanmakuManager manager;


    private void alert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setManager(DanmakuManager m) {
        manager = m;
    }


    @FXML
    private void connect() {
        alert("Connection", "Successful connected to server");
    }


    @FXML
    private void setMonitor() {

    }

    @FXML
    private void toggleFetch() {

    }

    @FXML
    private void toggleRun() {

    }

    @FXML
    private void clearDisplay() {
        manager.danmakuClear();
    }

    @FXML
    private void test() {
        manager.test();
    }

    @FXML
    private void updateSettings() {
        int f = Integer.parseInt(fontSizeText.getText());
        int d = Integer.parseInt(durationText.getText());
        manager.setDanmakuFontSize(f);
        manager.setDanmakuDuration(d);
    }

    @FXML
    private void push() {
        String text = danmakuText.getText();
        danmakuText.setText("");
        if (text.length() > 0) {
            manager.danmakuShow(text);
        }

    }


    public void addStatus(String text) {
        Text t = new Text(text);
        statusPane.getChildren().add(t);
    }

    public void clearStatus(){
        statusPane.getChildren().clear();
    }
}
