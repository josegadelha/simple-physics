package br.edu.ufersa.simplephysics.math.calculonumerico.AjusteDeCurvas.base;

import br.edu.ufersa.simplephysics.math.Vector2;

import java.util.List;

public interface CurverFit {
    FitResult fit(List<Vector2> points);
    double predict(double x, double[] coefficients);
}

