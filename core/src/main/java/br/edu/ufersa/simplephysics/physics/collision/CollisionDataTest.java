
package br.edu.ufersa.simplephysics.physics.collision;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.PhysicsObject;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;

public class CollisionDataTest {

    public static void main(String[] args) {

        PhysicsObject bodyA = new PhysicsObject(
            0, 0, false, 1, 1,
            new CircleShape(1)
        );

        PhysicsObject bodyB = new PhysicsObject(
            1, 0, false, 1, 1,
            new CircleShape(1)
        );

        System.out.println("=== Testes de CollisionData ===");

        // Teste 1: dados válidos.
        testar("Dados válidos", () ->
            new CollisionData(
                bodyA, bodyB,
                new Vector2(0.5, 0),
                new Vector2(2, 0),
                0.5
            )
        );

        // Teste 2: corpo A nulo.
        testar("Corpo A nulo", () ->
            new CollisionData(
                null, bodyB,
                new Vector2(0, 0),
                new Vector2(1, 0),
                0.5
            )
        );

        // Teste 3: mesmo corpo nas duas posições.
        testar("Corpos iguais", () ->
            new CollisionData(
                bodyA, bodyA,
                new Vector2(0, 0),
                new Vector2(1, 0),
                0.5
            )
        );

        // Teste 4: ponto de contato nulo.
        testar("Ponto nulo", () ->
            new CollisionData(
                bodyA, bodyB,
                null, new Vector2(1, 0), 0.5
            )
        );

        // Teste 5: normal nula.
        testar("Normal nula", () ->
            new CollisionData(
                bodyA, bodyB,
                new Vector2(0, 0), null, 0.5
            )
        );

        // Teste 6: normal de comprimento zero.
        testar("Normal zero", () ->
            new CollisionData(
                bodyA, bodyB,
                new Vector2(0, 0), new Vector2(0, 0), 0.5
            )
        );

        // Teste 7: penetração negativa.
        testar("Penetração negativa", () ->
            new CollisionData(
                bodyA, bodyB,
                new Vector2(0, 0), new Vector2(1, 0), -0.5
            )
        );

        // Teste 8: penetração NaN.
        testar("Penetração NaN", () ->
            new CollisionData(
                bodyA, bodyB,
                new Vector2(0, 0), new Vector2(1, 0),
                Double.NaN
            )
        );
    }

    private static void testar(String nome, Runnable teste) {
        try {
            teste.run();
            System.out.println(nome + ": aceito.");
        } catch (IllegalArgumentException e) {
            System.out.println(nome + ": rejeitado corretamente.");
        }
    }
}
