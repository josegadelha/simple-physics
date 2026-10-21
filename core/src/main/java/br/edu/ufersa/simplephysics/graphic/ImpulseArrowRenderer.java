package br.edu.ufersa.simplephysics.graphic;

import br.edu.ufersa.simplephysics.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ImpulseArrowRenderer {
    private static final int CIRCLE_SEGMENTS = 32;
    private static final Color ARROW_COLOR = Color.valueOf("686868");
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    public void render(OrthographicCamera camera, Vector2 start, Vector2 end) {
        if (start == null || end == null) {
            return;
        }

        double deltaX = end.x - start.x;
        double deltaY = end.y - start.y;
        double arrowLength = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (arrowLength < 0.0001) {
            return;
        }

        // Convert world coordinates to float values for the shape renderer.
        float startX = (float) start.x;
        float startY = (float) start.y;
        float endX = (float) end.x;
        float endY = (float) end.y;

        // Normalize the direction vector.
        float directionX = (float) (deltaX / arrowLength);
        float directionY = (float) (deltaY / arrowLength);

        // The arrow head is sized relative to the camera zoom.
        float arrowHeadLength = 0.12f * camera.zoom;
        float arrowHeadWidth = 0.07f * camera.zoom;

        // The arrow shaft's rendering parameters.
        float shaftHalfThickness = 0.015f * camera.zoom;
        float perpendicularDirectionX = -directionY * shaftHalfThickness;
        float perpendicularDirectionY = directionX * shaftHalfThickness;

        // The arrow head's rendering parameters.
        float arrowHeadBaseX = endX - directionX * arrowHeadLength;
        float arrowHeadBaseY = endY - directionY * arrowHeadLength;
        float arrowHeadLeftX = arrowHeadBaseX - directionY * arrowHeadWidth;
        float arrowHeadLeftY = arrowHeadBaseY + directionX * arrowHeadWidth;
        float arrowHeadRightX = arrowHeadBaseX + directionY * arrowHeadWidth;
        float arrowHeadRightY = arrowHeadBaseY - directionX * arrowHeadWidth;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(ARROW_COLOR);

        // Draw the start marker at the start of the impulse.
        shapeRenderer.circle(startX, startY, 0.04f * camera.zoom, CIRCLE_SEGMENTS);

        // Draw the shaft as two triangles, stopping at the base of the arrow head.
        shapeRenderer.triangle(
            startX + perpendicularDirectionX, startY + perpendicularDirectionY,
            arrowHeadBaseX + perpendicularDirectionX, arrowHeadBaseY + perpendicularDirectionY,
            arrowHeadBaseX - perpendicularDirectionX, arrowHeadBaseY - perpendicularDirectionY
        );
        shapeRenderer.triangle(
            startX + perpendicularDirectionX, startY + perpendicularDirectionY,
            arrowHeadBaseX - perpendicularDirectionX, arrowHeadBaseY - perpendicularDirectionY,
            startX - perpendicularDirectionX, startY - perpendicularDirectionY
        );

        // Draw the triangular tip at the end of the impulse.
        shapeRenderer.triangle(
            endX, endY,
            arrowHeadLeftX, arrowHeadLeftY,
            arrowHeadRightX, arrowHeadRightY
        );

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
