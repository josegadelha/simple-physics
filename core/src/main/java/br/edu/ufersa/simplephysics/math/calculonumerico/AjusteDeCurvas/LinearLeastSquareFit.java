package br.edu.ufersa.simplephysics.math.calculonumerico.AjusteDeCurvas;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.math.calculonumerico.AjusteDeCurvas.base.CurverFit;
import br.edu.ufersa.simplephysics.math.calculonumerico.AjusteDeCurvas.base.FitResult;

import java.util.List;

public class LinearLeastSquareFit implements CurverFit {
    private final double TOLERANCE = 1e-12;

    @Override
    public FitResult fit(List<Vector2> points) {
        double sx = 0, sy = 0, sxx = 0, sxy = 0, det, a0, a1;
        int n = points.size();

        for (Vector2 point : points) {
            double x, y;
            x = point.x;
            y = point.y;

            sx += x;
            sy += y;

            sxx += x*x;
            sxy += x * y;
        }

        det = n*sxx - sx*sx;

        if (det < TOLERANCE){
            throw new RuntimeException("Impossible fit: det="+det);
        }

        a1 = (n*sxy - sx*sy) / det;
        a0 = (sy*sxx - sx*sxy) / det;

        double[] coefficients = {a0, a1};
        String formatedExpression = a0 + " " + a1 + "x";

        return new FitResult(coefficients, formatedExpression);
    }

    @Override
    public double predict(double x, double[] coefficients) {
        if (coefficients.length != 2){
            throw new RuntimeException("Impossible fit: coefficients.length="+coefficients.length);
        }

        return coefficients[0] + coefficients[1]*x;
    }
}
