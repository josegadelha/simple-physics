
package br.edu.ufersa.simplephysics.physics.constraints;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.PhysicsObject;

public class ContactConstraint extends Constraint {

    private static final double SLOP_LINEAR = 0.002;
    private static final double MIN_EFFECTIVE_MASS = 0.000001;
    private static final double RESTITUTION_THRESHOLD = 1.0;

    public Object featureId;
    public boolean isReused = false;

    public double friction;
    public double restitution;

    public double accumulatedNormalLambda = 0.0;
    public double accumulatedFrictionLambda = 0.0;

    private double invMassA;
    private double invMassB;
    private double invIA;
    private double invIB;

    private Vector2 worldPoint;
    private Vector2 normal;
    private Vector2 tangent;

    private Vector2 localA;
    private Vector2 localB;
    private Vector2 worldA;
    private Vector2 worldB;
    private Vector2 rA;
    private Vector2 rB;

    private double penetration;
    private double relativeVelocity;

    public ContactConstraint(
        PhysicsObject objA,
        PhysicsObject objB,
        Vector2 worldPoint,
        Vector2 normal,
        double penetration,
        Object featureId) {

        super(objA, objB);

        if (worldPoint == null || normal == null) {
            throw new IllegalArgumentException(
                "O ponto de contato e a normal não podem ser nulos."
            );
        }

        if (!Double.isFinite(penetration) || penetration < 0) {
            throw new IllegalArgumentException(
                "A penetração deve ser finita e não negativa."
            );
        }

        this.featureId = featureId;

        this.friction = Math.sqrt(
            Math.max(0.0, objA.friction)
                * Math.max(0.0, objB.friction)
        );

        this.restitution = Math.sqrt(
            Math.max(0.0, objA.restitution)
                * Math.max(0.0, objB.restitution)
        );

        atualizarMassasInversas();

        setCollisionData(worldPoint, normal, penetration);

        // Guarda a velocidade relativa antes da resolução.
        Vector2 velA = velocidadeNoPonto(
            bodyA, rA
        );

        Vector2 velB = velocidadeNoPonto(
            bodyB, rB
        );

        Vector2 relVel = velB.subtract(velA);
        this.relativeVelocity = this.normal.dotProduct(relVel);
    }

    private void atualizarMassasInversas() {
        invMassA = bodyA.isStatic ? 0.0 : 1.0 / bodyA.mass;
        invMassB = bodyB.isStatic ? 0.0 : 1.0 / bodyB.mass;

        invIA = bodyA.isStatic
            ? 0.0 : 1.0 / bodyA.momentOfInertia;

        invIB = bodyB.isStatic
            ? 0.0 : 1.0 / bodyB.momentOfInertia;
    }

    private Vector2 velocidadeNoPonto(
        PhysicsObject body,
        Vector2 radius) {

        // Velocidade linear + velocidade tangencial angular.
        double w = body.angularVelocity;

        Vector2 velocidadeAngular = new Vector2(
            -w * radius.y,
            w * radius.x
        );

        return body.velocity.add(velocidadeAngular);
    }

    public void setCollisionData(
        Vector2 worldPoint,
        Vector2 normal,
        double penetration) {

        if (worldPoint == null || normal == null) {
            throw new IllegalArgumentException(
                "O ponto e a normal não podem ser nulos."
            );
        }

        double normalLength = normal.length();

        if (!Double.isFinite(normalLength) || normalLength < 1e-9) {
            throw new IllegalArgumentException(
                "A normal deve ser diferente de zero."
            );
        }

        if (!Double.isFinite(penetration) || penetration < 0) {
            throw new IllegalArgumentException(
                "A penetração deve ser finita e não negativa."
            );
        }

        this.worldPoint = worldPoint;
        this.normal = normal.scale(1.0 / normalLength);
        this.penetration = penetration;

        this.localA = bodyA.worldToLocal(worldPoint);
        this.localB = bodyB.worldToLocal(worldPoint);

        this.worldA = bodyA.localToWorld(localA);
        this.worldB = bodyB.localToWorld(localB);

        this.rA = worldA.subtract(bodyA.position);
        this.rB = worldB.subtract(bodyB.position);

        this.tangent = this.normal.rotate90CW();
    }

    @Override
    public void update(Object settings) {
        // Atualiza os pontos de contato com as posições atuais.
        worldA = bodyA.localToWorld(localA);
        worldB = bodyB.localToWorld(localB);

        rA = worldA.subtract(bodyA.position);
        rB = worldB.subtract(bodyB.position);

        atualizarMassasInversas();

        // A velocidade é guardada antes do warm starting.
        Vector2 velA = velocidadeNoPonto(bodyA, rA);
        Vector2 velB = velocidadeNoPonto(bodyB, rB);

        relativeVelocity = normal.dotProduct(
            velB.subtract(velA)
        );

        // Warm starting: reaplica os impulsos acumulados.
        Vector2 normalImpulse =
            normal.scale(accumulatedNormalLambda);

        Vector2 frictionImpulse =
            tangent.scale(accumulatedFrictionLambda);

        Vector2 totalImpulse =
            normalImpulse.add(frictionImpulse);

        bodyA.velocity = bodyA.velocity.subtract(
            totalImpulse.scale(invMassA)
        );

        bodyA.angularVelocity -=
            invIA * rA.crossProduct(totalImpulse);

        bodyB.velocity = bodyB.velocity.add(
            totalImpulse.scale(invMassB)
        );

        bodyB.angularVelocity +=
            invIB * rB.crossProduct(totalImpulse);
    }

    @Override
    public void solve(double dt, Object settings) {
        Object selectedSettings =
            this.constraintSettings != null
                ? this.constraintSettings
                : settings;

        if (!(selectedSettings instanceof ConstraintSettingsModel)) {
            throw new IllegalArgumentException(
                "As configurações devem ser ConstraintSettingsModel."
            );
        }

        if (!Double.isFinite(dt) || dt <= 0.0) {
            throw new IllegalArgumentException(
                "O intervalo dt deve ser finito e positivo."
            );
        }

        ConstraintSettingsModel cs =
            (ConstraintSettingsModel) selectedSettings;

        solveContact(dt, cs);
        solveFriction();
    }

    private void solveContact(
        double dt,
        ConstraintSettingsModel cs) {

        Vector2 velA = velocidadeNoPonto(bodyA, rA);
        Vector2 velB = velocidadeNoPonto(bodyB, rB);

        Vector2 relVel = velB.subtract(velA);
        double cdot = normal.dotProduct(relVel);

        double rnA = rA.crossProduct(normal);
        double rnB = rB.crossProduct(normal);

        double effectiveMass =
            invMassA + invMassB
                + rnA * rnA * invIA
                + rnB * rnB * invIB;

        if (effectiveMass < MIN_EFFECTIVE_MASS) {
            return;
        }

        double velocityBias = 0.0;
        double massScale = 1.0;
        double impulseScale = 0.0;

        double separation = Math.min(
            0.0, -penetration + SLOP_LINEAR
        );

        if ("baumgarte".equals(cs.mode)) {
            velocityBias =
                (cs.baumgarteFactor / dt) * separation;

        } else if ("soft".equals(cs.mode)) {
            double maxHertz = 0.25 / dt;
            double hz = Math.min(cs.contactSoft.hertz, maxHertz);

            SoftParams soft = getSoftConstraintParams(
                hz, cs.contactSoft.dampingRatio, dt
            );

            velocityBias = Math.max(
                soft.biasRate * separation,
                -cs.contactSpeed
            );

            massScale = soft.massScale;
            impulseScale = soft.impulseScale;
        }

        double lambda =
            -(massScale * cdot + velocityBias) / effectiveMass;

        lambda +=
            -impulseScale * accumulatedNormalLambda
                / effectiveMass;

        double oldAccum = accumulatedNormalLambda;

        accumulatedNormalLambda = Math.max(
            oldAccum + lambda, 0.0
        );

        lambda = accumulatedNormalLambda - oldAccum;

        if (lambda == 0.0) {
            return;
        }

        Vector2 impulse = normal.scale(lambda);

        bodyA.applyImpulse(impulse.scale(-1.0), worldA);
        bodyB.applyImpulse(impulse, worldB);
    }

    private void solveFriction() {
        if (friction <= 0.0) {
            return;
        }

        Vector2 velA = velocidadeNoPonto(bodyA, rA);
        Vector2 velB = velocidadeNoPonto(bodyB, rB);

        Vector2 relVel = velB.subtract(velA);
        double cdot = tangent.dotProduct(relVel);

        double rtA = rA.crossProduct(tangent);
        double rtB = rB.crossProduct(tangent);

        double effectiveMassTangent =
            invMassA + invMassB
                + rtA * rtA * invIA
                + rtB * rtB * invIB;

        if (effectiveMassTangent < MIN_EFFECTIVE_MASS) {
            return;
        }

        double lambda = -cdot / effectiveMassTangent;

        double maxFriction =
            friction * accumulatedNormalLambda;

        double oldAccum = accumulatedFrictionLambda;

        accumulatedFrictionLambda = Math.max(
            -maxFriction,
            Math.min(oldAccum + lambda, maxFriction)
        );

        lambda = accumulatedFrictionLambda - oldAccum;

        Vector2 frictionImpulse = tangent.scale(lambda);

        bodyA.applyImpulse(
            frictionImpulse.scale(-1.0), worldA
        );

        bodyB.applyImpulse(frictionImpulse, worldB);
    }

    public void applyRestitution() {
        if (restitution == 0.0 || isReused) {
            return;
        }

        if (relativeVelocity > -RESTITUTION_THRESHOLD) {
            return;
        }

        double rnA = rA.crossProduct(normal);
        double rnB = rB.crossProduct(normal);

        double effectiveMass =
            invMassA + invMassB
                + rnA * rnA * invIA
                + rnB * rnB * invIB;

        if (effectiveMass < MIN_EFFECTIVE_MASS) {
            return;
        }

        Vector2 velA = velocidadeNoPonto(bodyA, rA);
        Vector2 velB = velocidadeNoPonto(bodyB, rB);

        double vn = normal.dotProduct(
            velB.subtract(velA)
        );

        double impulse =
            -(vn + restitution * relativeVelocity)
                / effectiveMass;

        if (impulse > 0.0) {
            Vector2 restitutionImpulse = normal.scale(impulse);

            bodyA.applyImpulse(
                restitutionImpulse.scale(-1.0), worldA
            );

            bodyB.applyImpulse(
                restitutionImpulse, worldB
            );
        }
    }

    private static class SoftParams {
        final double biasRate;
        final double massScale;
        final double impulseScale;

        SoftParams(
            double biasRate,
            double massScale,
            double impulseScale) {

            this.biasRate = biasRate;
            this.massScale = massScale;
            this.impulseScale = impulseScale;
        }
    }


    private static SoftParams getSoftConstraintParams(
        double hertz,
        double dampingRatio,
        double timeStep) {

        if (hertz == 0.0) {
            return new SoftParams(0.0, 0.0, 0.0);
        }

        double omega = 2.0 * Math.PI * hertz;

        double a1 = 2.0 * dampingRatio + timeStep * omega;
        double a2 = timeStep * omega * a1;
        double a3 = 1.0 / (1.0 + a2);

        double biasRate = omega / a1;
        double massScale = a2 * a3;
        double impulseScale = a3;

        return new SoftParams(
            biasRate,
            massScale,
            impulseScale
        );
    }

}
