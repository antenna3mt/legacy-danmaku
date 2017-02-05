package com.antenna3mt;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.geometry.*;
import javafx.animation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.event.MenuEvent;


public class DanmakuManager extends Application {
    private Stage displayStage;
    private Stage controlPanelStage;
    private DanmakuController controller;
    private GraphicsContext gc;
    private Vector<Danmaku> danmakuList = new Vector<Danmaku>();

    private Boolean onFetch = false;
    private Boolean onRun = true;
    private Screen screen;


    private String connectURL = "https://danmaku-server.herokuapp.com";
    private Integer connectPort = 443;
    private Boolean connected = false;
    private Integer fetchInterval = 3000;
    private ObservableList<Screen> screens = null;


    public static void main(String[] args) {
        launch(args);
    }

    /*
     * Utility
     *
     */

    public double getWindowWidth() {
        Rectangle2D screenBounds = screen.getBounds();
        return screenBounds.getWidth();
    }

    public double getWindowHeight() {
        Rectangle2D screenBounds = screen.getBounds();
        return screenBounds.getHeight();
    }
    public double getWindowX() {
        Rectangle2D screenBounds = screen.getBounds();
        return screenBounds.getMinX();
    }

    public double getWindowY() {
        Rectangle2D screenBounds = screen.getBounds();
        return screenBounds.getMinY();
    }

    /*
     * Connections
     *
     */
    public void setConnection(String url, Integer port) {
        connectURL = url;
        connectPort = port;
    }

    public String getHttpResponse(String url, String method) throws Exception {
        URL u = new URL(url);
        HttpURLConnection con = (HttpURLConnection) u.openConnection();
        con.setRequestMethod(method);
        int code = con.getResponseCode();
        if (code == con.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            throw new Exception("No");
        }
    }

    public boolean checkConnection() {

        try {
            getHttpResponse(this.connectURL + ":" + this.connectPort.toString() + "/check", "GET");
            setConnected(true);

        } catch (Exception e) {
            System.out.print("Exception: " + e.toString());
            setConnected(false);
        }
        return connected;
    }

    private void fetchDanmaku() {
        if (connected && onFetch) {
            try {
                String data = getHttpResponse(this.connectURL + ":" + this.connectPort.toString() + "/fetch?car=918", "GET");
                JSONTokener jt = new JSONTokener(data);
                JSONArray arr = new JSONArray(jt);
                Iterator<Object> i = arr.iterator();
                while (i.hasNext()) {
                    JSONObject o = (JSONObject) i.next();
                    String content = o.getString("content");
                    String color_s = o.getString("color");
                    Danmaku d = new Danmaku(content, Color.web(color_s));
                    danmakuList.add(d);
                    d.play();
                }
            } catch (Exception e) {
                System.out.print("Exception: " + e.toString());
            }
        }


    }

    /*
     * Windows
     *
     */
    private void moveScreen(Screen screen) {
        this.screen = screen;
        Danmaku.windowWidth = getWindowWidth();
        Danmaku.windowHeight = getWindowHeight();
    }

    private void changeScreen(int code){
        for(Screen screen: screens){
            if(screen.hashCode() == code){
                moveScreen(screen);
                displayStage.hide();
                displayStage = new Stage();
                openDisplayWindow(displayStage);
            }
        }
    }

    private void setupScreen() {
        screens = Screen.getScreens();
        for (Screen screen : screens) {
            System.out.println(screen.hashCode());
        }
        moveScreen(Screen.getPrimary());
    }

    private void openDisplayWindow(Stage stage) {
        Canvas canvas = new Canvas(getWindowWidth(), getWindowHeight());
        gc = canvas.getGraphicsContext2D();
        Scene scene = new Scene(new Group(canvas), getWindowWidth(), getWindowHeight());
        scene.setFill(Color.TRANSPARENT);
        stage.setX(getWindowX());
        stage.setY(getWindowY());
        //stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Display");
        stage.setScene(scene);
        stage.show();
    }

    private void openControlPanelWindow(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("controlPanel.fxml"));
        TabPane root = (TabPane) loader.load();
        controller = loader.<DanmakuController>getController();
        stage.setTitle("Control Panel");
        stage.setScene(new Scene(root));
        stage.setAlwaysOnTop(true);
        stage.getIcons().add(new Image("com/antenna3mt/icon.png"));
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.exit(0);
            }
        });
        stage.show();
    }

    public void initControlPanel() {
        controller.durationText.setText(Danmaku.defaultDuration.toString());
        controller.fontSizeText.setText(new Double(Danmaku.defaultFont.getSize()).toString());
        controller.portText.setText(connectPort.toString());
        controller.serverText.setText(connectURL);
        setConnected(false);
        controller.runToggleButton.setSelected(onRun);

        for (final Screen screen : screens) {
            MenuItem mi = new MenuItem(Integer.toString(screen.hashCode()));
            mi.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeScreen(screen.hashCode());
                }
            });
            controller.monitorMenu.getItems().add(mi);
        }

    }


    /*
     * Controllers
     *
     */

    public void danmakuShow(String text) {
        Danmaku d = new Danmaku(text);
        danmakuList.add(d);
        d.play();
    }

    public void danmakuClear() {
        danmakuList.clear();
    }

    public void setDanmakuFontSize(Double size) {
        Danmaku.defaultFont = Danmaku.getDefaultFont(size);
    }

    public void setDanmakuDuration(int duration) {
        Danmaku.defaultDuration = duration;
    }

    public void setOnFetch(Boolean on) {
        this.onFetch = on;
    }

    public void setOnRun(Boolean on) {
        this.onRun = on;
    }

    public void setConnected(Boolean on) {
        this.connected = on;
        this.controller.fetchToggleButton.setDisable(!on);
    }

    public void test() {
        //fetchDanmaku();
        Danmaku d = new Danmaku();
        danmakuList.add(d);
        d.play();
    }

    private void updateStatus() {
        controller.clearStatus();
        // Queue length
        controller.addStatus("Queue Length: " + danmakuList.size());
        // Connected
        controller.addStatus("Connected: " + this.connected.toString());
        if (connected) {
            controller.addStatus("Server Address: " + this.connectURL);
            controller.addStatus("Server Port: " + this.connectPort.toString());
        }
        // Danmaku
        controller.addStatus("Danmaku Font Size: " + Danmaku.defaultFont.getSize());
        controller.addStatus("Danmaku Duration: " + Danmaku.defaultDuration);
    }

    /*
     * Timers
     *
     */

    private void setupAnimationTimer() {
        AnimationTimer atimer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                updateStatus();
                gc.clearRect(getWindowX(), getWindowY(), getWindowWidth(), getWindowHeight());

                if (onRun) {
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


            }
        };
        atimer.start();
    }

    private void setupFetchTimer(Integer timeInterval) {
        Timer time = new Timer();
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                fetchDanmaku();
            }
        }, 0, timeInterval);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize
        setupScreen();
        displayStage = new Stage();
        controlPanelStage = primaryStage;
        openDisplayWindow(displayStage);
        openControlPanelWindow(controlPanelStage);
        controller.setManager(this);
        initControlPanel();
        setupAnimationTimer();
        setupFetchTimer(fetchInterval);
    }
}
