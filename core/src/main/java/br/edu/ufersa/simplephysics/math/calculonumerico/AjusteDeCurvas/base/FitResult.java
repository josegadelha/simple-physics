package br.edu.ufersa.simplephysics.math.calculonumerico.AjusteDeCurvas.base;

public class FitResult {

    private final double[] coefficients;
    private final String formatedExpression;
    public FitResult(final double[] coefficients, final String formatedExpression) {
        this.coefficients = coefficients;
        this.formatedExpression = formatedExpression;
    }

    public double[] getCoefficients() {
        return coefficients;
    }
    public String getFormatedExpression() {
        return formatedExpression;
    }
}
