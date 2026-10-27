package br.edu.ufersa.simplephysics.physics;
import br.edu.ufersa.simplephysics.physics.constraints.ContactConstraint;
import br.edu.ufersa.simplephysics.physics.collision.CollisionData;
import br.edu.ufersa.simplephysics.physics.collision.CollisionHelper;
import br.edu.ufersa.simplephysics.physics.shape.PhysicsShape;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.constraints.Constraint;
import br.edu.ufersa.simplephysics.physics.constraints.ConstraintSettingsModel;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;
import br.edu.ufersa.simplephysics.physics.shape.ConvexPolygonShape;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {
    public static final double MAX_OBJECT_DISTANCE = 1000.0;

    public List<PhysicsObject> objects;
    public List<Constraint> constraints;
    public Vector2 gravity;
    public int constraintIterations;
    public ConstraintSettingsModel constraintSettings;

    public PhysicsWorld() {
        this.objects = new ArrayList<>();
        this.constraints = new ArrayList<>();
        this.gravity = new Vector2(0, -9.81);
        this.constraintIterations = 10;
        this.constraintSettings = new ConstraintSettingsModel();
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

    public void addConstraint(Constraint constraint) {
        if (constraint == null) {
            throw new IllegalArgumentException("Constraint cannot be null.");
        }
        constraints.add(constraint);
    }

    public void solveConstraints(double dt, int iterationCount) {
        if (constraints.isEmpty() || iterationCount <= 0) {
            return;
        }

        for (Constraint constraint : constraints) {
            constraint.update(constraintSettings);
        }

        for (int iteration = 0; iteration < iterationCount; iteration++) {
            for (Constraint constraint : constraints) {
                constraint.solve(dt, constraintSettings);
            }
        }
    }


    public void step(double dt) {
        // Apply gravity only to dynamic objects.
        for (PhysicsObject object : objects) {
            if (!object.isStatic) {
                object.velocity = object.velocity.add(gravity.scale(dt));
            }
        }

        // Solve constraints using Gauss-Seidel style loop.
        solveConstraints(dt, constraintIterations);

        // Update all objects.
        for (PhysicsObject object : objects) {
            object.step(dt);
        }

        // Remove objects that are too far away.
        objects.removeIf(object -> Math.hypot(object.position.x, object.position.y) > MAX_OBJECT_DISTANCE);
    }
}
