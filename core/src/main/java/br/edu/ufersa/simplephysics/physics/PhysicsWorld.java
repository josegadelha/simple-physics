package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;
import br.edu.ufersa.simplephysics.physics.shape.ConvexPolygonShape;
import br.edu.ufersa.simplephysics.physics.shape.PhysicsShape;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {
    public List<PhysicsObject> objects;
    public Vector2 gravity;
    public boolean collisionsEnabled;

    public PhysicsWorld() {
        this.objects = new ArrayList<>();
        this.gravity = new Vector2(0, -9.81);
        this.collisionsEnabled = false;
    }

    public void step(double dt) {
        // aplique a gravidade apenas a objetos dinâmicos.
        for (PhysicsObject object : objects) {
            if (!object.isStatic) {
                object.velocity = object.velocity.add(gravity.scale(dt));
            }
        }

        // atualize todos os objetos
        for (PhysicsObject object : objects) {
            object.step(dt);
        }

        // Detecta colisões se o botão estiver ativado
        if (this.collisionsEnabled) {
            for (int i = 0; i < objects.size(); i++) {
                for (int j = i + 1; j < objects.size(); j++) {
                    PhysicsObject objA = objects.get(i);
                    PhysicsObject objB = objects.get(j);

                    PhysicsShape shapeA = objA.shapes.get(0);
                    PhysicsShape shapeB = objB.shapes.get(0);
                    CollisionInfo info = null;

                    // Triagem do tipo de forma geométrica
                    if (shapeA instanceof ConvexPolygonShape && shapeB instanceof ConvexPolygonShape) {
                        info = CollisionHelper.polyToPolySAT(objA, (ConvexPolygonShape) shapeA, objB, (ConvexPolygonShape) shapeB);
                    } else if (shapeA instanceof CircleShape && shapeB instanceof CircleShape) {
                        info = CollisionHelper.circleToCircle(objA, (CircleShape) shapeA, objB, (CircleShape) shapeB);
                    } else if (shapeA instanceof CircleShape && shapeB instanceof ConvexPolygonShape) {
                        info = CollisionHelper.circleToPolySAT(objA, (CircleShape) shapeA, objB, (ConvexPolygonShape) shapeB);
                    } else if (shapeA instanceof ConvexPolygonShape && shapeB instanceof CircleShape) {
                        info = CollisionHelper.circleToPolySAT(objB, (CircleShape) shapeB, objA, (ConvexPolygonShape) shapeA);
                    }

                    if (info != null) {
                        System.out.printf("Colisão detetada! Penetração: %.4f\n", info.penetration);

                        // Executa o corte apenas se forem dois polígonos
                        if (shapeA instanceof ConvexPolygonShape && shapeB instanceof ConvexPolygonShape) {
                            List<Vector2> pontosDeContacto;

                            if (info.referenceIsA) {
                                pontosDeContacto = CollisionHelper.cortarPoligonoComPoligono(objA, (ConvexPolygonShape) shapeA, objB, (ConvexPolygonShape) shapeB, info);
                            } else {
                                pontosDeContacto = CollisionHelper.cortarPoligonoComPoligono(objB, (ConvexPolygonShape) shapeB, objA, (ConvexPolygonShape) shapeA, info);
                            }

                            for (Vector2 ponto : pontosDeContacto) {
                                System.out.printf("Ponto de contacto em X: %.2f, Y: %.2f\n", ponto.x, ponto.y);
                            }
                        }
                    }
                }
            }
        }
    }
}
