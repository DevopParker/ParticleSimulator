package com.physics.particlesimulator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Particle {
    Vector2D position;
    Vector2D velocity;
    public double radius = 3; // radius of the particles.
    public int type;
    public Color color;

    public Particle(Vector2D position, Vector2D velocity, int type, Color color) {
        this.position = position;
        this.velocity = velocity;
        this.type = type;
        this.color = color;
    }

    public void display(GraphicsContext graphicsContext, Camera2D camera) {
        double screenX = camera.worldToScreenX(position.x);
        double screenY = camera.worldToScreenY(position.y);
        double radiusZoomed = radius * camera.zoom;

        graphicsContext.setFill(color);
        graphicsContext.fillOval(screenX, screenY, radiusZoomed, radiusZoomed);
    }
}