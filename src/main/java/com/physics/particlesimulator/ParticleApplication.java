package com.physics.particlesimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParticleApplication extends Application {
    // FPS Variables
    private long lastUpdateTime = 0;
    private int frameCount = 0;
    private long lastFpsUpdateTime = 0;
    private double currentFps = 0;

    @Override
    public void start(Stage stage) throws IOException {

        Camera2D camera = new Camera2D(0, 0, 1.0);
        SimulationView particleCanvas = new SimulationView(stage, camera);
        javafx.scene.canvas.Canvas canvas = particleCanvas.getCanvas();
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        int worldWidth = 10000;
        int worldHeight = 6000;
        double screenWidth = canvas.getWidth();
        double screenHeight = canvas.getHeight();
        double padding = 100;

//      Set min zoom (zoomed OUT — see entire world with padding)
        double zoomOutX = screenWidth / (worldWidth + 2 * padding);
        double zoomOutY = screenHeight / (worldHeight + 2 * padding);
        camera.minZoom = Math.min(zoomOutX, zoomOutY);

//      Set max zoom (zoomed IN — stop when world fits exactly)
        double zoomInX = screenWidth / worldWidth;
        double zoomInY = screenHeight / worldHeight;
        camera.maxZoom = Math.min(zoomInX, zoomInY);

//      Start fully zoomed in on world
        camera.zoom = camera.maxZoom;
        camera.centerOnWorld(worldWidth, worldHeight, screenWidth, screenHeight);


        int numTypes = 7;
        PhysicsEngine.setParameters(numTypes);

        int numParticles = 15000;
        // int maxDistance = 100;
        int gridSize = 250;
        int gridWidth = worldWidth / gridSize;
        int gridHeight = worldHeight / gridSize;

        ArrayList<Particle>[][] particles = new ArrayList[gridHeight][gridWidth];
        List<Particle> particleList = new ArrayList<>();

        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                particles[row][col] = new ArrayList<Particle>();
            }
        }

        for (int i = 0; i < numParticles; i++) {
            double x = Vector2D.random(0, worldWidth);
            double y = Vector2D.random(0, worldHeight);
            Vector2D pos = new Vector2D(x, y);

            double vx = Vector2D.random(-1, 1); // slight initial motion
            double vy = Vector2D.random(-1, 1);
            Vector2D vel = new Vector2D(vx, vy);

            int type = (int) Vector2D.random(numTypes);
            Color color = Color.hsb(360.0 * type / numTypes, 1.0, 1.0); // colorful by type

            Particle p = new Particle(pos, vel ,type, color);
            particleList.add(p);

            int row = (int)(y / gridSize);
            int col = (int)(x / gridSize);

            if (row >= 0 && row < gridHeight && col >= 0 && col < gridWidth) {
                particles[row][col].add(p);
            } else {
                System.out.printf("Out of bounds! x=%.2f, y=%.2f, col=%d, row=%d%n", x, y, col, row);
            }
        }

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Calculate FPS
                if (lastUpdateTime == 0) {
                    lastUpdateTime = now;
                    lastFpsUpdateTime = now;
                }

                frameCount++;

                // Update FPS once per second
                if (now - lastFpsUpdateTime > 1_000_000_000) {
                    currentFps = frameCount / ((now - lastFpsUpdateTime) / 1_000_000_000.0);
                    frameCount = 0;
                    lastFpsUpdateTime = now;
                }

                graphicsContext.setFill(Color.BLACK);
                graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                PhysicsEngine.updateAll(particles, worldWidth, worldHeight);
                camera.clampToWorld(worldWidth, worldHeight, canvas.getWidth(), canvas.getHeight());

                // Draw grid lines
                graphicsContext.setStroke(Color.rgb(255, 255, 255, 0.2)); // White with 20% opacity
                graphicsContext.setLineWidth(0.5);

                // Calculate visible world area on screen
                double worldStartX = camera.screenToWorldX(0);
                double worldStartY = camera.screenToWorldY(0);
                double worldEndX = camera.screenToWorldX(canvas.getWidth());
                double worldEndY = camera.screenToWorldY(canvas.getHeight());

                // Draw vertical grid lines
                for (int col = 0; col <= gridWidth; col++) {
                    double worldX = col * gridSize;
                    // Only draw if line is within world bounds
                    if (worldX >= 0 && worldX <= worldWidth) {
                        double screenX = camera.worldToScreenX(worldX);
                        // Constrain Y endpoints to world boundaries
                        double startY = Math.max(0, camera.worldToScreenY(0));
                        double endY = Math.min(canvas.getHeight(), camera.worldToScreenY(worldHeight));
                        graphicsContext.strokeLine(screenX, startY, screenX, endY);
                    }
                }

                // Draw horizontal grid lines
                for (int row = 0; row <= gridHeight; row++) {
                    double worldY = row * gridSize;
                    // Only draw if line is within world bounds
                    if (worldY >= 0 && worldY <= worldHeight) {
                        double screenY = camera.worldToScreenY(worldY);
                        // Constrain X endpoints to world boundaries
                        double startX = Math.max(0, camera.worldToScreenX(0));
                        double endX = Math.min(canvas.getWidth(), camera.worldToScreenX(worldWidth));
                        graphicsContext.strokeLine(startX, screenY, endX, screenY);
                    }
                }

                for (int row = 0; row < gridHeight; row++) {
                    for (int col = 0; col < gridWidth; col++) {
                        for (Particle p : particles[row][col]) {
                            p.display(graphicsContext, camera);
                        }
                    }
                }


                // Display FPS and grid size
                graphicsContext.setFill(Color.WHITE);
                graphicsContext.fillText(String.format("FPS: %.1f", currentFps), 10, 20);
                graphicsContext.fillText(String.format("Grid Size: %d x %d (Cell: %d)", gridWidth, gridHeight, gridSize), 10, 40);
                lastUpdateTime = now;

                // World info display
                graphicsContext.fillText(String.format("Particles: %d", numParticles), 10, 60);
                graphicsContext.fillText(String.format("World: %d x %d", worldWidth, worldHeight), 10, 80);
                graphicsContext.fillText(String.format("Max Force Radius: %.1f", 250.0), 10, 100);
                graphicsContext.fillText(String.format("Camera: (%.1f, %.1f) Zoom: %.2f", camera.x, camera.y, camera.zoom), 10, 120);

            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch();
    }
}

// Force, minDistance, radius <- different for species of particle.