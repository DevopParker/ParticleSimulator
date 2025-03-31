package com.physics.particlesimulator;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {
    public static double[][] forces;
    public static double[][] minDistance;
    public static double[][] radii;

    public static void setParameters(int numTypes) {
        forces = new double[numTypes][numTypes];
        minDistance = new double[numTypes][numTypes];
        radii = new double[numTypes][numTypes];

        for (int i = 0; i < numTypes; i++) {
            for (int j = 0; j < numTypes; j++) {
                forces[i][j] = Vector2D.random(0.3,1.0);
                if (Vector2D.random(100) < 50) {
                    forces[i][j] *= -1;
                }
                minDistance[i][j] = Vector2D.random(30, 50);
                radii[i][j] = Vector2D.random(70, 250); // 70, 250 was the default but grid size matters
            }
        }
    }

    public static void updateAll(ArrayList<Particle>[][] grid, double width, double height) {
        double K = 0.05; // Scaling factor for forces
        double friction = 0.85; // Friction coefficient
        int gridHeight = grid.length;
        int gridWidth = grid[0].length;
        double gridSize = width / gridWidth;

        // List to track particles that need to move to new cells
        List<ParticleMove> particlesToMove = new ArrayList<>();

        // Process each cell in the grid
        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                ArrayList<Particle> currentCell = grid[row][col];

                // Skip empty cells
                if (currentCell.isEmpty()) continue;

                // For each particle in the current cell
                for (int i = 0; i < currentCell.size(); i++) {
                    Particle a = currentCell.get(i);
                    Vector2D totalForce = new Vector2D(0, 0);
                    Vector2D acceleration = new Vector2D(0, 0);

                    // Check interactions with other particles in current cell
                    for (int j = 0; j < currentCell.size(); j++) {
                        if (i != j) {
                            applyForces(a, currentCell.get(j), totalForce, width, height, K);
                        }
                    }

                    // Calculate cell radius based on maximum interaction distance
                    int cellRadius = (int)Math.ceil(250.0 / gridSize); // 3 with grid size 100 and max radius 250

// Check neighboring cells within calculated radius (with toroidal wrapping)
                    for (int dRow = -cellRadius; dRow <= cellRadius; dRow++) {
                        for (int dCol = -cellRadius; dCol <= cellRadius; dCol++) {
                            int nRow = (row + dRow + gridHeight) % gridHeight;
                            int nCol = (col + dCol + gridWidth) % gridWidth;

                            // Skip the current cell (already processed)
                            if (nRow == row && nCol == col) continue;

                            ArrayList<Particle> neighborCell = grid[nRow][nCol];
                            for (Particle b : neighborCell) {
                                applyForces(a, b, totalForce, width, height, K);
                            }
                        }
                    }


                    // Update particle velocity and position
                    acceleration.add(totalForce);
                    a.velocity.add(acceleration);
                    a.position.add(a.velocity);

                    // Handle wrapping around world borders
                    a.position.x = (a.position.x + width) % width;
                    a.position.y = (a.position.y + height) % height;
                    a.velocity.scale(friction);

                    // Check if particle moved to a different cell
                    int newRow = (int)(a.position.y / gridSize);
                    int newCol = (int)(a.position.x / gridSize);

                    // Ensure new coordinates are within bounds
                    newRow = Math.min(Math.max(newRow, 0), gridHeight - 1);
                    newCol = Math.min(Math.max(newCol, 0), gridWidth - 1);

                    // Check if particle needs to move to a different cell
                    if (newRow != row || newCol != col) {
                        particlesToMove.add(new ParticleMove(a, row, col, newRow, newCol));
                    }
                }
            }
        }

        // Move particles to new cells
        for (ParticleMove move : particlesToMove) {
            grid[move.oldRow][move.oldCol].remove(move.particle);
            grid[move.newRow][move.newCol].add(move.particle);
        }
    }

    // Helper class to track particle movements
    private static class ParticleMove {
        Particle particle;
        int oldRow, oldCol, newRow, newCol;

        ParticleMove(Particle p, int or, int oc, int nr, int nc) {
            particle = p;
            oldRow = or;
            oldCol = oc;
            newRow = nr;
            newCol = nc;
        }
    }

    // Helper method to calculate and apply forces between particles
    private static void applyForces(Particle a, Particle b, Vector2D totalForce, double width, double height, double K) {
        Vector2D direction = b.position.copy();
        direction.subtract(a.position);

        // Handle wrapping around the world borders
        if (direction.x > 0.5 * width) direction.x -= width;
        if (direction.x < -0.5 * width) direction.x += width;
        if (direction.y > 0.5 * height) direction.y -= height;
        if (direction.y < -0.5 * height) direction.y += height;

        double distance = direction.magnitude();
        if (distance > 0) { // Prevent division by zero
            direction.normalize();

            // Repulsive force
            if (distance < minDistance[a.type][b.type]) {
                Vector2D force = direction.copy();
                force.scale(Math.abs(forces[a.type][b.type]) * -3);
                force.scale(Vector2D.map(distance, 0, minDistance[a.type][b.type], 1, 0));
                force.scale(K);
                totalForce.add(force);
            }

            // Attraction Force
            if (distance < radii[a.type][b.type]) {
                Vector2D force = direction.copy();
                force.scale(forces[a.type][b.type]);
                force.scale(Vector2D.map(distance, 0, radii[a.type][b.type], 1, 0));
                force.scale(K);
                totalForce.add(force);
            }
        }
    }
}