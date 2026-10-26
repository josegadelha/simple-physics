package br.edu.ufersa.simplephysics.math.calculonumerico.AjusteDeCurvas;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.math.calculonumerico.AjusteDeCurvas.base.FitResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuadraticLeastSquareFitTest {
    private static final double DELTA = 1e-4;

    private final QuadraticLeastSquareFit fit = new QuadraticLeastSquareFit();

    @Test
    void fitsQuadraticCurveToExampleData() {
        FitResult result = fit.fit(List.of(
            new Vector2(0, 1.2),
            new Vector2(1, 1.6),
            new Vector2(2, 3.0),
            new Vector2(3, 5.1),
            new Vector2(4, 7.9),
            new Vector2(5, 11.6)
        ));

        assertArrayEquals(
            new double[] {1.167857142857, 0.1125, 0.394642857143},
            result.getCoefficients(),
            DELTA
        );
    }

    @Test
    void predictsYUsingConstantLinearAndQuadraticCoefficients() {
        assertEquals(1.1679 + 0.1125 * 4 + 0.3946 * 4 * 4,
            fit.predict(4, new double[] {1.1679, 0.1125, 0.3946}), DELTA);
    }

    @Test
    void rejectsDataWithFewerThanThreeDistinctXValues() {
        assertThrows(IllegalArgumentException.class, () -> fit.fit(List.of(
            new Vector2(1, 2),
            new Vector2(1, 4),
            new Vector2(2, 6)
        )));
    }

    @Test
    void rejectsWrongNumberOfPredictionCoefficients() {
        assertThrows(IllegalArgumentException.class,
            () -> fit.predict(2, new double[] {1, 2}));
    }
}
