package br.edu.ufersa.simplephysics.physics.Constraints;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.math.Mat2x2;
import br.edu.ufersa.simplephysics.physics.PhysicsObject;

public class RevoluteCostraint extends Constraint {

    //Variáveis que vão ser usadas
    public double beta;
    public Vector2 worldA, worldB;
    public double[][] K = {{0, 0}, {0, 0}};
    public Vector2 rB, rA = new Vector2(0, 0);
    public Vector2 localA, localB;

    //Construtor da classe
    public RevoluteConstraint(PhysicsObject bodyA, PhysicsObject bodyB, Vector2 worldPoint, Double lowerAngleLimit, Double upperAngleLimit, double stiffness) {
        super(bodyA, bodyB);
        this.localA = bodyA.worldToLocal(worldPoint);
        this.localB = bodyB.worldToLocal(worldPoint);
    }

    //Método que atualiza as posições dos pontos de ancoragem dos dois corpos
    public void update(Object settings){
        this.worldA = this.bodyA.localToWorld(this.localA);
        this.worldB = this.bodyB.localToWorld(this.localB);
        this.rA = this.worldA.subtract(this.bodyA.position);
        this.rB = this.worldB.subtract(this.bodyB.position);

        ConstraintSettingsModel cs = (this.constraintSettings != null) ? (ConstraintSettingsModel) this.constraintSettings : (ConstraintSettingsModel) settings;

        //Warm Starting: Reaplica o impulso do frame anterior para acelerar a convergência
        if (cs == null || cs.warmStarting) {
            this.bodyA.applyImpulse(this.accumulatedPointImpulse.scale(-1), this.worldA);
            this.bodyB.applyImpulse(this.accumulatedPointImpulse, this.worldB);
        }
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
