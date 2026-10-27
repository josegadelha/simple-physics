package br.edu.ufersa.simplephysics.physics.shape;

import br.edu.ufersa.simplephysics.math.Vector2;

public class ConvexPolygonShapeTest {

    public static void main(String[] args) {
        System.out.println("Teste iniciado!");

        testar("Triângulo válido", () ->
            new ConvexPolygonShape(
                new Vector2(0, 0),
                new Vector2(1, 0),
                new Vector2(0, 1)
            )
        );

        testar("Array nulo", () ->
            new ConvexPolygonShape((Vector2[]) null)
        );

        testar("Menos de três vértices", () ->
            new ConvexPolygonShape(
                new Vector2(0, 0),
                new Vector2(1, 0)
            )
        );

        testar("Vértice nulo", () ->
            new ConvexPolygonShape(
                new Vector2(0, 0),
                null,
                new Vector2(0, 1)
            )
        );

        testar("Coordenada NaN", () ->
            new ConvexPolygonShape(
                new Vector2(0, 0),
                new Vector2(Double.NaN, 0),
                new Vector2(0, 1)
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