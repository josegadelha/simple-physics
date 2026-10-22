package br.edu.ufersa.simplephysics.physics.Constraints;

import br.edu.ufersa.simplephysics.math.Mat2x2;
import br.edu.ufersa.simplephysics.math.Vector2;

//Classe Constraint ainda não existe!!
public class RevoluteCostraint /*extends Constraint*/ {

    public double beta;
    public Vector2 worldA, worldB;
    public Mat2x2 K;

    public void solve(double dt){

        //Erro posicional
        Vector2 C = this.worldB.subtract(this.worldA);

        //Termo bias
        Vector2 bias = C.scale(this.beta / dt);

        //Impulso
        Vector2 impulse = mat2x2Solve(this.K, );
    }
}
