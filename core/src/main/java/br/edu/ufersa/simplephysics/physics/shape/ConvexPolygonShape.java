package br.edu.ufersa.simplephysics.physics.shape;

import br.edu.ufersa.simplephysics.math.Vector2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConvexPolygonShape implements PhysicsShape {
    public final List<Vector2> vertices;

    public ConvexPolygonShape(Vector2... vertices) {
        if (vertices.length < 3) {
            throw new IllegalArgumentException("A polygon must have at least three vertices");
        }

        this.vertices = new ArrayList<>(Arrays.asList(vertices));
    }
}
