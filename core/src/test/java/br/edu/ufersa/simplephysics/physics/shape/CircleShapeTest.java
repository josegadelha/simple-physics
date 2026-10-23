package br.edu.ufersa.simplephysics.physics.shape;

import br.edu.ufersa.simplephysics.math.Vector2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CircleShapeTest {
    @Test
    void storesRadiusAndDefaultsOffsetToOrigin() {
        CircleShape circle = new CircleShape(5);

        assertEquals(5, circle.radius);
        assertEquals(0, circle.offset.x);
        assertEquals(0, circle.offset.y);
    }

    @Test
    void storesProvidedOffset() {
        CircleShape circle = new CircleShape(3, new Vector2(2, -1));

        assertEquals(2, circle.offset.x);
        assertEquals(-1, circle.offset.y);
    }

    @Test
    void rejectsInvalidRadius() {
        assertThrows(IllegalArgumentException.class, () -> new CircleShape(0));
        assertThrows(IllegalArgumentException.class, () -> new CircleShape(-1));
        assertThrows(IllegalArgumentException.class, () -> new CircleShape(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> new CircleShape(Double.POSITIVE_INFINITY));
    }
}
