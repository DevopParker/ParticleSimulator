package com.physics.particlesimulator;

public class Vector2D {
    public double x;
    public double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public void add(Vector2D vec) {
        this.x += vec.x;
        this.y += vec.y;
    }

    public void subtract(double x, double y) {
        this.x -= x;
        this.y -= y;
    }

    public void subtract(Vector2D vec) {
        this.x -= vec.x;
        this.y -= vec.y;
    }

    public void multiply(double x, double y) {
        this.x *= x;
        this.y *= y;
    }

    public void multiply(Vector2D vec) {
        this.x *= vec.x;
        this.y *= vec.y;
    }

    public void divide(double x, double y) {
        this.x /= x;
        this.y /= y;
    }

    public void divide(Vector2D vec) {
        this.x /= vec.x;
        this.y /= vec.y;
    }

    public void scale(double k) {
        this.x *= k;
        this.y *= k;
    }

    public double magnitude() {
        return Math.sqrt((x * x) + (y * y));
    }

    public Vector2D copy() {
        return new Vector2D(this.x, this.y);
    }

    public void normalize() {
        double magnitude = Math.sqrt((x * x) + (y * y));
        if (magnitude != 0) {
            x /= magnitude;
            y /= magnitude;
        }
    }

    public static double map(double value, double inMin, double inMax, double outMin, double outMax) {
        return (value - inMin) / (inMax - inMin) * (outMax - outMin) + outMin;
    }

    public static double random(double min, double max) {
        return min + Math.random() * (max - min);
    }

    public static double random(double max) {
        return Math.random() * max;
    }
}