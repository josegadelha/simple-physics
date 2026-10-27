package br.edu.ufersa.simplephysics.physics;

public class ContactConstraintTwoDynamicTest {

    public static void main(String[] args) {

        PhysicsWorld world = new PhysicsWorld();

        // Desativa a gravidade para isolar o efeito da colisão.
        world.gravity = new br.edu.ufersa.simplephysics.math.Vector2(0, 0);

        // Cria dois círculos dinâmicos.
        world.addCircle(-1.5, 0, 1, false);
        world.addCircle(1.5, 0, 1, false);

        PhysicsObject circleA = world.objects.get(0);
        PhysicsObject circleB = world.objects.get(1);

        // Os círculos se movem um em direção ao outro.
        circleA.velocity =
            new br.edu.ufersa.simplephysics.math.Vector2(2, 0);

        circleB.velocity =
            new br.edu.ufersa.simplephysics.math.Vector2(-2, 0);

        System.out.println("=== TESTE COM DOIS CORPOS DINAMICOS ===");

        for (int i = 1; i <= 300; i++) {

            world.step(0.016);

            if (i % 50 == 0) {
                System.out.printf(
                    "Quadro %d | A: X=%.4f, VX=%.4f"
                        + " | B: X=%.4f, VX=%.4f"
                        + " | Colisoes: %d%n",
                    i,
                    circleA.position.x,
                    circleA.velocity.x,
                    circleB.position.x,
                    circleB.velocity.x,
                    world.collisions.size()
                );
            }
        }

        System.out.println("\n=== RESULTADO FINAL ===");
        System.out.printf(
            "Circulo A: X=%.4f, VX=%.4f%n",
            circleA.position.x,
            circleA.velocity.x
        );
        System.out.printf(
            "Circulo B: X=%.4f, VX=%.4f%n",
            circleB.position.x,
            circleB.velocity.x
        );
    }
}