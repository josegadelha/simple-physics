package br.edu.ufersa.simplephysics.math;

public class Vector2 {
    public double x;
    public double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    public Vector2 subtract(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    public Vector2 scale(double scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    public double dotProduct(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    // Perpendicular dotProduct
    // Same as taking dotProduct while rotating the "other" vector 90 degrees clockwise
    public double crossProduct(Vector2 other) {
        return this.x * other.y - this.y * other.x;
    }

    // Here scalar is the angular velocity in radians
    public Vector2 crossProductScalarVector(double scalar) {
        return new Vector2(-this.y * scalar, this.x * scalar);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public Vector2 rotate90CW() {
        return new Vector2(this.y, -this.x);
    }
}
