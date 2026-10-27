
package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.math.Vector2;

public class ContactConstraintObliqueTest {

    public static void main(String[] args) {

        PhysicsWorld world = new PhysicsWorld();

        // Desativa a gravidade para isolar a colisão.
        world.gravity = new Vector2(0, 0);

        // Círculo estático usado como obstáculo.
        world.addCircle(0, 0, 1, true);

        // Círculo dinâmico que se aproxima na diagonal.
        world.addCircle(-2.5, 1.2, 1, false);

        PhysicsObject movingCircle = world.objects.get(1);

        movingCircle.velocity = new Vector2(2, -0.5);

        System.out.println("=== TESTE DE COLISAO OBLIQUA ===");
        System.out.printf(
            "Posicao inicial: X=%.4f, Y=%.4f%n",
            movingCircle.position.x,
            movingCircle.position.y
        );
        System.out.printf(
            "Velocidade inicial: VX=%.4f, VY=%.4f%n",
            movingCircle.velocity.x,
            movingCircle.velocity.y
        );

        for (int i = 1; i <= 100; i++) {

            world.step(0.016);

            if (i % 10 == 0) {
                System.out.printf(
                    "Quadro %d | X=%.4f, Y=%.4f"
                        + " | VX=%.4f, VY=%.4f"
                        + " | Colisoes: %d%n",
                    i,
                    movingCircle.position.x,
                    movingCircle.position.y,
                    movingCircle.velocity.x,
                    movingCircle.velocity.y,
                    world.collisions.size()
                );
            }
        }

        System.out.println("\n=== RESULTADO FINAL ===");
        System.out.printf(
            "Posicao: X=%.4f, Y=%.4f%n",
            movingCircle.position.x,
            movingCircle.position.y
        );
        System.out.printf(
            "Velocidade: VX=%.4f, VY=%.4f%n",
            movingCircle.velocity.x,
            movingCircle.velocity.y
        );
    }
}
