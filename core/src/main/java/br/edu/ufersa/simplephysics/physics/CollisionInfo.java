package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.math.Vector2;

public class CollisionInfo {
    public final Vector2 normal;
    public final double penetration;
    public final boolean referenceIsA;
    public final int referenceEdgeIndex;

    public CollisionInfo(Vector2 normal, double penetration, boolean referenceIsA, int referenceEdgeIndex) {
        this.normal = normal;
        this.penetration = penetration;
        this.referenceIsA = referenceIsA;
        this.referenceEdgeIndex = referenceEdgeIndex;
    }
}
