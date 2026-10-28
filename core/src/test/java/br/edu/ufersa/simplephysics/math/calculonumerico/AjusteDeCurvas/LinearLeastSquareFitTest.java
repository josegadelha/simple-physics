package br.edu.ufersa.simplephysics.math.calculonumerico.AjusteDeCurvas;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.math.calculonumerico.AjusteDeCurvas.base.FitResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LinearLeastSquareFitTest {
    private static final double DELTA = 1e-9;

    private final LinearLeastSquareFit fit = new LinearLeastSquareFit();

    @Test
    void calculatesInterceptAndSlopeForPointsOnALine() {
        FitResult result = fit.fit(List.of(
            new Vector2(0, 1),
            new Vector2(1, 3),
            new Vector2(2, 5)
        ));

        assertArrayEquals(new double[] {1, 2}, result.getCoefficients(), DELTA);
    }

    @Test
    void predictsYUsingInterceptAndSlope() {
        assertEquals(9, fit.predict(4, new double[] {1, 2}), DELTA);
    }

    @Test
    void rejectsPointsWithNoVariationInX() {
        assertThrows(RuntimeException.class, () -> fit.fit(List.of(
            new Vector2(2, 1),
            new Vector2(2, 3)
        )));
    }
}
