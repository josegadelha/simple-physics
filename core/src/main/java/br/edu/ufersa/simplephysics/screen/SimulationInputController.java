package br.edu.ufersa.simplephysics.screen;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.PhysicsObject;
import br.edu.ufersa.simplephysics.physics.PhysicsWorld;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;
import br.edu.ufersa.simplephysics.physics.shape.ConvexPolygonShape;
import br.edu.ufersa.simplephysics.physics.shape.PhysicsShape;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SimulationInputController {
    private static final float MIN_CAMERA_ZOOM = 0.25f;
    private static final float MAX_CAMERA_ZOOM = 3f;
    private static final float ZOOM_STEP = 1.1f;
    private static final double DEFAULT_CIRCLE_RADIUS = 0.25;
    private static final double DEFAULT_SQUARE_SIDE_LENGTH = 0.5;

    public enum Tool {
        NONE,
        CREATE_SQUARE,
        CREATE_CIRCLE,
        APPLY_IMPULSE
    }

    private final ScreenViewport viewport;
    private final OrthographicCamera camera;
    private final PhysicsWorld world;
    private final InputProcessor inputProcessor;
    private final float worldUnitsPerPixel;

    private Tool selectedTool = Tool.NONE;
    private boolean newObjectStatic;
    private double circleRadius = DEFAULT_CIRCLE_RADIUS;
    private double squareSideLength = DEFAULT_SQUARE_SIDE_LENGTH;
    private boolean applyingImpulse;
    private boolean cameraFollowEnabled;
    private PhysicsObject followedObject;
    private Vector2 impulseStart;
    private Vector2 impulseEnd;

    public SimulationInputController(
        ScreenViewport viewport,
        OrthographicCamera camera,
        PhysicsWorld world,
        float worldUnitsPerPixel
    ) {
        this.viewport = viewport;
        this.camera = camera;
        this.world = world;
        this.worldUnitsPerPixel = worldUnitsPerPixel;
        inputProcessor = createInputProcessor();
    }

    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    public void setSelectedTool(Tool selectedTool) {
        this.selectedTool = selectedTool;
    }

    public Tool getSelectedTool() {
        return selectedTool;
    }

    public void setNewObjectStatic(boolean newObjectStatic) {
        this.newObjectStatic = newObjectStatic;
    }

    public double getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(double circleRadius) {
        if (!Double.isFinite(circleRadius) || circleRadius <= 0) {
            throw new IllegalArgumentException("Circle radius must be finite and positive");
        }
        this.circleRadius = circleRadius;
    }

    public double getSquareSideLength() {
        return squareSideLength;
    }

    public void setSquareSideLength(double squareSideLength) {
        if (!Double.isFinite(squareSideLength) || squareSideLength <= 0) {
            throw new IllegalArgumentException("Square side length must be finite and positive");
        }
        this.squareSideLength = squareSideLength;
    }

    public boolean isApplyingImpulse() {
        return applyingImpulse;
    }

    public Vector2 getImpulseStart() {
        return impulseStart;
    }

    public Vector2 getImpulseEnd() {
        return impulseEnd;
    }

    public void setCameraFollowEnabled(boolean enabled) {
        cameraFollowEnabled = enabled;
        if (!enabled) {
            followedObject = null;
        }
    }

    public boolean isFollowingObject() {
        return followedObject != null;
    }

    public boolean isWaitingForFollowTarget() {
        return cameraFollowEnabled && followedObject == null;
    }

    public void updateCameraFollow() {
        if (cameraFollowEnabled && followedObject != null) {
            camera.position.x = (float) followedObject.position.x;
            camera.position.y = (float) followedObject.position.y;
            camera.update();
        }
    }

    private InputProcessor createInputProcessor() {
        return new InputAdapter() {
            private boolean isPanning;
            private int previousScreenX;
            private int previousScreenY;
            private PhysicsObject draggedPhysicsObject;

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Middle-drag pans independently of the selected tool.
                if (button == Input.Buttons.MIDDLE && isInsideViewport(screenX, screenY)) {
                    isPanning = true;
                    previousScreenX = screenX;
                    previousScreenY = screenY;
                    return true;
                }

                if (button != Input.Buttons.LEFT || !isInsideViewport(screenX, screenY)) {
                    return false;
                }

                Vector2 worldPosition = getMouseWorldPosition(screenX, screenY);
                if (cameraFollowEnabled && followedObject == null) {
                    PhysicsObject selectedObject = findObjectAt(worldPosition);
                    if (selectedObject != null) {
                        followedObject = selectedObject;
                    }
                    return true;
                }

                if (selectedTool == Tool.APPLY_IMPULSE) {
                    // Start an impulse drag only on an object.
                    draggedPhysicsObject = findObjectAt(worldPosition);
                    if (draggedPhysicsObject == null) {
                        return false;
                    }

                    impulseStart = worldPosition;
                    impulseEnd = worldPosition;
                    applyingImpulse = true;
                    return true;
                }

                if (selectedTool == Tool.CREATE_SQUARE) {
                    world.addSquare(
                        worldPosition.x,
                        worldPosition.y,
                        squareSideLength,
                        newObjectStatic
                    );
                    return true;
                }
                if (selectedTool == Tool.CREATE_CIRCLE) {
                    world.addCircle(
                        worldPosition.x,
                        worldPosition.y,
                        circleRadius,
                        newObjectStatic
                    );
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (draggedPhysicsObject != null) {
                    impulseEnd = getMouseWorldPosition(screenX, screenY);
                    return true;
                }

                if (!isPanning) {
                    return false;
                }

                // Adjust screen movement for camera zoom.
                camera.position.add(
                    -(screenX - previousScreenX) * worldUnitsPerPixel * camera.zoom,
                    (screenY - previousScreenY) * worldUnitsPerPixel * camera.zoom,
                    0
                );
                camera.update();
                previousScreenX = screenX;
                previousScreenY = screenY;
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT && draggedPhysicsObject != null) {
                    impulseEnd = getMouseWorldPosition(screenX, screenY);
                    Vector2 impulse = impulseEnd.subtract(impulseStart);
                    double magnitude = impulse.length();
                    if (magnitude > 0) {
                        // Apply the impulse where the drag began.
                        draggedPhysicsObject.applyImpulse(impulse.scale(magnitude), impulseStart);
                    }

                    // Reset the impulse drag state.
                    draggedPhysicsObject = null;
                    impulseStart = null;
                    impulseEnd = null;
                    applyingImpulse = false;
                    return true;
                }

                if (button != Input.Buttons.MIDDLE || !isPanning) {
                    return false;
                }

                isPanning = false;
                return true;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                if (!isInsideViewport(Gdx.input.getX(), Gdx.input.getY()) || amountY == 0) {
                    return false;
                }

                // Keep zoom changes proportional across wheel steps.
                float zoomFactor = (float) Math.pow(ZOOM_STEP, amountY);
                camera.zoom = Math.clamp(camera.zoom * zoomFactor, MIN_CAMERA_ZOOM, MAX_CAMERA_ZOOM);
                camera.update();
                return true;
            }
        };
    }

    private boolean isInsideViewport(int screenX, int screenY) {
        // Convert screen Y (top-origin) to viewport Y (bottom-origin).
        int screenYFromBottom = Gdx.graphics.getHeight() - screenY;
        return screenX >= viewport.getScreenX()
            && screenX < viewport.getScreenX() + viewport.getScreenWidth()
            && screenYFromBottom >= viewport.getScreenY()
            && screenYFromBottom < viewport.getScreenY() + viewport.getScreenHeight();
    }

    private Vector2 getMouseWorldPosition(int screenX, int screenY) {
        // Convert screen coordinates to world coordinates.
        com.badlogic.gdx.math.Vector2 worldPosition =
            viewport.unproject(new com.badlogic.gdx.math.Vector2(screenX, screenY));
        return new Vector2(worldPosition.x, worldPosition.y);
    }

    private PhysicsObject findObjectAt(Vector2 worldPosition) {
        // Check topmost objects first.
        for (int objectIndex = world.objects.size() - 1; objectIndex >= 0; objectIndex--) {
            PhysicsObject physicsObject = world.objects.get(objectIndex);
            // Shapes are defined in object-local coordinates.
            Vector2 localPosition = physicsObject.worldToLocal(worldPosition);

            for (PhysicsShape physicsShape : physicsObject.shapes) {
                if (physicsShape instanceof CircleShape) {
                    CircleShape circleShape = (CircleShape) physicsShape;
                    if (localPosition.subtract(circleShape.offset).length() <= circleShape.radius) {
                        return physicsObject;
                    }
                } else if (physicsShape instanceof ConvexPolygonShape
                    && containsPoint((ConvexPolygonShape) physicsShape, localPosition)) {
                    return physicsObject;
                }
            }
        }
        return null;
    }

    private boolean containsPoint(ConvexPolygonShape polygon, Vector2 point) {
        boolean inside = false;
        int vertexCount = polygon.vertices.size();

        // Toggle inside/outside whenever a ray crosses an edge.
        for (int vertexIndex = 0, previousVertexIndex = vertexCount - 1;
             vertexIndex < vertexCount;
             previousVertexIndex = vertexIndex++) {
            Vector2 currentVertex = polygon.vertices.get(vertexIndex);
            Vector2 previousVertex = polygon.vertices.get(previousVertexIndex);
            if ((currentVertex.y > point.y) != (previousVertex.y > point.y)) {
                double intersectionX = (previousVertex.x - currentVertex.x)
                    * (point.y - currentVertex.y) / (previousVertex.y - currentVertex.y) + currentVertex.x;
                if (point.x < intersectionX) {
                    inside = !inside;
                }
            }
        }
        return inside;
    }

}
