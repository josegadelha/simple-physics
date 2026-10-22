package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {
    public List<PhysicsObject> objects;
    public Vector2 gravity;

    public PhysicsWorld() {
        this.objects = new ArrayList<>();
        this.gravity = new Vector2(0, -9.81);
    }

    public void step(double dt) {
        // Apply gravity only to dynamic objects.
        for (PhysicsObject object : objects) {
            if (!object.isStatic) {
                object.velocity = object.velocity.add(gravity.scale(dt));
            }
        }

        // Update all objects
        for (PhysicsObject object : objects) {
            object.step(dt);
        }
    }
}
