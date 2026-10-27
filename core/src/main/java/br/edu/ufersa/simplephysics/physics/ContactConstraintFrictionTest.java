
package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.math.Vector2;

public class ContactConstraintFrictionTest {

    private static void executarTeste(double friction) {

        PhysicsWorld world = new PhysicsWorld();

        world.debugContactConstraints = true;

        // Desativa a gravidade para isolar o efeito da colisão.
        world.gravity = new Vector2(0, 0);

        // Cria um obstáculo estático.
        world.addCircle(0, 0, 1, true);

        // Cria um círculo dinâmico que atingirá o obstáculo.
        world.addCircle(-2.5, 1.2, 1, false);

        PhysicsObject movingCircle = world.objects.get(1);

        // Define o atrito dos dois corpos.
        world.objects.get(0).friction = friction;
        movingCircle.friction = friction;

        // Movimento inicial diagonal.
        movingCircle.velocity = new Vector2(2, -2);

        System.out.printf("%n=== TESTE COM ATRITO %.1f ===%n", friction);

        for (int i = 1; i <= 100; i++) {

            world.step(0.016);

            if (i % 10 == 0) {
                System.out.printf(
                    "Quadro %d | X=%.4f, Y=%.4f"
                        + " | VX=%.4f, VY=%.4f"
                        + " | Vel. angular=%.4f"
                        + " | Colisoes=%d%n",
                    i,
                    movingCircle.position.x,
                    movingCircle.position.y,
                    movingCircle.velocity.x,
                    movingCircle.velocity.y,
                    movingCircle.angularVelocity,
                    world.collisions.size()
                );
            }
        }

        System.out.printf(
            "Resultado final | VX=%.4f, VY=%.4f"
                + " | Vel. angular=%.4f%n",
            movingCircle.velocity.x,
            movingCircle.velocity.y,
            movingCircle.angularVelocity
        );
    }

    
   
        public static void main(String[] args) {
        PhysicsWorld world = new PhysicsWorld();
        world.debugContactConstraints = true;
        world.gravity = new Vector2(0, -9.81);

        // Círculo estático servindo como obstáculo.
        world.addCircle(0, 0, 1, true);

        // Círculo dinâmico próximo ao contato.
        world.addCircle(0, 2.1, 1, false);

        PhysicsObject movingCircle = world.objects.get(1);
        movingCircle.friction = 0.3;
        world.objects.get(0).friction = 0.3;

        for (int i = 1; i <= 30; i++) {
            world.step(0.016);

            System.out.printf(
                "Quadro %d | Y=%.6f | VY=%.6f | Colisoes=%d%n",
                i,
                movingCircle.position.y,
                movingCircle.velocity.y,
                world.collisions.size()
            );
        }
    }

}
