
package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.physics.collision.CollisionData;

public class PhysicsWorldCollisionTest {

    public static void main(String[] args) {

        PhysicsWorld world = new PhysicsWorld();

        // Desativa a gravidade para deixar o teste previsível.
        world.gravity = new br.edu.ufersa.simplephysics.math.Vector2(0, 0);

        // Dois círculos dinâmicos com raio 1.
        world.addCircle(0, 0, 1, false);
        world.addCircle(1.5, 0, 1, false);

        // Atualiza o mundo e detecta as colisões.
        world.step(0.016);

        System.out.println("Quantidade de objetos: " + world.objects.size());
        System.out.println("Colisões detectadas: " + world.collisions.size());

        if (!world.collisions.isEmpty()) {

            CollisionData collision = world.collisions.get(0);

            System.out.println(
                "Penetração: " + collision.penetration
            );

            System.out.println(
                "Normal: (" + collision.normal.x
                + ", " + collision.normal.y + ")"
            );

        } else {
            System.out.println("ERRO: a colisão não foi detectada.");
        }

        // Separamos os círculos para testar novamente.
        world.objects.get(1).position =
            new br.edu.ufersa.simplephysics.math.Vector2(5, 0);

        world.detectCollisions();

        System.out.println(
            "Colisões após separar os círculos: "
            + world.collisions.size()
        );
    }
}
