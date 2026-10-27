
package br.edu.ufersa.simplephysics.physics.constraints;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.PhysicsObject;

// Sequential impulse constraint for one contact point between two bodies.
public class ContactConstraint {

    private static final double EPSILON = 1e-6;
    private static final double BAUMGARTE = 0.05;
    private static final double POSITION_SLOP = 0.005;

    public final PhysicsObject bodyA;
    public final PhysicsObject bodyB;
    public final Vector2 worldPoint;
    public final Vector2 normal;
    public final Vector2 tangent;
    public final double penetration;
    public final double friction;

    private final Vector2 rA;
    private final Vector2 rB;

    private double accumulatedNormalLambda;
    private double accumulatedFrictionLambda;

    // Construtor original: começa sem impulsos acumulados.
    public ContactConstraint(
            PhysicsObject objA,
            PhysicsObject objB,
            Vector2 worldPoint,
            Vector2 normal,
            double penetration) {

        this(objA, objB, worldPoint, normal, penetration, 0, 0);
    }

    // Permite recuperar os impulsos do quadro anterior.
    public ContactConstraint(
            PhysicsObject objA,
            PhysicsObject objB,
            Vector2 worldPoint,
            Vector2 normal,
            double penetration,
            double previousNormalLambda,
            double previousFrictionLambda) {

        if (objA == null || objB == null
                || worldPoint == null || normal == null) {
            throw new IllegalArgumentException(
                "Bodies, contact point, and normal are required"
            );
        }

        double normalLength = normal.length();

        if (normalLength < EPSILON) {
            throw new IllegalArgumentException(
                "Contact normal must be non-zero"
            );
        }

        this.bodyA = objA;
        this.bodyB = objB;
        this.worldPoint = worldPoint;
        this.normal = normal.scale(1.0 / normalLength);
        this.tangent = this.normal.rotate90CW();
        this.penetration = Math.max(0, penetration);

        this.friction = Math.sqrt(
            Math.max(0, objA.friction)
                * Math.max(0, objB.friction)
        );

        this.rA = worldPoint.subtract(objA.position);
        this.rB = worldPoint.subtract(objB.position);

        // O impulso normal nunca pode ser negativo.
        this.accumulatedNormalLambda =
            Math.max(0, previousNormalLambda);

        // O impulso tangencial deve respeitar o limite de Coulomb.
        double maxFriction =
            this.friction * this.accumulatedNormalLambda;

        this.accumulatedFrictionLambda = Math.max(
            -maxFriction,
            Math.min(previousFrictionLambda, maxFriction)
        );
    }

    // Reaplica os impulsos recuperados uma única vez por quadro.
    public void update() {
        Vector2 impulse = normal.scale(accumulatedNormalLambda)
            .add(tangent.scale(accumulatedFrictionLambda));

        applyPairImpulse(impulse);
    }

    public void solve(double dt, boolean useBias) {
        solveContact(dt, useBias);
        solveFriction();
    }

    private void solveContact(double dt, boolean useBias) {
        Vector2 relativeVelocity =
            pointVelocity(bodyB, rB).subtract(pointVelocity(bodyA, rA));

        double rnA = rA.crossProduct(normal);
        double rnB = rB.crossProduct(normal);

        double effectiveMass =
            inverseMass(bodyA) + inverseMass(bodyB)
                + rnA * rnA * inverseInertia(bodyA)
                + rnB * rnB * inverseInertia(bodyB);

        if (effectiveMass < EPSILON) {
            return;
        }

        double bias = useBias && dt > 0
            ? BAUMGARTE * Math.max(penetration - POSITION_SLOP, 0) / dt
            : 0;

        double lambda =
            -(normal.dotProduct(relativeVelocity) - bias) / effectiveMass;

        double oldAccumulated = accumulatedNormalLambda;

        accumulatedNormalLambda = Math.max(
            oldAccumulated + lambda, 0
        );

        // Aplica somente a diferença do impulso acumulado.
        applyPairImpulse(
            normal.scale(accumulatedNormalLambda - oldAccumulated)
        );
    }

    private void solveFriction() {
        if (friction <= 0) {
            return;
        }

        Vector2 relativeVelocity =
            pointVelocity(bodyB, rB).subtract(pointVelocity(bodyA, rA));

        double rtA = rA.crossProduct(tangent);
        double rtB = rB.crossProduct(tangent);

        double effectiveMass =
            inverseMass(bodyA) + inverseMass(bodyB)
                + rtA * rtA * inverseInertia(bodyA)
                + rtB * rtB * inverseInertia(bodyB);

        if (effectiveMass < EPSILON) {
            return;
        }

        double lambda =
            -tangent.dotProduct(relativeVelocity) / effectiveMass;

        double maxFriction = friction * accumulatedNormalLambda;
        double oldAccumulated = accumulatedFrictionLambda;

        accumulatedFrictionLambda = Math.max(
            -maxFriction,
            Math.min(oldAccumulated + lambda, maxFriction)
        );

        // Aplica somente a diferença do impulso tangencial.
        applyPairImpulse(
            tangent.scale(accumulatedFrictionLambda - oldAccumulated)
        );
    }

    private void applyPairImpulse(Vector2 impulse) {
        bodyA.applyImpulse(impulse.scale(-1), worldPoint);
        bodyB.applyImpulse(impulse, worldPoint);
    }

    private static Vector2 pointVelocity(
            PhysicsObject body, Vector2 radius) {

        return body.velocity.add(
            radius.crossProductScalarVector(body.angularVelocity)
        );
    }

    private static double inverseMass(PhysicsObject body) {
        return body.isStatic ? 0 : 1.0 / body.mass;
    }

    private static double inverseInertia(PhysicsObject body) {
        return body.isStatic ? 0 : 1.0 / body.momentOfInertia;
    }

    public double getAccumulatedNormalLambda() {
        return accumulatedNormalLambda;
    }

    public double getAccumulatedFrictionLambda() {
        return accumulatedFrictionLambda;
    }
}
