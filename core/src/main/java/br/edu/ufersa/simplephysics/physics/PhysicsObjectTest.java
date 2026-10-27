
package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.physics.shape.CircleShape;
import br.edu.ufersa.simplephysics.math.Vector2;

public class PhysicsObjectTest {

    public static void main(String[] args) {

        testar("Ponto de contato com infinito é rejeitado", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 2, 4, new CircleShape(1)
            );

            objeto.applyImpulse(
                new Vector2(0, 4),
                new Vector2(Double.POSITIVE_INFINITY, 0)
            );
        });

        testar("Ponto de contato com NaN é rejeitado", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 2, 4, new CircleShape(1)
            );

            objeto.applyImpulse(
                new Vector2(0, 4),
                new Vector2(Double.NaN, 0)
            );
        });

        testar("Impulso com infinito é rejeitado", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 2, 4, new CircleShape(1)
            );

            objeto.applyImpulse(
                new Vector2(Double.POSITIVE_INFINITY, 0),
                new Vector2(0, 0)
            );
        });

        testar("Impulso com NaN é rejeitado", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 2, 4, new CircleShape(1)
            );

            objeto.applyImpulse(
                new Vector2(Double.NaN, 0),
                new Vector2(0, 0)
            );
        });

        testar("Ponto de contato nulo é rejeitado", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 2, 4, new CircleShape(1)
            );

            objeto.applyImpulse(
                new Vector2(0, 4),
                null
            );
        });

        testar("Impulso nulo é rejeitado", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 2, 4, new CircleShape(1)
            );

            objeto.applyImpulse(
                null,
                new Vector2(0, 0)
            );
        });

        testar("Impulso negativo atualiza velocidade corretamente", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 2, 4, new CircleShape(1)
            );

            objeto.applyImpulse(
                new Vector2(-6, 0),
                new Vector2(0, 0)
            );

            if (Math.abs(objeto.velocity.x - (-3.0)) > 1e-9
                || Math.abs(objeto.velocity.y) > 1e-9) {
                throw new AssertionError(
                    "Velocidade incorreta para impulso negativo"
                );
            }
        });

        testar("Impulso no centro não gera rotação", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 2, 4, new CircleShape(1)
            );

            objeto.applyImpulse(
                new Vector2(0, 4),
                new Vector2(0, 0)
            );

            if (Math.abs(objeto.angularVelocity) > 1e-9) {
                throw new AssertionError(
                    "O impulso no centro gerou rotação"
                );
            }
        });

        testar("Impulso não altera objeto estático", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, true, 1, 1, new CircleShape(1)
            );

            objeto.applyImpulse(
                new Vector2(0, 4),
                new Vector2(1, 0)
            );

            if (Math.abs(objeto.velocity.x) > 1e-9
                || Math.abs(objeto.velocity.y) > 1e-9
                || Math.abs(objeto.angularVelocity) > 1e-9) {
                throw new AssertionError(
                    "O impulso alterou a velocidade do objeto estático"
                );
            }
        });

        testar("Impulso fora do centro altera velocidade angular", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 2, 4, new CircleShape(1)
            );

            objeto.applyImpulse(
                new Vector2(0, 4),
                new Vector2(1, 0)
            );

            if (Math.abs(objeto.angularVelocity - 1.0) > 1e-9) {
                throw new AssertionError("Velocidade angular incorreta");
            }
        });


        testar("Impulso altera velocidade linear", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 2, 1, new CircleShape(1)
            );

            objeto.applyImpulse(
                new Vector2(4, 0),
                new Vector2(0, 0)
            );

            if (Math.abs(objeto.velocity.x - 2.0) > 1e-9
                || Math.abs(objeto.velocity.y) > 1e-9) {
                throw new AssertionError("Velocidade linear incorreta");
            }
        });


        testar("Tempo NaN", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 1, 1, new CircleShape(1)
            );

            objeto.step(Double.NaN);
        });

        testar("Tempo infinito", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 1, 1, new CircleShape(1)
            );

            objeto.step(Double.POSITIVE_INFINITY);
        });

        testar("Tempo negativo", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 1, 1, new CircleShape(1)
            );

            objeto.step(-1);
        });

        testar("Tempo zero", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 1, 1, new CircleShape(1)
            );

            objeto.velocity = new Vector2(2, 3);
            objeto.step(0);

            if (objeto.position.x != 0 || objeto.position.y != 0) {
                throw new AssertionError("A posição mudou com dt zero");
            }
        });


        testar("Atualização da posição", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 1, 1, new CircleShape(1)
            );

            objeto.velocity = new Vector2(2, 3);
            objeto.step(0.5);

            if (Math.abs(objeto.position.x - 1.0) > 1e-9
                    || Math.abs(objeto.position.y - 1.5) > 1e-9) {
                throw new AssertionError("Posição incorreta");
            }
        });

        testar("Atualização da rotação", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, false, 1, 1, new CircleShape(1)
            );

            objeto.angularVelocity = 2;
            objeto.step(0.5);

            if (Math.abs(objeto.rotation - 1.0) > 1e-9) {
                throw new AssertionError("Rotação incorreta");
            }
        });

        testar("Objeto estático permanece imóvel", () -> {
            PhysicsObject objeto = new PhysicsObject(
                0, 0, true, 1, 1, new CircleShape(1)
            );

            objeto.velocity = new Vector2(2, 3);
            objeto.angularVelocity = 2;
            objeto.step(0.5);

            if (objeto.position.x != 0 || objeto.position.y != 0
                    || objeto.rotation != 0) {
                throw new AssertionError("Objeto estático se moveu");
            }
        });
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


