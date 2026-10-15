package br.edu.ufersa.simplephysics.graphic;

import br.edu.ufersa.simplephysics.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ImpulseArrowRenderer {
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private static final Color ARROW_COLOR = Color.valueOf("686868");

    public void render(OrthographicCamera camera, Vector2 start, Vector2 end) {
        if (start == null || end == null) {
            return;
        }

        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length < 0.0001) {
            return;
        }

        float startX = (float) start.x;
        float startY = (float) start.y;
        float endX = (float) end.x;
        float endY = (float) end.y;
        float headLength = 0.12f * camera.zoom;
        float headWidth = 0.07f * camera.zoom;
        float directionX = (float) (dx / length);
        float directionY = (float) (dy / length);
        float baseX = endX - directionX * headLength;
        float baseY = endY - directionY * headLength;
        float headLeftX = baseX - directionY * headWidth;
        float headLeftY = baseY + directionX * headWidth;
        float headRightX = baseX + directionY * headWidth;
        float headRightY = baseY - directionX * headWidth;
        float halfLineWidth = 0.015f * camera.zoom;
        float perpendicularX = -directionY * halfLineWidth;
        float perpendicularY = directionX * halfLineWidth;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(ARROW_COLOR);
        shapeRenderer.circle(startX, startY, 0.04f * camera.zoom, 20);
        shapeRenderer.triangle(
            startX + perpendicularX, startY + perpendicularY,
            endX + perpendicularX, endY + perpendicularY,
            endX - perpendicularX, endY - perpendicularY
        );
        shapeRenderer.triangle(
            startX + perpendicularX, startY + perpendicularY,
            endX - perpendicularX, endY - perpendicularY,
            startX - perpendicularX, startY - perpendicularY
        );
        shapeRenderer.triangle(
            endX, endY,
            headLeftX, headLeftY,
            headRightX, headRightY
        );
        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
