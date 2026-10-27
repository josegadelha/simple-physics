
package br.edu.ufersa.simplephysics.physics.collision;
import br.edu.ufersa.simplephysics.math.Vector2;

import br.edu.ufersa.simplephysics.physics.PhysicsObject;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;
import br.edu.ufersa.simplephysics.physics.shape.ConvexPolygonShape;

public class CollisionHelperTest {

    public static void main(String[] args) {

        // Teste 1: círculos sobrepostos.
        PhysicsObject a = new PhysicsObject(
            0, 0, false, 1, 1,
            new CircleShape(2)
        );

        PhysicsObject b = new PhysicsObject(
            3, 0, false, 1, 1,
            new CircleShape(2)
        );

        CollisionData collision =
            CollisionHelper.checkCollision(a, b);

        System.out.println("TESTE 1 - Círculos sobrepostos");

        if (collision != null) {
            System.out.println("Colisão detectada!");
            System.out.println(
                "Normal: (" + collision.normal.x
                    + ", " + collision.normal.y + ")"
            );
            System.out.println(
                "Penetração: " + collision.penetration
            );
            System.out.println(
                "Ponto de contato: ("
                    + collision.worldPoint.x + ", "
                    + collision.worldPoint.y + ")"
            );
        } else {
            System.out.println("ERRO: colisão não detectada.");
        }

        // Teste 2: círculos separados.
        PhysicsObject c = new PhysicsObject(
            0, 0, false, 1, 1,
            new CircleShape(1)
        );

        PhysicsObject d = new PhysicsObject(
            5, 0, false, 1, 1,
            new CircleShape(1)
        );

        CollisionData noCollision =
            CollisionHelper.checkCollision(c, d);

        System.out.println("\nTESTE 2 - Círculos separados");
        System.out.println(
            noCollision == null
                ? "Correto: não existe colisão."
                : "ERRO: colisão detectada indevidamente."
        );

        // Teste 3: círculos com centros coincidentes.
        PhysicsObject e = new PhysicsObject(
            0, 0, false, 1, 1,
            new CircleShape(1)
        );

        PhysicsObject f = new PhysicsObject(
            0, 0, false, 1, 1,
            new CircleShape(1)
        );

        CollisionData coincident =
            CollisionHelper.checkCollision(e, f);

        System.out.println("\nTESTE 3 - Centros coincidentes");
        System.out.println(
            coincident != null
                && Double.isFinite(coincident.normal.x)
                && Double.isFinite(coincident.normal.y)
                ? "Correto: colisão detectada com normal válida."
                : "ERRO: resultado inválido."
        );


    // Teste 4: círculos tangentes.
    PhysicsObject g = new PhysicsObject(
        0, 0, false, 1, 1,
        new CircleShape(1)
    );

    PhysicsObject h = new PhysicsObject(
        2, 0, false, 1, 1,
        new CircleShape(1)
    );

    CollisionData tangent = CollisionHelper.checkCollision(g, h);

        System.out.println("\nTESTE 4 - Círculos tangentes");

        System.out.println(
    tangent !=null&&tangent.penetration ==0.0
        ?"Correto: tangência detectada com penetração zero."
        :"ERRO: resultado inesperado."
        );

        // Teste 5: objetos com múltiplos círculos.
        PhysicsObject i = new PhysicsObject(
            0, 0, false, 1, 1,
            new CircleShape(1),
            new CircleShape(0.5, new Vector2(5, 0))
        );

        PhysicsObject j = new PhysicsObject(
            1.5, 0, false, 1, 1,
            new CircleShape(1)
        );

        CollisionData multiple = CollisionHelper.checkCollision(i, j);

        System.out.println("\nTESTE 5 - Objetos com múltiplos círculos");

        System.out.println(
            multiple != null
                ? "Correto: colisão encontrada entre as formas."
                : "ERRO: colisão não detectada."
        );

        // Teste 6: círculo próximo de um polígono.
        PhysicsObject circle = new PhysicsObject(
            0, 0, false, 1, 1,
            new CircleShape(1)
        );

        PhysicsObject polygon = new PhysicsObject(
            0.5, 0, true, 1, 1,
            new ConvexPolygonShape(
                new Vector2(0, -1),
                new Vector2(1, -1),
                new Vector2(1, 1),
                new Vector2(0, 1)
            )
        );

        CollisionData circlePolygon =
            CollisionHelper.checkCollision(circle, polygon);

        System.out.println("\nTESTE 6 - Círculo e polígono");

        if (circlePolygon != null) {
            System.out.println("Colisão detectada!");
            System.out.println(
                "Normal: (" + circlePolygon.normal.x
                    + ", " + circlePolygon.normal.y + ")"
            );
            System.out.println(
                "Penetração: " + circlePolygon.penetration
            );
            System.out.println(
                "Ponto de contato: ("
                    + circlePolygon.worldPoint.x + ", "
                    + circlePolygon.worldPoint.y + ")"
            );
        } else {
            System.out.println("ERRO: colisão não detectada.");
        }

        System.out.println("\nTESTE 7 - Polígono e círculo");

        CollisionData polygonCircle =
            CollisionHelper.checkCollision(polygon, circle);

        if (polygonCircle != null) {
            System.out.println("Colisão detectada!");
            System.out.println(
                "Normal: (" + polygonCircle.normal.x
                    + ", " + polygonCircle.normal.y + ")"
            );
            System.out.println(
                "Penetração: " + polygonCircle.penetration
            );
            System.out.println(
                "Ponto de contato: ("
                    + polygonCircle.worldPoint.x + ", "
                    + polygonCircle.worldPoint.y + ")"
            );
        } else {
            System.out.println("ERRO: colisão não detectada.");
        }

        System.out.println("\nTESTE 8 - Círculo e polígono separados");

        PhysicsObject circleFar = new PhysicsObject(
            -3, 0, false, 1, 1,
            new CircleShape(1)
        );

        CollisionData separated = CollisionHelper.checkCollision(
            circleFar, polygon
        );

        if (separated == null) {
            System.out.println("Correto: não existe colisão.");
        } else {
            System.out.println("ERRO: colisão detectada entre formas separadas.");
            System.out.println("Penetração: " + separated.penetration);
        }

        System.out.println("\nTESTE 9 - Círculo tangente ao polígono");

        PhysicsObject tangentCircle = new PhysicsObject(
            -0.5, 0, false, 1, 1,
            new CircleShape(1)
        );

        CollisionData tangentPolygon = CollisionHelper.checkCollision(
            tangentCircle, polygon
        );

        if (tangentPolygon != null) {
            System.out.println("Tangência detectada!");
            System.out.println(
                "Penetração: " + tangentPolygon.penetration
            );
            System.out.println(
                "Normal: (" + tangentPolygon.normal.x
                    + ", " + tangentPolygon.normal.y + ")"
            );
        } else {
            System.out.println("ERRO: a tangência não foi detectada.");
        }

        System.out.println("\nTESTE 10 - Círculo próximo ao vértice");

        PhysicsObject cornerCircle = new PhysicsObject(
            1.8, 1.8, false, 1, 1,
            new CircleShape(1)
        );

        CollisionData cornerCollision = CollisionHelper.checkCollision(
            cornerCircle, polygon
        );

        if (cornerCollision != null) {
            System.out.println("Colisão detectada!");
            System.out.println(
                "Penetração: " + cornerCollision.penetration
            );
            System.out.println(
                "Normal: (" + cornerCollision.normal.x
                    + ", " + cornerCollision.normal.y + ")"
            );
            System.out.println(
                "Ponto de contato: ("
                    + cornerCollision.worldPoint.x + ", "
                    + cornerCollision.worldPoint.y + ")"
            );
        } else {
            System.out.println("Nenhuma colisão detectada.");
        }

        System.out.println("\nTESTE 11 - Círculo e polígono rotacionado");

        PhysicsObject rotatedPolygon = new PhysicsObject(
            0, 0, true, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-1, -1),
                new Vector2(1, -1),
                new Vector2(1, 1),
                new Vector2(-1, 1)
            )
        );

// Rotaciona o polígono em 45 graus.
        rotatedPolygon.rotation = Math.PI / 4.0;

        PhysicsObject testCircle = new PhysicsObject(
            1.7, 0, false, 1, 1,
            new CircleShape(0.5)
        );

        CollisionData rotatedCollision = CollisionHelper.checkCollision(
            testCircle, rotatedPolygon
        );

        if (rotatedCollision != null) {
            System.out.println("Colisão detectada!");
            System.out.println(
                "Penetração: " + rotatedCollision.penetration
            );
            System.out.println(
                "Normal: (" + rotatedCollision.normal.x
                    + ", " + rotatedCollision.normal.y + ")"
            );
        } else {
            System.out.println("Nenhuma colisão detectada.");
        }

        System.out.println(
            "Comprimento da normal: "
                + Math.sqrt(
                rotatedCollision.normal.x * rotatedCollision.normal.x
                    + rotatedCollision.normal.y * rotatedCollision.normal.y
            )
        );

        System.out.println("\nTESTE 12 - Círculo dentro do polígono");

        PhysicsObject innerCircle = new PhysicsObject(
            0, 0, false, 1, 1,
            new CircleShape(0.5)
        );

        PhysicsObject enclosingPolygon = new PhysicsObject(
            0, 0, true, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-2, -2),
                new Vector2(2, -2),
                new Vector2(2, 2),
                new Vector2(-2, 2)
            )
        );

        CollisionData innerCollision = CollisionHelper.checkCollision(
            innerCircle, enclosingPolygon
        );

        if (innerCollision != null) {
            System.out.println("Colisão detectada!");
            System.out.println(
                "Penetração: " + innerCollision.penetration
            );
            System.out.println(
                "Normal: (" + innerCollision.normal.x
                    + ", " + innerCollision.normal.y + ")"
            );
        } else {
            System.out.println("ERRO: colisão não detectada.");
        }

        System.out.println("\nTESTE 13 - Polígono envolvendo círculo");

        CollisionData reversedInnerCollision =
            CollisionHelper.checkCollision(
                enclosingPolygon, innerCircle
            );

        if (reversedInnerCollision != null) {
            System.out.println("Colisão detectada!");
            System.out.println(
                "Penetração: " + reversedInnerCollision.penetration
            );
            System.out.println(
                "Normal: (" + reversedInnerCollision.normal.x
                    + ", " + reversedInnerCollision.normal.y + ")"
            );
        } else {
            System.out.println("ERRO: colisão não detectada.");
        }
    }
}
