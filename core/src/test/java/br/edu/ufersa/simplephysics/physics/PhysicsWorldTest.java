package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.constraints.Constraint;
import br.edu.ufersa.simplephysics.physics.constraints.RevoluteConstraint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhysicsWorldTest {
    private static final double DELTA = 1e-9;

    @Test
    void initializesWithDefaultGravityAndNoObjects() {
        PhysicsWorld world = new PhysicsWorld();

        assertTrue(world.objects.isEmpty());
        assertVectorEquals(new Vector2(0, -9.81), world.gravity);
    }

    @Test
    void createsShapesWithMassAndInertiaBasedOnTheirDimensions() {
        PhysicsWorld world = new PhysicsWorld();
        world.addSquare(0, 0, 2, false);
        world.addCircle(0, 0, 2, false);

        assertEquals(4, world.objects.get(0).mass, DELTA);
        assertEquals(8.0 / 3.0, world.objects.get(0).momentOfInertia, DELTA);
        assertEquals(4 * Math.PI, world.objects.get(1).mass, DELTA);
        assertEquals(8 * Math.PI, world.objects.get(1).momentOfInertia, DELTA);
    }

    @Test
    void appliesGravityAndIntegratesDynamicObjects() {
        PhysicsWorld world = new PhysicsWorld();
        PhysicsObject object = new PhysicsObject(1, 10, false, 1, 1);
        object.velocity = new Vector2(2, 3);
        object.angularVelocity = 1;
        world.objects.add(object);

        world.step(0.5);

        assertVectorEquals(new Vector2(2, -1.905), object.velocity);
        assertVectorEquals(new Vector2(2, 9.0475), object.position);
        assertEquals(0.5, object.rotation, DELTA);
    }

    @Test
    void appliesGravityToEveryDynamicObject() {
        PhysicsWorld world = new PhysicsWorld();
        PhysicsObject first = new PhysicsObject(0, 0, false, 1, 1);
        PhysicsObject second = new PhysicsObject(5, 5, false, 1, 1);
        world.objects.add(first);
        world.objects.add(second);

        world.step(0.5);

        assertVectorEquals(new Vector2(0, -4.905), first.velocity);
        assertVectorEquals(new Vector2(0, -4.905), second.velocity);
        assertVectorEquals(new Vector2(0, -2.4525), first.position);
        assertVectorEquals(new Vector2(5, 2.5475), second.position);
    }

    @Test
    void leavesStaticObjectsUnaffectedByGravity() {
        PhysicsWorld world = new PhysicsWorld();
        PhysicsObject object = new PhysicsObject(1, 10, true, 1, 1);
        object.velocity = new Vector2(2, 3);
        world.objects.add(object);

        world.step(1);

        assertVectorEquals(new Vector2(2, 3), object.velocity);
        assertVectorEquals(new Vector2(1, 10), object.position);
    }

    @Test
    void removesObjectsBeyondTheWorldDistanceLimit() {
        PhysicsWorld world = new PhysicsWorld();
        PhysicsObject withinLimit = new PhysicsObject(
            PhysicsWorld.MAX_OBJECT_DISTANCE,
            0,
            true,
            1,
            1
        );
        PhysicsObject outsideLimit = new PhysicsObject(
            PhysicsWorld.MAX_OBJECT_DISTANCE + 1,
            0,
            true,
            1,
            1
        );
        PhysicsObject nonFinite = new PhysicsObject(Double.NaN, 0, true, 1, 1);
        world.objects.add(withinLimit);
        world.objects.add(outsideLimit);
        world.objects.add(nonFinite);

        world.step(0);

        assertEquals(1, world.objects.size());
        assertTrue(world.objects.contains(withinLimit));
    }

    @Test
    void removesObjectsThatMoveBeyondTheWorldDistanceLimit() {
        PhysicsWorld world = new PhysicsWorld();
        PhysicsObject object = new PhysicsObject(
            PhysicsWorld.MAX_OBJECT_DISTANCE - 1,
            0,
            false,
            1,
            1
        );
        object.velocity = new Vector2(2, 0);
        world.objects.add(object);

        world.step(1);

        assertTrue(world.objects.isEmpty());
    }

    @Test
    void usesConfiguredGravity() {
        PhysicsWorld world = new PhysicsWorld();
        world.gravity = new Vector2(4, 0);
        PhysicsObject object = new PhysicsObject(0, 0, false, 1, 1);
        world.objects.add(object);

        world.step(0.25);

        assertVectorEquals(new Vector2(1, 0), object.velocity);
        assertVectorEquals(new Vector2(0.25, 0), object.position);
    }

    @Test
    void reducesLinearAndAngularVelocityWithoutGravityWhenEnabled() {
        PhysicsWorld world = new PhysicsWorld();
        world.reduceVelocityEnabled = true;
        PhysicsObject object = new PhysicsObject(0, 0, false, 1, 1);
        object.velocity = new Vector2(4, 0);
        object.angularVelocity = 2;
        world.objects.add(object);

        world.step(0.5);

        double dampingFactor = Math.exp(-1);
        assertVectorEquals(new Vector2(4 * dampingFactor, 0), object.velocity);
        assertEquals(2 * dampingFactor, object.angularVelocity, DELTA);
        assertVectorEquals(new Vector2(2 * dampingFactor, 0), object.position);
    }

    @Test
    void iteratesConstraintsUsingGaussSeidelStyleLoop() {
        PhysicsWorld world = new PhysicsWorld();
        world.constraintIterations = 5;
        TrackingConstraint constraint = new TrackingConstraint();
        world.addConstraint(constraint);

        world.step(0.1);

        assertEquals(1, constraint.updateCalls);
        assertEquals(5, constraint.solveCalls);
    }

    @Test
    void createsRevoluteConstraintAtSeparateWorldAnchors() {
        PhysicsObject first = new PhysicsObject(0, 0, false, 1, 1);
        PhysicsObject second = new PhysicsObject(2, 0, false, 1, 1);
        RevoluteConstraint constraint = new RevoluteConstraint(
            first,
            second,
            new Vector2(1, 0),
            new Vector2(2, 1)
        );

        assertVectorEquals(new Vector2(1, 0), first.localToWorld(constraint.localA));
        assertVectorEquals(new Vector2(2, 1), second.localToWorld(constraint.localB));
    }

    private static void assertVectorEquals(Vector2 expected, Vector2 actual) {
        assertEquals(expected.x, actual.x, DELTA, "X component mismatch");
        assertEquals(expected.y, actual.y, DELTA, "Y component mismatch");
    }

    private static class TrackingConstraint extends Constraint {
        private int updateCalls = 0;
        private int solveCalls = 0;

        private TrackingConstraint() {
            super(null, null);
        }

        @Override
        public void update(Object settings) {
            updateCalls++;
        }

        @Override
        public void solve(double dt, Object settings) {
            solveCalls++;
        }
    }
}
