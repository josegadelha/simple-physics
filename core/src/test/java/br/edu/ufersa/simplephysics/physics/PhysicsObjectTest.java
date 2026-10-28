package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhysicsObjectTest {
    private static final double DELTA = 1e-9;

    @Test
    void initializesDynamicObjectAndCopiesItsShapes() {
        CircleShape shape = new CircleShape(2);

        PhysicsObject object = new PhysicsObject(3, 4, false, 2, 5, shape);

        assertVectorEquals(new Vector2(3, 4), object.position);
        assertVectorEquals(new Vector2(0, 0), object.velocity);
        assertEquals(0, object.rotation, DELTA);
        assertEquals(0, object.angularVelocity, DELTA);
        assertFalse(object.isStatic);
        assertEquals(2, object.mass, DELTA);
        assertEquals(5, object.momentOfInertia, DELTA);
        assertEquals(1, object.shapes.size());
        assertSame(shape, object.shapes.get(0));
    }

    @Test
    void initializesStaticObjectWithInfiniteMassAndInertia() {
        PhysicsObject object = new PhysicsObject(1, 2, true, 1, 1);

        assertTrue(object.isStatic);
        assertEquals(Double.POSITIVE_INFINITY, object.mass);
        assertEquals(Double.POSITIVE_INFINITY, object.momentOfInertia);
    }

    @Test
    void rejectsInvalidDynamicMassAndMomentOfInertia() {
        assertThrows(IllegalArgumentException.class,
            () -> new PhysicsObject(0, 0, false, 0, 1));
        assertThrows(IllegalArgumentException.class,
            () -> new PhysicsObject(0, 0, false, -1, 1));
        assertThrows(IllegalArgumentException.class,
            () -> new PhysicsObject(0, 0, false, Double.NaN, 1));
        assertThrows(IllegalArgumentException.class,
            () -> new PhysicsObject(0, 0, false, Double.POSITIVE_INFINITY, 1));
        assertThrows(IllegalArgumentException.class,
            () -> new PhysicsObject(0, 0, false, 1, 0));
        assertThrows(IllegalArgumentException.class,
            () -> new PhysicsObject(0, 0, false, 1, -1));
        assertThrows(IllegalArgumentException.class,
            () -> new PhysicsObject(0, 0, false, 1, Double.NaN));
        assertThrows(IllegalArgumentException.class,
            () -> new PhysicsObject(0, 0, false, 1, Double.POSITIVE_INFINITY));
    }

    @Test
    void integratesDynamicPositionAndRotation() {
        PhysicsObject object = new PhysicsObject(1, 2, false, 1, 1);
        object.velocity = new Vector2(3, -4);
        object.angularVelocity = 2;

        object.step(0.5);

        assertVectorEquals(new Vector2(2.5, 0), object.position);
        assertEquals(1, object.rotation, DELTA);
    }

    @Test
    void doesNotMoveOrRotateStaticObject() {
        PhysicsObject object = new PhysicsObject(1, 2, true, 1, 1);
        object.velocity = new Vector2(3, -4);
        object.angularVelocity = 2;

        object.step(0.5);

        assertVectorEquals(new Vector2(1, 2), object.position);
        assertEquals(0, object.rotation, DELTA);
    }

    @Test
    void convertsPointsBetweenLocalAndWorldCoordinates() {
        PhysicsObject object = new PhysicsObject(10, 20, false, 1, 1);
        object.rotation = Math.PI / 2;
        Vector2 localPoint = new Vector2(2, 0);

        Vector2 worldPoint = object.localToWorld(localPoint);

        assertVectorEquals(new Vector2(10, 22), worldPoint);
        assertVectorEquals(localPoint, object.worldToLocal(worldPoint));
    }

    @Test
    void appliesImpulseToLinearAndAngularVelocity() {
        PhysicsObject object = new PhysicsObject(0, 0, false, 2, 4);

        object.applyImpulse(new Vector2(0, 6), new Vector2(2, 0));

        assertVectorEquals(new Vector2(0, 3), object.velocity);
        assertEquals(3, object.angularVelocity, DELTA);
    }

    @Test
    void doesNotApplyImpulseToStaticObject() {
        PhysicsObject object = new PhysicsObject(0, 0, true, 1, 1);

        object.applyImpulse(new Vector2(0, 6), new Vector2(2, 0));

        assertVectorEquals(new Vector2(0, 0), object.velocity);
        assertEquals(0, object.angularVelocity, DELTA);
    }

    private static void assertVectorEquals(Vector2 expected, Vector2 actual) {
        assertEquals(expected.x, actual.x, DELTA, "X component mismatch");
        assertEquals(expected.y, actual.y, DELTA, "Y component mismatch");
    }
}
