package br.edu.ufersa.simplephysics.physics.shape;

import br.edu.ufersa.simplephysics.math.Vector2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConvexPolygonShapeTest {
    @Test
    void storesVertices() {
        Vector2 first = new Vector2(0, 0);
        Vector2 second = new Vector2(2, 0);
        Vector2 third = new Vector2(1, 2);

        ConvexPolygonShape polygon = new ConvexPolygonShape(first, second, third);

        assertEquals(3, polygon.vertices.size());
        assertSame(first, polygon.vertices.get(0));
        assertSame(second, polygon.vertices.get(1));
        assertSame(third, polygon.vertices.get(2));
    }

    @Test
    void rejectsFewerThanThreeVertices() {
        assertThrows(IllegalArgumentException.class,
            () -> new ConvexPolygonShape(new Vector2(0, 0), new Vector2(1, 1)));
        assertThrows(IllegalArgumentException.class,
            () -> new ConvexPolygonShape(new Vector2(0, 0)));
        assertThrows(IllegalArgumentException.class,
            ConvexPolygonShape::new);
    }
}
