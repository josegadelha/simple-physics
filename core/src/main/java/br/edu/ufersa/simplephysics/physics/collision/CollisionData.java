
package br.edu.ufersa.simplephysics.physics.collision;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.PhysicsObject;

/**
 * Armazena os dados geométricos de uma colisão
 * entre dois objetos físicos.
 */
public class CollisionData {

    public final PhysicsObject bodyA;
    public final PhysicsObject bodyB;

    // Ponto de contato no sistema de coordenadas do mundo.
    public final Vector2 worldPoint;

    // Vetor unitário que aponta do objeto A para o objeto B.
    public final Vector2 normal;

    // Quanto os dois objetos estão sobrepostos.
    public final double penetration;

    public CollisionData(
            PhysicsObject bodyA,
            PhysicsObject bodyB,
            Vector2 worldPoint,
            Vector2 normal,
            double penetration) {

        if (bodyA == null || bodyB == null || bodyA == bodyB) {
            throw new IllegalArgumentException(
                "Os corpos devem ser válidos e distintos."
            );
        }

        if (worldPoint == null
                || !Double.isFinite(worldPoint.x)
                || !Double.isFinite(worldPoint.y)) {
            throw new IllegalArgumentException(
                "O ponto de contato deve ter coordenadas finitas."
            );
        }

        if (normal == null
                || !Double.isFinite(normal.x)
                || !Double.isFinite(normal.y)) {
            throw new IllegalArgumentException(
                "A normal deve ter coordenadas finitas."
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

        this.bodyA = bodyA;
        this.bodyB = bodyB;
        this.worldPoint = worldPoint;
        this.normal = normal.scale(1.0 / normalLength);
        this.penetration = penetration;
    }
}
