package br.edu.ufersa.simplephysics.math;

public class SoftParams {
    public double biasRate;
    public double massScale;
    public double impulseScale;

    public SoftParams(double biasRate, double massScale, double impulseScale) {
        this.biasRate = biasRate;
        this.massScale = massScale;
        this.impulseScale = impulseScale;
    }

    public static SoftParams getSoftConstraintParams(double hertz, double dampingRatio, double timeStep) {
        if (hertz == 0) {
            return new SoftParams(0, 0, 0);
        }
        double omega = 2 * Math.PI * hertz;
        double a1 = 2 * dampingRatio + timeStep * omega;
        double a2 = timeStep * omega * a1;
        double a3 = 1.0 / (1.0 + a2);
        return new SoftParams(omega / a1, a2 * a3, a3);
    }
}
