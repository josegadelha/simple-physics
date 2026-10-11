package br.edu.ufersa.simplephysics.graphic;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GridRenderer {
    private static final Color GRID_COLOR = Color.valueOf("afafaf");
    private static final Color AXIS_COLOR = Color.valueOf("686868");
    private static final float AXIS_WIDTH = 0.03f;

    private final ShapeRenderer shapeRenderer;
    private final float gridSize;

    public GridRenderer(float gridSize) {
        this.gridSize = gridSize;
        this.shapeRenderer = new ShapeRenderer();
    }


    public void render(Camera camera, float width, float height) {
        float halfWidth = width / 2;
        float halfHeight = height / 2;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(GRID_COLOR);

        for (float x = gridSize; x <= halfWidth; x += gridSize) {
            shapeRenderer.line(x, -halfHeight, x, halfHeight);
            shapeRenderer.line(-x, -halfHeight, -x, halfHeight);
        }
        for (float y = gridSize; y <= halfHeight; y += gridSize) {
            shapeRenderer.line(-halfWidth, y, halfWidth, y);
            shapeRenderer.line(-halfWidth, -y, halfWidth, -y);
        }

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(AXIS_COLOR);

        shapeRenderer.rect(-AXIS_WIDTH / 2, -halfHeight, AXIS_WIDTH, height);
        shapeRenderer.rect(-halfWidth, -AXIS_WIDTH / 2, width, AXIS_WIDTH);

        shapeRenderer.end();
    }


    public void dispose() {
        shapeRenderer.dispose();
    }
}
