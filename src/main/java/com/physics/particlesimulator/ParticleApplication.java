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

        int numParticles = 10000;
        int maxDistance = 100;
        int gridSize = maxDistance;
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
                graphicsContext.setFill(Color.BLACK);
                graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                PhysicsEngine.updateAll(particles, worldWidth, worldHeight);
                camera.clampToWorld(worldWidth, worldHeight, canvas.getWidth(), canvas.getHeight());

                for (int row = 0; row < gridHeight; row++) {
                    for (int col = 0; col < gridWidth; col++) {
                        for (Particle p : particles[row][col]) {
                            p.display(graphicsContext, camera);
                        }
                    }
                }
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch();
    }
}

// Force, minDistance, radius <- different for species of particle.