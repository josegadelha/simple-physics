
package br.edu.ufersa.simplephysics.physics;

public class ContactConstraintTest {

    public static void main(String[] args) {

        PhysicsWorld world = new PhysicsWorld();

        // Mantém a gravidade padrão do mundo: (0, -9.81).

        // Círculo estático que representa o chão.
        world.addCircle(0, 0, 1, true);

        // Círculo dinâmico que começa acima do chão.
        world.addCircle(0, 3, 1, false);

        PhysicsObject movingCircle = world.objects.get(1);

        System.out.println("=== TESTE DE COLISAO COM GRAVIDADE ===");
        System.out.printf(
            "Posicao inicial Y: %.4f%n",
            movingCircle.position.y
        );
        System.out.printf(
            "Velocidade inicial Y: %.4f%n",
            movingCircle.velocity.y
        );
        System.out.println();

        // Simula 120 quadros, com intervalo de 16 ms por quadro.
        for (int i = 1; i <= 120; i++) {

            world.step(0.016);

            // Exibe o estado a cada 10 quadros.
            if (i % 10 == 0) {
                System.out.printf(
                    "Quadro %d | Y: %.4f | Velocidade Y: %.4f"
                        + " | Colisoes: %d%n",
                    i,
                    movingCircle.position.y,
                    movingCircle.velocity.y,
                    world.collisions.size()
                );
            }
        }

        System.out.println();
        System.out.println("=== RESULTADO FINAL ===");
        System.out.printf(
            "Posicao Y: %.4f%n",
            movingCircle.position.y
        );
        System.out.printf(
            "Velocidade Y: %.4f%n",
            movingCircle.velocity.y
        );
        System.out.println(
            "Colisoes no ultimo quadro: " + world.collisions.size()
        );
    }
}
