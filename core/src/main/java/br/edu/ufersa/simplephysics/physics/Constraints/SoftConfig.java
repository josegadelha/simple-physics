package br.edu.ufersa.simplephysics.physics.constraints;

public class SoftConfig {
        public double hertz;
        public double dampingRatio;
        public SoftConfig(double hertz, double dampingRatio) {
            this.hertz = hertz;
            this.dampingRatio = dampingRatio;
        }
}
