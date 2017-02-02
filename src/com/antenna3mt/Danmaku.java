package com.antenna3mt;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.util.Random;


public class Danmaku {
    public Font font;
    public Color fillColor;
    public Color strokeColor;
    public Integer strokeWidth;
    public String text;
    public Integer duration;
    public Transition transition;

    public double x;
    public double y;
    public Boolean finished;
    private double width;
    private double height;
    public static double windowWidth;
    public static double windowHeight;


    public static Font defaultFont = getDefaultFont(40);
    public static Color defaultFillColor = Color.WHITE;
    public static Color defaultStrokeColor = Color.BLACK;
    public static Integer defaultStrokeWidth = 3;
    public static String defaultText = "Yi Jin 金燚";
    public static Integer defaultDuration = 16;

    public static Font getDefaultFont(double size){
        return Font.font("Microsoft Yahei", FontWeight.EXTRA_BOLD, size);
    }


    public static double getTextWidth(String text, Font font, Integer strokeWidth) {
        Text node = new Text(text);
        node.setFont(font);
        node.setStrokeWidth(strokeWidth);
        return node.getLayoutBounds().getWidth();
    }

    public static double getTextHeight(String text, Font font, Integer strokeWidth) {
        Text node = new Text(text);
        node.setFont(font);
        node.setStrokeWidth(strokeWidth);
        return node.getLayoutBounds().getHeight();
    }


    public Danmaku(String text, Font font, Color fillColor, Color strokeColor, Integer strokeWidth, Integer duration) {
        this.text = text;
        this.font = font;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.duration = duration;
        this.finished = false;

        this.width = getTextWidth(text, font, strokeWidth);
        this.height = getTextHeight(text, font, strokeWidth);
        this.x = this.windowWidth;
        this.y = (new Random().nextDouble()) * (this.windowHeight - this.height) + this.height / 2;

        Danmaku self = this;

        this.transition = new Transition() {
            {
                setCycleCount(1);
                setCycleDuration(Duration.seconds(self.defaultDuration));
                setAutoReverse(false);
                setInterpolator(Interpolator.LINEAR);
                setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        self.finished = true;
                    }
                });
            }

            @Override
            protected void interpolate(double frac) {
                self.x = self.windowWidth - frac * (self.windowWidth + self.width);
            }
        };
    }

    public Danmaku(String text) {
        this(text, defaultFont, defaultFillColor, defaultStrokeColor, defaultStrokeWidth, defaultDuration);
    }

    public Danmaku() {
        this(defaultText);
    }

    public void play() {
        this.transition.play();
    }
}