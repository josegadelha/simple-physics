package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.constraints.Constraint;
import br.edu.ufersa.simplephysics.physics.constraints.ConstraintSettingsModel;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;
import br.edu.ufersa.simplephysics.physics.shape.ConvexPolygonShape;
import br.edu.ufersa.simplephysics.physics.shape.PhysicsShape;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {
    public static final double MAX_OBJECT_DISTANCE = 1000.0;
    private static final double VELOCITY_DAMPING_RATE = 2.0;
    private static final double VELOCITY_STOP_THRESHOLD = 1e-4;

    public List<PhysicsObject> objects;
    public List<Constraint> constraints;
    public Vector2 gravity;
    public int constraintIterations;
    public ConstraintSettingsModel constraintSettings;
    public boolean reduceVelocityEnabled;
    public boolean collisionsEnabled;

    public PhysicsWorld() {
        this.objects = new ArrayList<>();
        this.constraints = new ArrayList<>();
        this.gravity = new Vector2(0, -9.81);
        this.collisionsEnabled = false;
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

    public void solveConstraints(double dt, int IterationCount) {
        if (constraints.isEmpty() || IterationCount <= 0) {
            return;
        }

        for (Constraint constraint : constraints) {
            constraint.update(constraintSettings);
        }

        for (int iteration = 0; iteration < IterationCount; iteration++) {
            for (Constraint constraint : constraints) {
                constraint.solve(dt, constraintSettings);
            }
        }
    }

    public void step(double dt) {
        // Apply gravity only to dynamic objects.
        for (PhysicsObject object : objects) {
            if (!object.isStatic && !reduceVelocityEnabled) {
                object.velocity = object.velocity.add(gravity.scale(dt));
            }
        }

        // Solve constraints using Gauss-Seidel style loop.
        solveConstraints(dt, constraintIterations);

        // Apply velocity damping to reduce the velocity of dynamic objects over time.
        if (reduceVelocityEnabled) {
            double dampingFactor = Math.exp(-VELOCITY_DAMPING_RATE * dt);
            for (PhysicsObject object : objects) {
                if (object.isStatic) {
                    continue;
                }

                object.velocity = object.velocity.scale(dampingFactor);
                if (object.velocity.length() < VELOCITY_STOP_THRESHOLD) {
                    object.velocity = new Vector2(0, 0);
                }

                object.angularVelocity *= dampingFactor;
                if (Math.abs(object.angularVelocity) < VELOCITY_STOP_THRESHOLD) {
                    object.angularVelocity = 0;
                }
            }
        }

        // Update all objects.
        for (PhysicsObject object : objects) {
            object.step(dt);
        }

        // Remove objects that are too far away.
        objects.removeIf(object -> Math.hypot(object.position.x, object.position.y) > MAX_OBJECT_DISTANCE);

        // Detecta colisões se o botão estiver ativado
        if (this.collisionsEnabled) {
            for (int i = 0; i < objects.size(); i++) {
                for (int j = i + 1; j < objects.size(); j++) {
                    PhysicsObject objA = objects.get(i);
                    PhysicsObject objB = objects.get(j);

                    PhysicsShape shapeA = objA.shapes.get(0);
                    PhysicsShape shapeB = objB.shapes.get(0);
                    CollisionInfo info = null;

                    // Sort by geometric shape type
                    if (shapeA instanceof ConvexPolygonShape && shapeB instanceof ConvexPolygonShape) {
                        info = CollisionHelper.polyToPolySAT(objA, (ConvexPolygonShape) shapeA, objB, (ConvexPolygonShape) shapeB);
                    } else if (shapeA instanceof CircleShape && shapeB instanceof CircleShape) {
                        info = CollisionHelper.circleToCircle(objA, (CircleShape) shapeA, objB, (CircleShape) shapeB);
                    } else if (shapeA instanceof CircleShape && shapeB instanceof ConvexPolygonShape) {
                        info = CollisionHelper.circleToPolySAT(objA, (CircleShape) shapeA, objB, (ConvexPolygonShape) shapeB);
                    } else if (shapeA instanceof ConvexPolygonShape && shapeB instanceof CircleShape) {
                        info = CollisionHelper.circleToPolySAT(objB, (CircleShape) shapeB, objA, (ConvexPolygonShape) shapeA);
                    }

                    if (info != null) {
                        System.out.printf("Colisão detetada! Penetração: %.4f\n", info.penetration);

                        // Perform clipping only if both are polygons
                        if (shapeA instanceof ConvexPolygonShape && shapeB instanceof ConvexPolygonShape) {
                            List<Vector2> pontosDeContacto;

                            if (info.referenceIsA) {
                                pontosDeContacto = CollisionHelper.cortarPoligonoComPoligono(objA, (ConvexPolygonShape) shapeA, objB, (ConvexPolygonShape) shapeB, info);
                            } else {
                                pontosDeContacto = CollisionHelper.cortarPoligonoComPoligono(objB, (ConvexPolygonShape) shapeB, objA, (ConvexPolygonShape) shapeA, info);
                            }

                            for (Vector2 ponto : pontosDeContacto) {
                                System.out.printf("Ponto de contacto em X: %.2f, Y: %.2f\n", ponto.x, ponto.y);
                            }
                        }
                    }
                }
            }
        }
    }
}
