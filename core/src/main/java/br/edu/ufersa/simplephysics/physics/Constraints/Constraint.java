package br.edu.ufersa.simplephysics.physics.Constraints;

import br.edu.ufersa.simplephysics.physics.PhysicsObject;

abstract class Constraint {
    public PhysicsObject bodyA;
    public PhysicsObject bodyB;
    public Object constraintSettings = null;

    public Constraint(PhysicsObject bodyA, PhysicsObject bodyB) {
        this.bodyA = bodyA;
        this.bodyB = bodyB;
    }

    public void update(Object constraintSettings) {}
    public void solve(double dt, Object constraintSettings) {}
}
