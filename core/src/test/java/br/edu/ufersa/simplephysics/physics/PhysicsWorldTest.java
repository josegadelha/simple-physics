package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.math.Vector2;
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
    void appliesGravityAndIntegratesDynamicObjects() {
        PhysicsWorld world = new PhysicsWorld();
        PhysicsObject object = new PhysicsObject(1, 10, false);
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
        PhysicsObject first = new PhysicsObject(0, 0, false);
        PhysicsObject second = new PhysicsObject(5, 5, false);
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
        PhysicsObject object = new PhysicsObject(1, 10, true);
        object.velocity = new Vector2(2, 3);
        world.objects.add(object);

        world.step(1);

        assertVectorEquals(new Vector2(2, 3), object.velocity);
        assertVectorEquals(new Vector2(1, 10), object.position);
    }

    @Test
    void usesConfiguredGravity() {
        PhysicsWorld world = new PhysicsWorld();
        world.gravity = new Vector2(4, 0);
        PhysicsObject object = new PhysicsObject(0, 0, false);
        world.objects.add(object);

        world.step(0.25);

        assertVectorEquals(new Vector2(1, 0), object.velocity);
        assertVectorEquals(new Vector2(0.25, 0), object.position);
    }

    private static void assertVectorEquals(Vector2 expected, Vector2 actual) {
        assertEquals(expected.x, actual.x, DELTA, "X component mismatch");
        assertEquals(expected.y, actual.y, DELTA, "Y component mismatch");
    }
}
