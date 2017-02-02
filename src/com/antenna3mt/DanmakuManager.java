package com.antenna3mt;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.scene.paint.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.animation.*;
import javafx.util.*;
import com.antenna3mt.Danmaku;

import static java.util.concurrent.TimeUnit.*;

import java.beans.*;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DanmakuManager extends Application {
    private Stage displayStage;
    private Stage controlPanelStage;
    private DanmakuController controller;
    private GraphicsContext gc;
    private Vector<Danmaku> danmakuList = new Vector<>();

    private Boolean onFetch;
    private Boolean onRun;
    private Screen screen;

    private Integer defaultPort = 20;


    public static void main(String[] args) {
        launch(args);
    }


    public double getWindowWidth() {
        Rectangle2D screenBounds = screen.getVisualBounds();
        return screenBounds.getWidth();
    }

    public double getWindowHeight() {
        Rectangle2D screenBounds = screen.getVisualBounds();
        return screenBounds.getHeight();
    }


    private void openDisplayWindow(Stage stage) {
        Canvas canvas = new Canvas(getWindowWidth(), getWindowHeight());
        gc = canvas.getGraphicsContext2D();
        Scene scene = new Scene(new Group(canvas), getWindowWidth(), getWindowHeight());
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setTitle("Display");
        stage.setScene(scene);
        stage.show();
    }

    private TabPane openControlPanelWindow(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("controlPanel.fxml"));
        TabPane root = (TabPane) loader.load();
        controller = loader.<DanmakuController>getController();
        stage.setTitle("Control Panel");
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image("com/antenna3mt/icon.png"));
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.exit(0);
            }
        });
        stage.show();
        return root;
    }

    public void danmakuShow(String text) {
        Danmaku d = new Danmaku(text);
        danmakuList.add(d);
        d.play();
    }

    public void danmakuClear(){
        danmakuList.clear();
    }

    public void setDanmakuFontSize(int size) {
        Danmaku.defaultFont = Danmaku.getDefaultFont(size);
    }

    public void setDanmakuDuration(int duration) {
        Danmaku.defaultDuration = duration;
    }

    public void test() {
        Danmaku d = new Danmaku();
        danmakuList.add(d);
        d.play();
    }

    private void updateStatus() {
        controller.clearStatus();
        // Queue length
        controller.addStatus("Queue Length: " + danmakuList.size());
    }


    private void setupScreen(Screen screen) {
        this.screen = Screen.getPrimary();
        Danmaku.windowWidth = getWindowWidth();
        Danmaku.windowHeight = getWindowHeight();
    }

    public void initControlPanel(){
        controller.durationText.setText(Danmaku.defaultDuration.toString());
        controller.fontSizeText.setText(new Double(Danmaku.defaultFont.getSize()).toString());
        controller.portText.setText(defaultPort.toString());
    }

    /*
    displayStage.hide();
    displayStage = new Stage();
    openDisplayWindow(displayStage);
    */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize
        setupScreen(Screen.getPrimary());
        displayStage = new Stage();
        controlPanelStage = primaryStage;
        openDisplayWindow(displayStage);
        openControlPanelWindow(controlPanelStage);
        controller.setManager(this);
        initControlPanel();

        // Setup animation
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateStatus();
                gc.clearRect(0, 0, getWindowWidth(), getWindowHeight());

                Iterator<Danmaku> i = danmakuList.iterator();
                while (i.hasNext()) {
                    Danmaku d = i.next();
                    if (d.finished) {
                        i.remove();
                    } else {
                        gc.setFill(d.fillColor);
                        gc.setFont(d.font);
                        gc.setStroke(d.strokeColor);
                        gc.setLineWidth(d.strokeWidth);
                        gc.strokeText(d.text, d.x, d.y);
                        gc.fillText(d.text, d.x, d.y);
                    }
                }

            }
        };

        // Start animation
        timer.start();
    }
}
