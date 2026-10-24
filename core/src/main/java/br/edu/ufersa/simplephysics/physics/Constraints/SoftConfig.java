package br.edu.ufersa.simplephysics.physics.Constraints;

public class SoftConfig {
        public double hertz;
        public double dampingRatio;
        public SoftConfig(double hertz, double dampingRatio) {
            this.hertz = hertz;
            this.dampingRatio = dampingRatio;
        }
}
