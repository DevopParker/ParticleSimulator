package com.physics.particlesimulator;

public class Camera2D {
    public double x;      // top-left corner x in world space
    public double y;
    public double zoom;   // 1.0 = 100%, >1 = zoom in, <1 = zoom out
    public double minZoom = 0.1; // dynamically calculated based on world and screen size
    public double maxZoom = 5.0; // default, but override dynamically at runtime


    public Camera2D(double x, double y, double zoom) {
        this.x = x;
        this.y = y;
        this.zoom = zoom;
    }

    public double worldToScreenX(double worldX) {
        return (worldX - x) * zoom;
    }

    public double worldToScreenY(double worldY) {
        return (worldY - y) * zoom;
    }

    public double screenToWorldX(double screenX) {
        return screenX / zoom + x;
    }

    public double screenToWorldY(double screenY) {
        return screenY / zoom + y;
    }

    public void move(double dx, double dy) {
        x += dx / zoom;
        y += dy / zoom;
    }

    public void zoomBy(double factor) {
        zoom *= factor;
        zoom = Math.max(minZoom, Math.min(zoom, maxZoom));
    }

    public void clampToWorld(double worldWidth, double worldHeight, double screenWidth, double screenHeight) {
        double viewWidth = screenWidth / zoom;
        double viewHeight = screenHeight / zoom;
        double padding = 200 / zoom;

        // Apply clamping to both dimensions
        x = clampPosition(x, viewWidth, worldWidth, padding);
        y = clampPosition(y, viewHeight, worldHeight, padding);
    }

    private double clampPosition(double position, double viewSize, double worldSize, double padding) {
        if (viewSize >= worldSize + 2 * padding) {
            // Center the view if it's larger than the world
            return (worldSize - viewSize) / 2;
        } else {
            // Otherwise, clamp within bounds
            double min = -padding;
            double max = worldSize - viewSize + padding;
            return Math.max(min, Math.min(position, max));
        }
    }

    public void zoomAroundScreenCenter(double factor, double screenWidth, double screenHeight) {
        double worldXBefore = screenToWorldX(screenWidth / 2);
        double worldYBefore = screenToWorldY(screenHeight / 2);

        zoom *= factor;
        zoom = Math.max(minZoom, Math.min(zoom, 5.0));

        double worldXAfter = screenToWorldX(screenWidth / 2);
        double worldYAfter = screenToWorldY(screenHeight / 2);

        x += (worldXBefore - worldXAfter);
        y += (worldYBefore - worldYAfter);
    }

    public void centerOnWorld(double worldWidth, double worldHeight, double screenWidth, double screenHeight) {
        x = (worldWidth - (screenWidth / zoom)) / 2;
        y = (worldHeight - (screenHeight / zoom)) / 2;
    }
}

// System.out.printf("Zoom: %.2f | Camera: (%.2f, %.2f)%n", zoom, x, y);