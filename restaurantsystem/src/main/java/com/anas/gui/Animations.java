package com.anas.gui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class Animations {

    // NEW SCREEN TRANSITION (Slide Up + Bounce)
    public static void screenTransition(Node node) {
        // Start state: Slightly down and invisible
        node.setTranslateY(50); 
        node.setOpacity(0); 

        // Slide Up
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), node);
        tt.setToY(0);
        // "BACK_OUT" makes it go slightly past the target and settle back (Bouncy feel)
        tt.setInterpolator(Interpolator.EASE_OUT); 

        // Fade In (Simultaneous)
        FadeTransition ft = new FadeTransition(Duration.millis(400), node);
        ft.setToValue(1);

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.play();
    }

    // 2. SLIDE UP (For Lists)
    public static void fadeInUp(Node node) {
        node.setTranslateY(30);
        node.setOpacity(0);

        TranslateTransition tt = new TranslateTransition(Duration.millis(400), node);
        tt.setToY(0);
        
        FadeTransition ft = new FadeTransition(Duration.millis(400), node);
        ft.setToValue(1);

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.play();
    }

    // 3. POP IN (For Dialogs)
    public static void popIn(Node node) {
        node.setScaleX(0.9);
        node.setScaleY(0.9);
        node.setOpacity(0); // Start invisible to avoid flash

        ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
        st.setToX(1.0); st.setToY(1.0);
        st.setInterpolator(Interpolator.EASE_OUT);
        
        FadeTransition ft = new FadeTransition(Duration.millis(300), node);
        ft.setToValue(1.0);
        
        ParallelTransition pt = new ParallelTransition(st, ft);
        pt.play();
    }

    // 4. SHAKE (Error Feedback)
    public static void shake(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }

    // 4. HOVER EFFECT (Scale up slightly on mouse over)
    public static void addHoverEffect(Node node) {
        node.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
            st.setToX(1.03); st.setToY(1.03); // Grow 3%
            st.play();
        });
        
        node.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
            st.setToX(1.0); st.setToY(1.0); // Return to normal
            st.play();
        });
    }
    
    // BUTTON ANIMATIONS (Hover & Click)
    public static void animateButton(Button btn) {
        // HOVER: Grow slightly
        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.05); st.setToY(1.05);
            st.play();
        });

        // EXIT: Return to normal
        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.0); st.setToY(1.0);
            st.play();
        });

        // PRESS: Shrink slightly (Click feel)
        btn.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(50), btn);
            st.setToX(0.95); st.setToY(0.95);
            st.play();
        });

        // RELEASE: Bounce back
        btn.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(50), btn);
            st.setToX(1.05); st.setToY(1.05);
            st.play();
        });
    }
}