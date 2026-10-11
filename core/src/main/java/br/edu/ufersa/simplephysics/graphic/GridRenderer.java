package br.edu.ufersa.simplephysics.graphic;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GridRenderer {
    private static final float AXIS_WIDTH = 0.06f;
    private static final Color GRID_COLOR = Color.valueOf("afafaf");
    private static final Color AXIS_COLOR = Color.valueOf("686868");

    private final ShapeRenderer shapeRenderer;
    private final float gridSize;

    public GridRenderer(float gridSize) {
        this.gridSize = gridSize;
        this.shapeRenderer = new ShapeRenderer();
    }


    public void render(Camera camera, float width, float height) {
        float minX = camera.position.x - width / 2;
        float maxX = camera.position.x + width / 2;
        float minY = camera.position.y - height / 2;
        float maxY = camera.position.y + height / 2;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(GRID_COLOR);

        for (float x = (float) Math.ceil(minX / gridSize) * gridSize; x <= maxX; x += gridSize) {
            shapeRenderer.line(x, minY, x, maxY);
        }
        for (float y = (float) Math.ceil(minY / gridSize) * gridSize; y <= maxY; y += gridSize) {
            shapeRenderer.line(minX, y, maxX, y);
        }

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(AXIS_COLOR);

        if (minX <= 0 && maxX >= 0) {
            shapeRenderer.rect(-AXIS_WIDTH / 2, minY, AXIS_WIDTH, height);
        }
        if (minY <= 0 && maxY >= 0) {
            shapeRenderer.rect(minX, -AXIS_WIDTH / 2, width, AXIS_WIDTH);
        }

        shapeRenderer.end();
    }


    public void dispose() {
        shapeRenderer.dispose();
    }
}
