
package br.edu.ufersa.simplephysics.graphic;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.PhysicsObject;
import br.edu.ufersa.simplephysics.physics.PhysicsWorld;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;
import br.edu.ufersa.simplephysics.physics.shape.ConvexPolygonShape;
import br.edu.ufersa.simplephysics.physics.shape.PhysicsShape;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PhysicsShapeRenderer {
    private static final double REFERENCE_CIRCLE_RADIUS = 0.25;
    private static final int REFERENCE_CIRCLE_SEGMENTS = 32;
    private static final int MIN_CIRCLE_SEGMENTS = 16;
    private static final int MAX_CIRCLE_SEGMENTS = 128;
    private static final Color OUTLINE_COLOR = Color.valueOf("686868");

    private final ShapeRenderer shapeRenderer;
    private final Map<PhysicsObject, Color> objectFillColors;
    private float nextHue = (float) Math.random();

    public PhysicsShapeRenderer() {
        this.shapeRenderer = new ShapeRenderer();
        this.objectFillColors = new IdentityHashMap<>();
    }

    public void render(Camera camera, PhysicsWorld world) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        for (PhysicsObject object : world.objects) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            try {
                shapeRenderer.setColor(getFillColor(object));
                for (PhysicsShape shape : object.shapes) {
                    if (shape instanceof CircleShape) {
                        drawCircle(object, (CircleShape) shape);
                    } else if (shape instanceof ConvexPolygonShape) {
                        drawPolygon(object, (ConvexPolygonShape) shape);
                    } else {
                        throw new IllegalArgumentException(
                            "Unsupported physics shape: " + shape.getClass().getName()
                        );
                    }
                }
            } finally {
                shapeRenderer.end();
            }

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            try {
                shapeRenderer.setColor(OUTLINE_COLOR);
                for (PhysicsShape shape : object.shapes) {
                    if (shape instanceof CircleShape) {
                        drawCircleOutline(object, (CircleShape) shape);
                    } else if (shape instanceof ConvexPolygonShape) {
                        drawPolygonOutline(object, (ConvexPolygonShape) shape);
                    }
                }
            } finally {
                shapeRenderer.end();
            }

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            try {
                shapeRenderer.setColor(OUTLINE_COLOR);
                for (PhysicsShape shape : object.shapes) {
                    if (shape instanceof CircleShape) {
                        drawCircleCenter(object, (CircleShape) shape);
                    }
                }
            } finally {
                shapeRenderer.end();
            }
        }
    }

    private Color getFillColor(PhysicsObject object) {
        return objectFillColors.computeIfAbsent(object, key -> {
            Color color = new Color().fromHsv(nextHue * 360f, 0.25f, 0.98f);

            nextHue = (nextHue + 0.618034f) % 1f;

            return color;
        });
    }

    private void drawCircle(PhysicsObject object, CircleShape circle) {
        Vector2 center = object.localToWorld(circle.offset);

        shapeRenderer.circle(
            (float) center.x,
            (float) center.y,
            (float) circle.radius,
            getCircleSegments(circle.radius)
        );
    }

    private void drawPolygon(PhysicsObject object, ConvexPolygonShape polygon) {
        Vector2 first = object.localToWorld(polygon.vertices.get(0));

        for (int i = 1; i < polygon.vertices.size() - 1; i++) {
            Vector2 second = object.localToWorld(polygon.vertices.get(i));
            Vector2 third = object.localToWorld(polygon.vertices.get(i + 1));

            shapeRenderer.triangle(
                (float) first.x, (float) first.y,
                (float) second.x, (float) second.y,
                (float) third.x, (float) third.y
            );
        }
    }

    private void drawCircleOutline(PhysicsObject object, CircleShape circle) {
        Vector2 center = object.localToWorld(circle.offset);

        shapeRenderer.circle(
            (float) center.x,
            (float) center.y,
            (float) circle.radius,
            getCircleSegments(circle.radius)
        );
        shapeRenderer.line(
            (float) center.x,
            (float) center.y,
            (float) (center.x + circle.radius * Math.cos(object.rotation)),
            (float) (center.y + circle.radius * Math.sin(object.rotation))
        );
    }

    private void drawCircleCenter(PhysicsObject object, CircleShape circle) {
        Vector2 center = object.localToWorld(circle.offset);

        shapeRenderer.circle(
            (float) center.x,
            (float) center.y,
            (float) circle.radius * 0.1f,
            getCircleSegments(circle.radius)
        );
    }

    private int getCircleSegments(double radius) {
        int segments = (int) Math.ceil(REFERENCE_CIRCLE_SEGMENTS * Math.sqrt(radius / REFERENCE_CIRCLE_RADIUS));
        return Math.clamp(segments, MIN_CIRCLE_SEGMENTS, MAX_CIRCLE_SEGMENTS);
    }

    private void drawPolygonOutline(PhysicsObject object, ConvexPolygonShape polygon) {
        for (int i = 0; i < polygon.vertices.size(); i++) {
            Vector2 start = object.localToWorld(polygon.vertices.get(i));
            Vector2 end = object.localToWorld(
                polygon.vertices.get((i + 1) % polygon.vertices.size())
            );

            shapeRenderer.line(
                (float) start.x,
                (float) start.y,
                (float) end.x,
                (float) end.y
            );
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
        objectFillColors.clear();
    }
}
