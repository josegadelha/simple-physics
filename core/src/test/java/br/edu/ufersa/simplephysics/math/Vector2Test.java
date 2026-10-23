package br.edu.ufersa.simplephysics.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Vector2Test {
    private static final double DELTA = 1e-9;

    @Test
    void addsVectors() {
        assertVectorEquals(new Vector2(6, 2), new Vector2(2, -3).add(new Vector2(4, 5)));
    }

    @Test
    void subtractsVectors() {
        assertVectorEquals(new Vector2(-2, -8), new Vector2(2, -3).subtract(new Vector2(4, 5)));
    }

    @Test
    void scalesVectorByScalar() {
        assertVectorEquals(new Vector2(-6, 9), new Vector2(2, -3).scale(-3));
    }

    @Test
    void calculatesDotProduct() {
        assertEquals(-7, new Vector2(2, 3).dotProduct(new Vector2(4, -5)), DELTA);
    }

    @Test
    void calculatesCrossProduct() {
        assertEquals(-22, new Vector2(2, 3).crossProduct(new Vector2(4, -5)), DELTA);
    }

    @Test
    void calculatesLength() {
        assertEquals(5, new Vector2(3, 4).length(), DELTA);
    }

    @Test
    void rotatesVector90DegreesClockwise() {
        assertVectorEquals(new Vector2(4, -3), new Vector2(3, 4).rotate90CW());
    }

    @Test
    void computesVelocityFromAngularVelocityAndRadius() {
        assertVectorEquals(
            new Vector2(-6, 4),
            new Vector2(2, 3).crossProductScalarVector(2)
        );
    }

    private static void assertVectorEquals(Vector2 expected, Vector2 actual) {
        assertEquals(expected.x, actual.x, DELTA, "X component mismatch");
        assertEquals(expected.y, actual.y, DELTA, "Y component mismatch");
    }
}
