
package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.physics.collision.CollisionData;

public class PhysicsWorldCollisionTest {


    public static void main(String[] args) {

        PhysicsWorld world = new PhysicsWorld();

        // Desativa a gravidade para deixar o teste previsível.
        world.gravity = new br.edu.ufersa.simplephysics.math.Vector2(0, 0);

        // Quadrado grande, centralizado na origem.
        PhysicsObject polygonGrande = new PhysicsObject(
            0, 0, false, 1, 1,
            new br.edu.ufersa.simplephysics.physics.shape.ConvexPolygonShape(
                new br.edu.ufersa.simplephysics.math.Vector2(-5, -5),
                new br.edu.ufersa.simplephysics.math.Vector2(5, -5),
                new br.edu.ufersa.simplephysics.math.Vector2(5, 5),
                new br.edu.ufersa.simplephysics.math.Vector2(-5, 5)
            )
        );

        // Quadrado pequeno, inicialmente dentro do grande.
        PhysicsObject polygonPequeno = new PhysicsObject(
            2, 0, false, 1, 1,
            new br.edu.ufersa.simplephysics.physics.shape.ConvexPolygonShape(
                new br.edu.ufersa.simplephysics.math.Vector2(-1, -1),
                new br.edu.ufersa.simplephysics.math.Vector2(1, -1),
                new br.edu.ufersa.simplephysics.math.Vector2(1, 1),
                new br.edu.ufersa.simplephysics.math.Vector2(-1, 1)
            )
        );

        world.objects.add(polygonGrande);
        world.objects.add(polygonPequeno);

        System.out.println("TESTE 26 - Resolução física de polígonos contidos");
        System.out.println(
            "Posição inicial do grande: " + polygonGrande.position
        );
        System.out.println(
            "Posição inicial do pequeno: " + polygonPequeno.position
        );

        // Executa vários passos para observar a correção de posição.
        for (int i = 0; i < 60; i++) {
            world.step(0.016);
        }

        System.out.println(
            "Posição final do grande: " + polygonGrande.position
        );
        System.out.println(
            "Posição final do pequeno: " + polygonPequeno.position
        );

        System.out.println(
            "Colisões finais detectadas: " + world.collisions.size()
        );

        if (world.collisions.isEmpty()) {
            System.out.println(
                "Não há colisões no último passo da simulação."
            );
        } else {
            CollisionData collision = world.collisions.get(0);

            System.out.println(
                "Penetração final: " + collision.penetration
            );
            System.out.println(
                "Normal final: (" + collision.normal.x
                    + ", " + collision.normal.y + ")"
            );
        }
    }

}
