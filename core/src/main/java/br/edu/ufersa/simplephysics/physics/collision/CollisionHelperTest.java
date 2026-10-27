
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



        // Teste 14: dois polígonos sobrepostos.
        System.out.println("\nTESTE 14 - Polígonos sobrepostos");

        PhysicsObject polygonA = new PhysicsObject(
            0, 0, false, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-1, -1),
                new Vector2(1, -1),
                new Vector2(1, 1),
                new Vector2(-1, 1)
            )
        );

        PhysicsObject polygonB = new PhysicsObject(
            1.5, 0, false, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-1, -1),
                new Vector2(1, -1),
                new Vector2(1, 1),
                new Vector2(-1, 1)
            )
        );

        CollisionData polygonCollision =
            CollisionHelper.checkCollision(polygonA, polygonB);

        if (polygonCollision != null) {
            System.out.println("Correto: colisão detectada!");
            System.out.println(
                "Normal: (" + polygonCollision.normal.x
                    + ", " + polygonCollision.normal.y + ")"
            );
            System.out.println(
                "Penetração: " + polygonCollision.penetration
            );
            System.out.println(
                "Ponto de contato: ("
                    + polygonCollision.worldPoint.x + ", "
                    + polygonCollision.worldPoint.y + ")"
            );
        } else {
            System.out.println("ERRO: colisão não detectada.");
        }


        // Teste 15: dois polígonos separados.
        System.out.println("\nTESTE 15 - Polígonos separados");

        PhysicsObject polygonC = new PhysicsObject(
            0, 0, false, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-1, -1),
                new Vector2(1, -1),
                new Vector2(1, 1),
                new Vector2(-1, 1)
            )
        );

        PhysicsObject polygonD = new PhysicsObject(
            3, 0, false, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-1, -1),
                new Vector2(1, -1),
                new Vector2(1, 1),
                new Vector2(-1, 1)
            )
        );

        CollisionData separatedPolygons =
            CollisionHelper.checkCollision(polygonC, polygonD);

        if (separatedPolygons == null) {
            System.out.println("Correto: não existe colisão.");
        } else {
            System.out.println(
                "ERRO: colisão detectada indevidamente."
            );
            System.out.println(
                "Penetração: " + separatedPolygons.penetration
            );
        }


        // Teste 16: dois polígonos que apenas se encostam.
        System.out.println("\nTESTE 16 - Polígonos tangentes");

        PhysicsObject polygonE = new PhysicsObject(
            0, 0, false, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-1, -1),
                new Vector2(1, -1),
                new Vector2(1, 1),
                new Vector2(-1, 1)
            )
        );

        PhysicsObject polygonF = new PhysicsObject(
            2, 0, false, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-1, -1),
                new Vector2(1, -1),
                new Vector2(1, 1),
                new Vector2(-1, 1)
            )
        );

        CollisionData tangentPolygons =
            CollisionHelper.checkCollision(polygonE, polygonF);

        if (tangentPolygons != null
            && tangentPolygons.penetration == 0.0) {

            System.out.println(
                "Correto: tangência detectada com penetração zero."
            );
            System.out.println(
                "Normal: (" + tangentPolygons.normal.x
                    + ", " + tangentPolygons.normal.y + ")"
            );

        } else if (tangentPolygons != null) {

            System.out.println(
                "Colisão detectada, mas a penetração foi: "
                    + tangentPolygons.penetration
            );

        } else {
            System.out.println(
                "ERRO: a tangência não foi detectada."
            );
        }


        // Teste 17: colisão entre polígonos com rotação.
        System.out.println("\nTESTE 17 - Polígonos rotacionados");

        PhysicsObject polygonG = new PhysicsObject(
            0, 0, false, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-1, -1),
                new Vector2(1, -1),
                new Vector2(1, 1),
                new Vector2(-1, 1)
            )
        );

        PhysicsObject polygonH = new PhysicsObject(
            1.5, 0, false, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-1, -1),
                new Vector2(1, -1),
                new Vector2(1, 1),
                new Vector2(-1, 1)
            )
        );

        // Rotaciona o segundo quadrado em 45 graus.
        polygonH.rotation = Math.PI / 4.0;

        CollisionData rotatedPolygons =
            CollisionHelper.checkCollision(polygonG, polygonH);

        if (rotatedPolygons != null) {
            System.out.println("Correto: colisão detectada!");
            System.out.println(
                "Normal: (" + rotatedPolygons.normal.x
                    + ", " + rotatedPolygons.normal.y + ")"
            );
            System.out.println(
                "Penetração: " + rotatedPolygons.penetration
            );
        } else {
            System.out.println("ERRO: colisão não detectada.");
        }


        // Teste 18: polígonos rotacionados e separados.
        System.out.println(
            "\nTESTE 18 - Polígonos rotacionados e separados"
        );

        PhysicsObject polygonI = new PhysicsObject(
            0, 0, false, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-1, -1),
                new Vector2(1, -1),
                new Vector2(1, 1),
                new Vector2(-1, 1)
            )
        );

        PhysicsObject polygonJ = new PhysicsObject(
            5, 0, false, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-1, -1),
                new Vector2(1, -1),
                new Vector2(1, 1),
                new Vector2(-1, 1)
            )
        );

        polygonJ.rotation = Math.PI / 4.0;

        CollisionData rotatedSeparated =
            CollisionHelper.checkCollision(polygonI, polygonJ);

        if (rotatedSeparated == null) {
            System.out.println(
                "Correto: não existe colisão."
            );
        } else {
            System.out.println(
                "ERRO: colisão detectada indevidamente."
            );
            System.out.println(
                "Penetração: " + rotatedSeparated.penetration
            );
        }


        // Teste 19: inverter a ordem dos polígonos.
        System.out.println(
            "\nTESTE 19 - Inversão da ordem dos polígonos"
        );

        CollisionData reverseCollision =
            CollisionHelper.checkCollision(polygonB, polygonA);

        if (polygonCollision != null && reverseCollision != null) {
            System.out.println(
                "Normal A -> B: ("
                    + polygonCollision.normal.x + ", "
                    + polygonCollision.normal.y + ")"
            );

            System.out.println(
                "Normal B -> A: ("
                    + reverseCollision.normal.x + ", "
                    + reverseCollision.normal.y + ")"
            );

            System.out.println(
                "Penetração A -> B: "
                    + polygonCollision.penetration
            );

            System.out.println(
                "Penetração B -> A: "
                    + reverseCollision.penetration
            );

            boolean normalsOpposite =
                Math.abs(
                    polygonCollision.normal.x
                        + reverseCollision.normal.x
                ) < 1e-9
                    && Math.abs(
                    polygonCollision.normal.y
                        + reverseCollision.normal.y
                ) < 1e-9;

            boolean samePenetration =
                Math.abs(
                    polygonCollision.penetration
                        - reverseCollision.penetration
                ) < 1e-9;

            System.out.println(
                normalsOpposite && samePenetration
                    ? "Correto: normal invertida e penetração consistente."
                    : "ERRO: os resultados são inconsistentes."
            );

        } else {
            System.out.println(
                "ERRO: uma das colisões não foi detectada."
            );
        }


        // Teste 20: verificar se a normal é unitária.
        System.out.println("\nTESTE 20 - Normal unitária");

        if (polygonCollision != null) {
            double normalLength = Math.sqrt(
                polygonCollision.normal.x * polygonCollision.normal.x
                    + polygonCollision.normal.y * polygonCollision.normal.y
            );

            System.out.println("Comprimento da normal: " + normalLength);

            if (Math.abs(normalLength - 1.0) < 1e-9) {
                System.out.println("Correto: a normal é unitária.");
            } else {
                System.out.println("ERRO: a normal não é unitária.");
            }
        } else {
            System.out.println("ERRO: não existe colisão para testar.");
        }


        // Teste 21: polígono com uma aresta de comprimento zero.
        System.out.println(
            "\nTESTE 21 - Polígono com vértices consecutivos iguais"
        );

        PhysicsObject polygonWithDuplicateVertex = new PhysicsObject(
            0, 0, false, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-1, -1),
                new Vector2(1, -1),
                new Vector2(1, -1), // Vértice repetido.
                new Vector2(1, 1),
                new Vector2(-1, 1)
            )
        );

        PhysicsObject polygonForDuplicateTest = new PhysicsObject(
            1.5, 0, false, 1, 1,
            new ConvexPolygonShape(
                new Vector2(-1, -1),
                new Vector2(1, -1),
                new Vector2(1, 1),
                new Vector2(-1, 1)
            )
        );

        CollisionData duplicateVertexCollision =
            CollisionHelper.checkCollision(
                polygonWithDuplicateVertex,
                polygonForDuplicateTest
            );

        if (duplicateVertexCollision != null
            && Double.isFinite(duplicateVertexCollision.normal.x)
            && Double.isFinite(duplicateVertexCollision.normal.y)
            && Double.isFinite(duplicateVertexCollision.penetration)) {

            System.out.println(
                "Colisão detectada com valores finitos."
            );
            System.out.println(
                "Penetração: "
                    + duplicateVertexCollision.penetration
            );

        } else {
            System.out.println(
                "ERRO: colisão não detectada ou resultado inválido."
            );
        }


        // Teste 22: validar as coordenadas do ponto de contato.
        System.out.println("\nTESTE 22 - Ponto de contato válido");

        if (polygonCollision != null
            && Double.isFinite(polygonCollision.worldPoint.x)
            && Double.isFinite(polygonCollision.worldPoint.y)) {

            System.out.println(
                "Correto: ponto de contato com coordenadas finitas."
            );
            System.out.println(
                "Ponto: (" + polygonCollision.worldPoint.x
                    + ", " + polygonCollision.worldPoint.y + ")"
            );

        } else {
            System.out.println(
                "ERRO: ponto de contato inválido."
            );
        }



    }
}
