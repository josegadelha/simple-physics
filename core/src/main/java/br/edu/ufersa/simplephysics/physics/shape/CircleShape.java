package br.edu.ufersa.simplephysics.physics.shape;

import br.edu.ufersa.simplephysics.math.Vector2;

public class CircleShape implements PhysicsShape {
    public final double radius;
    public final Vector2 offset;

    public CircleShape(double radius) {
        this(radius, new Vector2(0, 0));
    }

    public CircleShape(double radius, Vector2 offset) {
        if (radius <= 0 || Double.isNaN(radius) || Double.isInfinite(radius)) {
            throw new IllegalArgumentException("Radius must be a finite positive value");
        }

        this.radius = radius;
        this.offset = offset;
    }
}
