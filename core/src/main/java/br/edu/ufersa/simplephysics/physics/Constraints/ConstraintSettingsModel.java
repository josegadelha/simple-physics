package br.edu.ufersa.simplephysics.physics.constraints;

public class ConstraintSettingsModel {
        public String mode = "soft"; // 'off' | 'baumgarte' | 'soft'
        public double baumgarteFactor = 0.1;
        public SoftConfig contactSoft = new SoftConfig(30.0, 10.0);
        public SoftConfig jointSoft = new SoftConfig(60.0, 0.0);
        public double contactSpeed = 3.0;
        public boolean warmStarting = true;
}
