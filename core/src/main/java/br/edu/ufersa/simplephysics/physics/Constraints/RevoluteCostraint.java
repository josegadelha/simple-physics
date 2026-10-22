package br.edu.ufersa.simplephysics.physics.Constraints;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.math.Mat2x2;
import br.edu.ufersa.simplephysics.physics.PhysicsObject;

//Classe Constraint ainda não existe!!
public class RevoluteCostraint /*extends Constraint*/ {

    //Variáveis que vão ser usadas
    public double beta;
    public Vector2 worldA, worldB;
    public double[][] K = {{0, 0}, {0, 0}};
    public Vector2 rB = new Vector2(0, 0);
    public Vector2 localA, localB;

    //Construtor da classe
    public RevoluteConstraint(PhysicsObject bodyA, PhysicsObject bodyB, Vector2 worldPoint, Double lowerAngleLimit, Double upperAngleLimit, double stiffness) {
        super(bodyA, bodyB);
        this.localA = bodyA.worldToLocal(worldPoint);
        this.localB = bodyB.worldToLocal(worldPoint);
    }


    public void solve(double dt){

        Vector2 velB = this.bodyB.velocity.add(rB.crossSv(this.bodyB.angularVelocity));
        Vector2 Cdot = velB.subtract(this.worldA);

        //Erro posicional
        Vector2 C = this.worldB.subtract(this.worldA);

        //Termo bias
        Vector2 bias = C.scale(this.beta / dt);

        //Impulso
        Vector2 impulse = Mat2x2.mat2x2Solve(this.K, Cdot.add(bias).scale(-1));
    }
}
