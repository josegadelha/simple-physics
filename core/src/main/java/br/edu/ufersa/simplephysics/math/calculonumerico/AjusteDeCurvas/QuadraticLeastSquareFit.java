package br.edu.ufersa.simplephysics.math.calculonumerico.AjusteDeCurvas;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.math.calculonumerico.AjusteDeCurvas.base.CurverFit;
import br.edu.ufersa.simplephysics.math.calculonumerico.AjusteDeCurvas.base.FitResult;
import br.edu.ufersa.simplephysics.math.calculonumerico.metodosDiretos.eliminacaoGaussiana.EliminacaoGaussiana;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuadraticLeastSquareFit implements CurverFit {

    @Override
    public FitResult fit(List<Vector2> points) {
        if (points == null) {
            throw new IllegalArgumentException("Points cannot be null.");
        }

        Set<Double> distinctX = new HashSet<>();
        double s0 = points.size();
        double s1 = 0, s2 = 0, s3 = 0, s4 = 0;
        double sy = 0, sxy = 0, sx2y = 0;

        for (Vector2 point : points) {
            if (point == null || !Double.isFinite(point.x) || !Double.isFinite(point.y)) {
                throw new IllegalArgumentException("Points must contain finite x and y values.");
            }

            double x = point.x;
            double y = point.y;
            double x2 = x * x;
            distinctX.add(x);
            s1 += x;
            s2 += x2;
            s3 += x2 * x;
            s4 += x2 * x2;
            sy += y;
            sxy += x * y;
            sx2y += x2 * y;
        }

        if (distinctX.size() < 3) {
            throw new IllegalArgumentException("At least 3 distinct x values are required.");
        }

        double[][] matrix = {
            {s0, s1, s2},
            {s1, s2, s3},
            {s2, s3, s4}
        };
        double[] rightHandSide = {sy, sxy, sx2y};
        double[] coefficients = EliminacaoGaussiana.resolver(matrix, rightHandSide);
        String expression = coefficients[0] + " " + coefficients[1] + "x "
            + coefficients[2] + "x^2";

        return new FitResult(coefficients, expression);
    }

    @Override
    public double predict(double x, double[] coefficients) {
        if (coefficients == null || coefficients.length != 3) {
            throw new IllegalArgumentException("Quadratic fit requires 3 coefficients [a, b, c].");
        }

        return coefficients[0] + coefficients[1] * x + coefficients[2] * x * x;
    }
}
