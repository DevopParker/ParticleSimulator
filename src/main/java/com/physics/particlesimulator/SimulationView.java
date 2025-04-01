package com.physics.particlesimulator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimulationView {
    private final javafx.scene.canvas.Canvas canvas;
    private double lastMouseX;
    private double lastMouseY;
    private boolean dragging = false;

    public SimulationView(Stage stage, Camera2D camera) {

        canvas = new javafx.scene.canvas.Canvas(1920, 1080);

        VBox ui = new VBox(10);
        ui.setPadding(new Insets(10));
        ui.setAlignment(Pos.TOP_LEFT);

        StackPane stackPane = new StackPane(canvas);
        Scene scene = new Scene(stackPane);

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> camera.move(0, -20);
                case S -> camera.move(0, 20);
                case A -> PhysicsEngine.setParameters(7);
                case D -> camera.move(20, 0);
            }
        });

        scene.setOnScroll(e -> {
            double factor = (e.getDeltaY() > 0) ? 1.1 : 0.9;
            camera.zoomAroundScreenCenter(factor, canvas.getWidth(), canvas.getHeight());
        });

        scene.setOnMousePressed(e -> {
            dragging = true;
            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();
        });

        scene.setOnMouseReleased(e -> dragging = false);

        scene.setOnMouseDragged(e -> {
            if (dragging) {
                double dx = e.getSceneX() - lastMouseX;
                double dy = e.getSceneY() - lastMouseY;
                camera.move(-dx, -dy); // Invert to move camera with drag
                lastMouseX = e.getSceneX();
                lastMouseY = e.getSceneY();
            }
        });

        stage.setTitle("Particle Simulator");
        stage.setScene(scene);
        stage.show();
    }

    public javafx.scene.canvas.Canvas getCanvas() {
        return canvas;
    }
}