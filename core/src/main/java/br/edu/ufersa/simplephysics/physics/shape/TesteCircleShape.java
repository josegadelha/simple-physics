package br.edu.ufersa.simplephysics.physics.shape;


import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;

public class TesteCircleShape {

    public static void main(String[] args) {

        // Teste 1: círculo válido.
        testar("Raio válido", () ->
            new CircleShape(1.0)
        );

        // Teste 2: raio zero.
        testar("Raio zero", () ->
            new CircleShape(0)
        );

        // Teste 3: raio negativo.
        testar("Raio negativo", () ->
            new CircleShape(-1)
        );

        // Teste 4: deslocamento nulo.
        testar("Offset nulo", () ->
            new CircleShape(1, null)
        );

        // Teste 5: coordenada inválida.
        testar("Offset com NaN", () ->
            new CircleShape(1, new Vector2(Double.NaN, 0))
        );
    }

    private static void testar(String nome, Runnable teste) {
        try {
            teste.run();
            System.out.println(nome + ": objeto criado.");
        } catch (IllegalArgumentException e) {
            System.out.println(nome + ": rejeitado corretamente.");
        }
    }
}

