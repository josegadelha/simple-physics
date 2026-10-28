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

    public List<Vector2> getNormals() {
        List<Vector2> normals = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            Vector2 p1 = vertices.get(i);
            Vector2 p2 = vertices.get((i + 1) % vertices.size()); // O módulo (%) conecta o último vértice ao primeiro

            Vector2 edge = p2.subtract(p1);
            // Use the method that already exists in your Vector2 to find the perpendicular, and the new method to normalize it.
            Vector2 normal = edge.rotate90CW().normalize();
            normals.add(normal);
        }
        return normals;
    }
}
