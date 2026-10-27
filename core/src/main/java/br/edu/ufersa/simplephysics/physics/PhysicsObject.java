package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.shape.PhysicsShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhysicsObject {

    public Vector2 position;
    public Vector2 velocity;
    public double rotation;
    public double angularVelocity;
    public boolean isStatic;
    public double mass;
    public double momentOfInertia;
    public double friction = 0.3;
    public final List<PhysicsShape> shapes;

    public PhysicsObject(
        double x,
        double y,
        boolean isStatic,
        double mass,
        double momentOfInertia,
        PhysicsShape... shapes

    ) {

        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException(
                "Position coordinates must be finite"
            );
        }

        if (shapes == null || shapes.length == 0) {
            throw new IllegalArgumentException(
                "At least one shape is required"
            );
        }

        for (PhysicsShape shape : shapes) {
            if (shape == null) {
                throw new IllegalArgumentException(
                    "Shapes cannot contain null elements"
                );
            }
        }

        if (!isStatic) {
            if (!Double.isFinite(mass) || mass <= 0) {
                throw new IllegalArgumentException("Mass must be finite and positive");
            }
            if (!Double.isFinite(momentOfInertia) || momentOfInertia <= 0) {
                throw new IllegalArgumentException("Moment of inertia must be finite and positive");
            }
        }

        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.rotation = 0;
        this.angularVelocity = 0;
        this.isStatic = isStatic;
        this.mass = isStatic ? Double.POSITIVE_INFINITY : mass;
        this.momentOfInertia = isStatic ? Double.POSITIVE_INFINITY : momentOfInertia;
        this.shapes = new ArrayList<>(Arrays.asList(shapes));
    }


    public void step(double dt) {
        if (!Double.isFinite(dt) || dt < 0) {
            throw new IllegalArgumentException(
                "Time step must be finite and non-negative"
            );
        }

        if (isStatic) return;

        this.position = this.position.add(this.velocity.scale(dt));
        this.rotation += this.angularVelocity * dt;
    }


    public Vector2 localToWorld(Vector2 localPoint) {
        // Calculate cosine and sine of the object's rotation angle
        double cos = Math.cos(rotation);
        double sin = Math.sin(rotation);

        // Rotate the local point and add the object's world X and Y positions
        return new Vector2(
            position.x + localPoint.x * cos - localPoint.y * sin,
            position.y + localPoint.x * sin + localPoint.y * cos
        );
    }

    public Vector2 worldToLocal(Vector2 worldPoint) {
        // Calculate cosine and sine of the inverse rotation angle
        double cos = Math.cos(-rotation);
        double sin = Math.sin(-rotation);

        // Subtract the object's world position to get the point relative to its origin
        Vector2 relativePoint = worldPoint.subtract(position);

        // Apply the inverse rotation to calculate the local X and Y coordinates
        return new Vector2(
            relativePoint.x * cos - relativePoint.y * sin,
            relativePoint.x * sin + relativePoint.y * cos
        );
    }


    public void applyImpulse(Vector2 impulse, Vector2 contactPoint) {
        if (impulse == null) {
            throw new IllegalArgumentException(
                "Impulse cannot be null"
            );
        }

        if (contactPoint == null) {
            throw new IllegalArgumentException(
                "Contact point cannot be null"
            );
        }

        if (!Double.isFinite(impulse.x)
            || !Double.isFinite(impulse.y)) {
            throw new IllegalArgumentException(
                "Impulse components must be finite"
            );
        }

        if (!Double.isFinite(contactPoint.x)
            || !Double.isFinite(contactPoint.y)) {
            throw new IllegalArgumentException(
                "Contact point coordinates must be finite"
            );
        }

        if (isStatic) return;

        Vector2 contactOffset = contactPoint.subtract(this.position);
        double angularImpulse = contactOffset.crossProduct(impulse);

        // Update linear velocity based on the impulse and the object's mass
        this.velocity = this.velocity.add(impulse.scale(1.0 / this.mass));

        // Update angular velocity based on the angular impulse and moment of inertia
        this.angularVelocity += angularImpulse / this.momentOfInertia;
    }

}
