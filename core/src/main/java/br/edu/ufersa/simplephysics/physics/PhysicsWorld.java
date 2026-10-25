package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;
import br.edu.ufersa.simplephysics.physics.shape.ConvexPolygonShape;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {
    public List<PhysicsObject> objects;
    public Vector2 gravity;

    public PhysicsWorld() {
        this.objects = new ArrayList<>();
        this.gravity = new Vector2(0, -9.81);
    }

    public void addSquare(double x, double y, double sideLength, boolean isStatic) {
        if (!Double.isFinite(sideLength) || sideLength <= 0) {
            throw new IllegalArgumentException("Square side length must be finite and positive");
        }

        double halfSideLength = sideLength / 2;
        double mass = sideLength * sideLength;
        double momentOfInertia = mass * sideLength * sideLength / 6;
        PhysicsObject square = new PhysicsObject(
            x,
            y,
            isStatic,
            mass,
            momentOfInertia,
            new ConvexPolygonShape(
                new Vector2(-halfSideLength, -halfSideLength),
                new Vector2(halfSideLength, -halfSideLength),
                new Vector2(halfSideLength, halfSideLength),
                new Vector2(-halfSideLength, halfSideLength)
            )
        );
        objects.add(square);
    }

    public void addCircle(double x, double y, double radius, boolean isStatic) {
        if (!Double.isFinite(radius) || radius <= 0) {
            throw new IllegalArgumentException("Circle radius must be finite and positive");
        }

        double mass = Math.PI * radius * radius;
        double momentOfInertia = mass * radius * radius / 2;
        PhysicsObject circle = new PhysicsObject(
            x,
            y,
            isStatic,
            mass,
            momentOfInertia,
            new CircleShape(radius)
        );
        objects.add(circle);
    }

    public void step(double dt) {
        // Apply gravity only to dynamic objects.
        for (PhysicsObject object : objects) {
            if (!object.isStatic) {
                object.velocity = object.velocity.add(gravity.scale(dt));
            }
        }

        // Update all objects
        for (PhysicsObject object : objects) {
            object.step(dt);
        }
    }
}
