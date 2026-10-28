package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.shape.ConvexPolygonShape;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;
import java.util.ArrayList;
import java.util.List;

public class CollisionHelper {

    // Extrai as normais perpendiculares a partir de vértices globais
    private static List<Vector2> getNormals(List<Vector2> vertices) {
        List<Vector2> normals = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            Vector2 p1 = vertices.get(i);
            Vector2 p2 = vertices.get((i + 1) % vertices.size());
            Vector2 edge = p2.subtract(p1);
            normals.add(edge.rotate90CW().normalize());
        }
        return normals;
    }

    // Projeta os vértices em um eixo e retorna [min, max]
    private static double[] projectVerts(List<Vector2> verts, Vector2 axis) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (Vector2 v : verts) {
            double p = v.dotProduct(axis);
            if (p < min) min = p;
            if (p > max) max = p;
        }
        return new double[]{min, max};
    }

    public static CollisionInfo polyToPolySAT(PhysicsObject objA, ConvexPolygonShape shapeA,
                                              PhysicsObject objB, ConvexPolygonShape shapeB) {

        // 1. Transformar os vértices locais em globais (World Space)
        List<Vector2> vertsA = new ArrayList<>();
        for (Vector2 v : shapeA.vertices) vertsA.add(objA.localToWorld(v));

        List<Vector2> vertsB = new ArrayList<>();
        for (Vector2 v : shapeB.vertices) vertsB.add(objB.localToWorld(v));

        List<Vector2> normalsA = getNormals(vertsA);
        List<Vector2> normalsB = getNormals(vertsB);

        double globalMinPen = Double.POSITIVE_INFINITY;
        double bestSepA = Double.NEGATIVE_INFINITY;
        int bestEdgeA = 0;

        double bestSepB = Double.NEGATIVE_INFINITY;
        int bestEdgeB = 0;

        // 2. Testar todos os eixos normais do Polígono A
        for (int i = 0; i < normalsA.size(); i++) {
            Vector2 n = normalsA.get(i);
            double[] projA = projectVerts(vertsA, n);
            double[] projB = projectVerts(vertsB, n);

            // Existe um eixo de separação = Nenhuma colisão possível
            if (projA[0] > projB[1] || projB[0] > projA[1]) return null;

            double pen = Math.min(projA[1], projB[1]) - Math.max(projA[0], projB[0]);
            if (pen < globalMinPen) globalMinPen = pen;

            double sep = projB[0] - projA[1];
            if (sep > bestSepA) {
                bestSepA = sep;
                bestEdgeA = i;
            }
        }

        // 3. Testar todos os eixos normais do Polígono B
        for (int i = 0; i < normalsB.size(); i++) {
            Vector2 n = normalsB.get(i);
            double[] projA = projectVerts(vertsA, n);
            double[] projB = projectVerts(vertsB, n);

            if (projA[0] > projB[1] || projB[0] > projA[1]) return null;

            double pen = Math.min(projA[1], projB[1]) - Math.max(projA[0], projB[0]);
            if (pen < globalMinPen) globalMinPen = pen;

            double sep = projA[0] - projB[1];
            if (sep > bestSepB) {
                bestSepB = sep;
                bestEdgeB = i;
            }
        }

        // 4. Determinar a Aresta de Referência (com tolerância de histerese)
        boolean referenceIsA = true;
        int referenceEdgeIndex = bestEdgeA;
        Vector2 normal = normalsA.get(referenceEdgeIndex);

        double faceSwitchTol = 1e-4;
        if (bestSepB > bestSepA + faceSwitchTol) {
            referenceIsA = false;
            referenceEdgeIndex = bestEdgeB;
            normal = normalsB.get(referenceEdgeIndex);
        }

        return new CollisionInfo(normal, globalMinPen, referenceIsA, referenceEdgeIndex);
    }

    // Colisão Círculo vs Círculo
    public static CollisionInfo circleToCircle(PhysicsObject objA, CircleShape shapeA, PhysicsObject objB, CircleShape shapeB) {
        Vector2 posA = objA.localToWorld(shapeA.offset);
        Vector2 posB = objB.localToWorld(shapeB.offset);
        Vector2 diff = posB.subtract(posA);
        double dist = diff.length();
        double radiusSum = shapeA.radius + shapeB.radius;

        if (dist >= radiusSum) return null; // Não colidiu

        double penetration = radiusSum - dist;
        Vector2 normal = dist > 0 ? diff.normalize() : new Vector2(1, 0);
        return new CollisionInfo(normal, penetration, true, -1);
    }

    // Colisão Círculo vs Polígono (Tradução do tutorial)
    public static CollisionInfo circleToPolySAT(PhysicsObject objCirc, CircleShape circ, PhysicsObject objPoly, ConvexPolygonShape poly) {
        Vector2 circlePos = objCirc.localToWorld(circ.offset);
        List<Vector2> verts = new ArrayList<>();
        for (Vector2 v : poly.vertices) verts.add(objPoly.localToWorld(v));

        double minPen = Double.POSITIVE_INFINITY;
        Vector2 bestNormal = null;
        List<Vector2> normals = getNormals(verts);

        // Testar todas as normais das faces do polígono
        for (Vector2 n : normals) {
            double[] projPoly = projectVerts(verts, n);
            double cProj = circlePos.dotProduct(n);
            double minC = cProj - circ.radius;
            double maxC = cProj + circ.radius;

            if (projPoly[0] > maxC || minC > projPoly[1]) return null;

            double pen = Math.min(projPoly[1], maxC) - Math.max(projPoly[0], minC);
            if (pen < minPen) {
                minPen = pen;
                bestNormal = n;
            }
        }

        // Testar eixo especial: do centro do círculo ao vértice mais próximo
        Vector2 closestVert = verts.get(0);
        double minDist = closestVert.subtract(circlePos).length();
        for (Vector2 v : verts) {
            double d = v.subtract(circlePos).length();
            if (d < minDist) {
                closestVert = v;
                minDist = d;
            }
        }

        Vector2 axis = closestVert.subtract(circlePos).normalize();
        double[] projPoly = projectVerts(verts, axis);
        double cProj = circlePos.dotProduct(axis);
        double minC = cProj - circ.radius;
        double maxC = cProj + circ.radius;

        if (projPoly[0] > maxC || minC > projPoly[1]) return null;

        double pen = Math.min(projPoly[1], maxC) - Math.max(projPoly[0], minC);
        if (pen < minPen) {
            minPen = pen;
            bestNormal = axis;
        }

        return new CollisionInfo(bestNormal, minPen, false, -1);
    }

    // Algoritmo de corte de segmento de reta (Clipping)
    private static List<Vector2> cortarSegmentoDeReta(Vector2 p1, Vector2 p2, Vector2 normal, Vector2 deslocamento) {
        List<Vector2> pontosCortados = new ArrayList<>();
        double distancia0 = p1.subtract(deslocamento).dotProduct(normal);
        double distancia1 = p2.subtract(deslocamento).dotProduct(normal);

        // Pontos atrás do plano não são cortados
        if (distancia0 <= 0) pontosCortados.add(p1);
        if (distancia1 <= 0) pontosCortados.add(p2);

        // Se um ponto está na frente e o outro atrás, calcula a intersecção
        if (Math.signum(distancia0) != Math.signum(distancia1) && pontosCortados.size() < 2) {
            double percentagemCruzamento = distancia1 / (distancia1 - distancia0);
            Vector2 pontoIntersecao = p2.add(p1.subtract(p2).scale(percentagemCruzamento));
            pontosCortados.add(pontoIntersecao);
        }

        return pontosCortados;
    }

    // Extrai os pontos de contacto entre dois polígonos
    public static List<Vector2> cortarPoligonoComPoligono(PhysicsObject objRef, ConvexPolygonShape formaRef,
                                                          PhysicsObject objInc, ConvexPolygonShape formaInc,
                                                          CollisionInfo colisao) {

        List<Vector2> vertsRef = new ArrayList<>();
        for (Vector2 v : formaRef.vertices) vertsRef.add(objRef.localToWorld(v));

        List<Vector2> vertsInc = new ArrayList<>();
        for (Vector2 v : formaInc.vertices) vertsInc.add(objInc.localToWorld(v));

        List<Vector2> normaisRef = getNormals(vertsRef);
        List<Vector2> normaisInc = getNormals(vertsInc);

        int indiceRef = colisao.referenceEdgeIndex;
        Vector2 a1 = vertsRef.get(indiceRef);
        Vector2 a2 = vertsRef.get((indiceRef + 1) % vertsRef.size());
        Vector2 n = normaisRef.get(indiceRef);

        // Seleção da aresta incidente
        double menorProdutoEscalar = Double.POSITIVE_INFINITY;
        int indiceIncidente = 0;
        for (int i = 0; i < normaisInc.size(); i++) {
            double d = n.dotProduct(normaisInc.get(i));
            if (d < menorProdutoEscalar) {
                menorProdutoEscalar = d;
                indiceIncidente = i;
            }
        }

        Vector2 b2 = vertsInc.get(indiceIncidente);
        Vector2 b1 = vertsInc.get((indiceIncidente + 1) % vertsInc.size());

        Vector2 arestaRef = a2.subtract(a1);
        Vector2 tangenteRef = arestaRef.normalize();
        double tamanhoRef = arestaRef.length();

        // Eliminação precoce
        double proj1 = b1.subtract(a1).dotProduct(tangenteRef);
        double proj2 = b2.subtract(a1).dotProduct(tangenteRef);
        double projMin = Math.min(proj1, proj2);
        double projMax = Math.max(proj1, proj2);

        if (projMax < 0 || projMin > tamanhoRef) return new ArrayList<>();

        // Primeiro corte
        List<Vector2> pontosCortados = cortarSegmentoDeReta(b1, b2, tangenteRef.scale(-1), a1);
        if (pontosCortados.size() < 2) return new ArrayList<>();

        // Segundo corte
        pontosCortados = cortarSegmentoDeReta(pontosCortados.get(0), pontosCortados.get(1), tangenteRef, a2);

        // Filtro final de penetração
        double TOLERANCIA_LINEAR = 0.002;
        List<Vector2> pontosFinais = new ArrayList<>();
        for (Vector2 ponto : pontosCortados) {
            if (n.dotProduct(ponto.subtract(a1)) <= TOLERANCIA_LINEAR) {
                pontosFinais.add(ponto);
            }
        }

        return pontosFinais;
    }

}
