
package br.edu.ufersa.simplephysics.physics.collision;
import java.util.ArrayList;
import java.util.List;
import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.PhysicsObject;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;
import br.edu.ufersa.simplephysics.physics.shape.ConvexPolygonShape;
import br.edu.ufersa.simplephysics.physics.shape.PhysicsShape;

public final class CollisionHelper {

    private static final double EPSILON = 1e-9;

    // Evita criar objetos desta classe.
    private CollisionHelper() {
    }

    /*
     * Verifica se dois objetos possuem formas colidindo.
     * Retorna os dados da primeira colisão encontrada.
     * Retorna null se não houver colisão.
     */
    public static CollisionData checkCollision(
        PhysicsObject bodyA,
        PhysicsObject bodyB) {

        if (bodyA == null || bodyB == null) {
            throw new IllegalArgumentException(
                "Os objetos físicos não podem ser nulos."
            );
        }

        // Dois objetos estáticos não precisam de resolução de colisão.
        if (bodyA.isStatic && bodyB.isStatic) {
            return null;
        }

        for (PhysicsShape shapeA : bodyA.shapes) {
            for (PhysicsShape shapeB : bodyB.shapes) {

                CollisionData collision = null;

                if (shapeA instanceof CircleShape
                    && shapeB instanceof CircleShape) {

                    collision = circleToCircle(
                        bodyA,
                        (CircleShape) shapeA,
                        bodyB,
                        (CircleShape) shapeB
                    );

                } else if (shapeA instanceof CircleShape
                    && shapeB instanceof ConvexPolygonShape) {

                    collision = circleToPolygon(
                        bodyA,
                        (CircleShape) shapeA,
                        bodyB,
                        (ConvexPolygonShape) shapeB
                    );

                } else if (shapeA instanceof ConvexPolygonShape
                    && shapeB instanceof CircleShape) {

                    // Detectamos círculo contra polígono e depois
                    // invertemos a normal para manter A -> B.
                    CollisionData reversed = circleToPolygon(
                        bodyB,
                        (CircleShape) shapeB,
                        bodyA,
                        (ConvexPolygonShape) shapeA
                    );

                    if (reversed != null) {
                        collision = new CollisionData(
                            bodyA,
                            bodyB,
                            reversed.worldPoint,
                            reversed.normal.scale(-1),
                            reversed.penetration
                        );
                    }
                }

                if (collision != null) {
                    return collision;
                }
            }
        }

        return null;
    }

    /*
     * Detecta colisão entre dois círculos.
     */
    private static CollisionData circleToCircle(
        PhysicsObject bodyA,
        CircleShape circleA,
        PhysicsObject bodyB,
        CircleShape circleB) {

        Vector2 centerA = bodyA.localToWorld(circleA.offset);
        Vector2 centerB = bodyB.localToWorld(circleB.offset);

        Vector2 direction = centerB.subtract(centerA);

        double distance = direction.length();
        double totalRadius = circleA.radius + circleB.radius;

        if (distance > totalRadius) {
            return null;
        }

        double penetration = totalRadius - distance;

        Vector2 normal;

        if (distance > EPSILON) {
            normal = direction.scale(1.0 / distance);
        } else {
            // Evita normalizar um vetor de comprimento zero.
            normal = new Vector2(1, 0);
        }

        Vector2 pointA = centerA.add(
            normal.scale(circleA.radius)
        );

        Vector2 pointB = centerB.subtract(
            normal.scale(circleB.radius)
        );

        Vector2 worldPoint = pointA.add(pointB).scale(0.5);

        return new CollisionData(
            bodyA,
            bodyB,
            worldPoint,
            normal,
            penetration
        );
    }

    /*
     * Detecta colisão entre um círculo e um polígono convexo.
     * A normal aponta do círculo para o polígono.
     */
    private static CollisionData circleToPolygon(
        PhysicsObject circleBody,
        CircleShape circle,
        PhysicsObject polygonBody,
        ConvexPolygonShape polygon) {

        Vector2 circleCenter =
            circleBody.localToWorld(circle.offset);

        int count = polygon.vertices.size();

        Vector2 closestPoint = null;
        Vector2 closestEdgeNormal = null;
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        // Procura o ponto mais próximo em todas as arestas.
        for (int i = 0; i < count; i++) {

            Vector2 localA = polygon.vertices.get(i);
            Vector2 localB =
                polygon.vertices.get((i + 1) % count);

            Vector2 a = polygonBody.localToWorld(localA);
            Vector2 b = polygonBody.localToWorld(localB);

            Vector2 edge = b.subtract(a);
            double edgeLengthSquared = edge.dotProduct(edge);

            if (edgeLengthSquared <= EPSILON * EPSILON) {
                continue;
            }

            Vector2 fromA = circleCenter.subtract(a);

            double t = fromA.dotProduct(edge) / edgeLengthSquared;
            t = Math.max(0.0, Math.min(1.0, t));

            Vector2 point = a.add(edge.scale(t));
            Vector2 difference = point.subtract(circleCenter);
            double distanceSquared = difference.dotProduct(difference);

            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closestPoint = point;

                // Normal perpendicular à aresta, usada como
                // alternativa se o centro coincidir com o ponto.
                Vector2 edgeNormal = edge.rotate90CW();
                double edgeLength = edge.length();

                closestEdgeNormal =
                    edgeNormal.scale(1.0 / edgeLength);
            }
        }

        if (closestPoint == null) {
            return null;
        }

        double distance = Math.sqrt(minDistanceSquared);

        // Verifica se o centro do círculo está dentro do polígono.
        boolean inside = isPointInsidePolygon(
            circleCenter,
            polygonBody,
            polygon
        );

        // Fora do polígono, sem sobreposição.
        if (!inside && distance > circle.radius) {
            return null;
        }

        Vector2 normal;

        if (distance > EPSILON) {
            normal = closestPoint
                .subtract(circleCenter)
                .scale(1.0 / distance);
        } else {
            normal = closestEdgeNormal;
        }

        // Fora: raio - distância.
        // Dentro: raio + distância até a borda.
        double penetration = inside
            ? circle.radius + distance
            : circle.radius - distance;

        Vector2 circlePoint = circleCenter.add(
            normal.scale(circle.radius)
        );

        Vector2 worldPoint = circlePoint
            .add(closestPoint)
            .scale(0.5);

        return new CollisionData(
            circleBody,
            polygonBody,
            worldPoint,
            normal,
            Math.max(0.0, penetration)
        );
    }

    /*
     * Verifica se um ponto está dentro do polígono,
     * utilizando o método de cruzamento de raios.
     */
    private static boolean isPointInsidePolygon(
        Vector2 point,
        PhysicsObject body,
        ConvexPolygonShape polygon) {

        boolean inside = false;
        int count = polygon.vertices.size();

        for (int i = 0, j = count - 1; i < count; j = i++) {

            Vector2 a = body.localToWorld(
                polygon.vertices.get(i)
            );

            Vector2 b = body.localToWorld(
                polygon.vertices.get(j)
            );

            boolean crosses = (a.y > point.y) != (b.y > point.y);

            if (crosses) {
                double intersectionX =
                    (b.x - a.x) * (point.y - a.y)
                        / (b.y - a.y) + a.x;

                if (point.x < intersectionX) {
                    inside = !inside;
                }
            }
        }

        return inside;
    }

    private static List<Vector2> getWorldVertices(
        PhysicsObject body,
        ConvexPolygonShape polygon) {

        List<Vector2> worldVertices = new ArrayList<>();

        for (Vector2 vertex : polygon.vertices) {
            worldVertices.add(body.localToWorld(vertex));
        }

        return worldVertices;
    }

    private static double[] projectPolygon(
        List<Vector2> vertices,
        Vector2 axis) {

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (Vector2 vertex : vertices) {
            double projection = vertex.dotProduct(axis);

            min = Math.min(min, projection);
            max = Math.max(max, projection);
        }

        return new double[] { min, max };
    }

    private static boolean testPolygonAxes(
        List<Vector2> verticesFrom,
        List<Vector2> verticesA,
        List<Vector2> verticesB,
        double[] best) {

        for (int i = 0; i < verticesFrom.size(); i++) {
            Vector2 current = verticesFrom.get(i);
            Vector2 next = verticesFrom.get(
                (i + 1) % verticesFrom.size());

            Vector2 edge = next.subtract(current);
            double edgeLength = edge.length();

            // Ignora arestas de comprimento praticamente zero.
            if (edgeLength <= EPSILON) {
                continue;
            }

            // O eixo de teste é perpendicular à aresta.
            Vector2 axis = edge.rotate90CW().scale(1.0 / edgeLength);

            double[] projectionA = projectPolygon(verticesA, axis);
            double[] projectionB = projectPolygon(verticesB, axis);

            double overlap = Math.min(projectionA[1], projectionB[1])
                - Math.max(projectionA[0], projectionB[0]);

            // Se houver separação neste eixo, não existe colisão.
            if (overlap < -EPSILON) {
                return false;
            }

            // Pequenas diferenças numéricas são tratadas como contato.
            overlap = Math.max(0.0, overlap);

            // Guarda o eixo de menor sobreposição.
            if (overlap < best[0]) {
                best[0] = overlap;
                best[1] = axis.x;
                best[2] = axis.y;
            }
        }

        return true;
    }

}
