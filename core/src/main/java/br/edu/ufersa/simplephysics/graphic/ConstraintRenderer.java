package br.edu.ufersa.simplephysics.graphic;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.PhysicsObject;
import br.edu.ufersa.simplephysics.physics.PhysicsWorld;
import br.edu.ufersa.simplephysics.physics.Constraints.Constraint;
import br.edu.ufersa.simplephysics.physics.Constraints.RevoluteConstraint;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ConstraintRenderer {
    private static final int CIRCLE_SEGMENTS = 20;
    private static final Color JOINT_COLOR = Color.valueOf("686868");
    private static final Color PREVIEW_COLOR = Color.valueOf("a0a0a0");
    private static final float JOINT_RADIUS = 0.045f;
    private static final float ANCHOR_RADIUS = 0.025f;

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    public void render(
        Camera camera,
        PhysicsWorld world,
        Vector2 pendingAnchorStart,
        Vector2 pendingAnchorEnd
    ) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(JOINT_COLOR);
        try {
            for (Constraint constraint : world.constraints) {
                if (constraint instanceof RevoluteConstraint) {
                    drawConstraint((RevoluteConstraint) constraint);
                }
            }

            if (pendingAnchorStart != null && pendingAnchorEnd != null) {
                shapeRenderer.setColor(PREVIEW_COLOR);
                shapeRenderer.line(
                    (float) pendingAnchorStart.x,
                    (float) pendingAnchorStart.y,
                    (float) pendingAnchorEnd.x,
                    (float) pendingAnchorEnd.y
                );
            }
        } finally {
            shapeRenderer.end();
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(JOINT_COLOR);
        try {
            for (Constraint constraint : world.constraints) {
                if (constraint instanceof RevoluteConstraint) {
                    RevoluteConstraint revolute = (RevoluteConstraint) constraint;
                    drawAnchor(revolute.bodyA.localToWorld(revolute.localA), ANCHOR_RADIUS);
                    drawAnchor(revolute.bodyB.localToWorld(revolute.localB), ANCHOR_RADIUS);
                }
            }

            if (pendingAnchorStart != null && pendingAnchorEnd != null) {
                shapeRenderer.setColor(PREVIEW_COLOR);
                drawAnchor(pendingAnchorStart, ANCHOR_RADIUS);
                drawAnchor(pendingAnchorEnd, ANCHOR_RADIUS);
            }
        } finally {
            shapeRenderer.end();
        }
    }

    private void drawConstraint(RevoluteConstraint constraint) {
        Vector2 anchorA = constraint.bodyA.localToWorld(constraint.localA);
        Vector2 anchorB = constraint.bodyB.localToWorld(constraint.localB);
        shapeRenderer.line(
            (float) anchorA.x,
            (float) anchorA.y,
            (float) anchorB.x,
            (float) anchorB.y
        );
    }

    private void drawAnchor(Vector2 position, float radius) {
        shapeRenderer.circle(
            (float) position.x,
            (float) position.y,
            radius,
            CIRCLE_SEGMENTS
        );
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
