package br.edu.ufersa.simplephysics.math;

public class Mat2x2 {
    public Vector2 mat2x2Solve(double[][] K, Vector2 b) {
        double det = K[0][0] * K[1][1] - K[0][1] * K[1][0];
        double invDet = det != 0 ? 1.0 / det : 0;
        return new Vector2(
            invDet * (K[1][1] * b.x - K[0][1] * b.y),
            invDet * (K[0][0] * b.y - K[1][0] * b.x)
        );
    }
}
