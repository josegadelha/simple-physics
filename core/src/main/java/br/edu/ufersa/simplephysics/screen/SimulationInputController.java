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

public final class SimulationInputController {
    private static final float MIN_CAMERA_ZOOM = 0.25f;
    private static final float MAX_CAMERA_ZOOM = 3f;
    private static final float ZOOM_STEP = 1.1f;

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
    private boolean applyingImpulse;
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

    public boolean isApplyingImpulse() {
        return applyingImpulse;
    }

    public Vector2 getImpulseStart() {
        return impulseStart;
    }

    public Vector2 getImpulseEnd() {
        return impulseEnd;
    }

    private InputProcessor createInputProcessor() {
        return new InputAdapter() {
            private boolean panning;
            private int lastScreenX;
            private int lastScreenY;
            private PhysicsObject impulseObject;

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.MIDDLE && isInsideViewport(screenX, screenY)) {
                    panning = true;
                    lastScreenX = screenX;
                    lastScreenY = screenY;
                    return true;
                }

                if (button != Input.Buttons.LEFT || !isInsideViewport(screenX, screenY)) {
                    return false;
                }

                Vector2 mousePosition = getMouseWorldPosition(screenX, screenY);
                if (selectedTool == Tool.APPLY_IMPULSE) {
                    impulseObject = findObjectAt(mousePosition);
                    if (impulseObject == null) {
                        return false;
                    }

                    impulseStart = mousePosition;
                    impulseEnd = mousePosition;
                    applyingImpulse = true;
                    return true;
                }

                if (selectedTool == Tool.CREATE_SQUARE) {
                    world.objects.add(createSquare(mousePosition));
                    return true;
                }
                if (selectedTool == Tool.CREATE_CIRCLE) {
                    world.objects.add(createCircle(mousePosition));
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (impulseObject != null) {
                    impulseEnd = getMouseWorldPosition(screenX, screenY);
                    return true;
                }

                if (!panning) {
                    return false;
                }

                camera.position.add(
                    -(screenX - lastScreenX) * worldUnitsPerPixel * camera.zoom,
                    (screenY - lastScreenY) * worldUnitsPerPixel * camera.zoom,
                    0
                );
                camera.update();
                lastScreenX = screenX;
                lastScreenY = screenY;
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT && impulseObject != null) {
                    impulseEnd = getMouseWorldPosition(screenX, screenY);
                    Vector2 impulse = impulseEnd.subtract(impulseStart);
                    double magnitude = impulse.length();
                    if (magnitude > 0) {
                        impulseObject.applyImpulse(impulse.scale(magnitude), impulseStart);
                    }

                    impulseObject = null;
                    impulseStart = null;
                    impulseEnd = null;
                    applyingImpulse = false;
                    return true;
                }

                if (button != Input.Buttons.MIDDLE || !panning) {
                    return false;
                }

                panning = false;
                return true;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                if (!isInsideViewport(Gdx.input.getX(), Gdx.input.getY()) || amountY == 0) {
                    return false;
                }

                float zoomFactor = (float) Math.pow(ZOOM_STEP, amountY);
                camera.zoom = Math.max(
                    MIN_CAMERA_ZOOM,
                    Math.min(MAX_CAMERA_ZOOM, camera.zoom * zoomFactor)
                );
                camera.update();
                return true;
            }
        };
    }

    private boolean isInsideViewport(int screenX, int screenY) {
        int screenYFromBottom = Gdx.graphics.getHeight() - screenY;
        return screenX >= viewport.getScreenX()
            && screenX < viewport.getScreenX() + viewport.getScreenWidth()
            && screenYFromBottom >= viewport.getScreenY()
            && screenYFromBottom < viewport.getScreenY() + viewport.getScreenHeight();
    }

    private Vector2 getMouseWorldPosition(int screenX, int screenY) {
        com.badlogic.gdx.math.Vector2 position =
            viewport.unproject(new com.badlogic.gdx.math.Vector2(screenX, screenY));
        return new Vector2(position.x, position.y);
    }

    private PhysicsObject findObjectAt(Vector2 worldPosition) {
        for (int i = world.objects.size() - 1; i >= 0; i--) {
            PhysicsObject object = world.objects.get(i);
            Vector2 localPosition = object.worldToLocal(worldPosition);

            for (PhysicsShape shape : object.shapes) {
                if (shape instanceof CircleShape) {
                    CircleShape circle = (CircleShape) shape;
                    if (localPosition.subtract(circle.offset).length() <= circle.radius) {
                        return object;
                    }
                } else if (shape instanceof ConvexPolygonShape
                    && containsPoint((ConvexPolygonShape) shape, localPosition)) {
                    return object;
                }
            }
        }
        return null;
    }

    private boolean containsPoint(ConvexPolygonShape polygon, Vector2 point) {
        boolean inside = false;
        int count = polygon.vertices.size();

        for (int i = 0, j = count - 1; i < count; j = i++) {
            Vector2 a = polygon.vertices.get(i);
            Vector2 b = polygon.vertices.get(j);
            if ((a.y > point.y) != (b.y > point.y)) {
                double intersectionX = (b.x - a.x) * (point.y - a.y) / (b.y - a.y) + a.x;
                if (point.x < intersectionX) {
                    inside = !inside;
                }
            }
        }
        return inside;
    }

    private PhysicsObject createSquare(Vector2 position) {
        return new PhysicsObject(position.x, position.y, new ConvexPolygonShape(
            new Vector2(-0.25, -0.25),
            new Vector2(0.25, -0.25),
            new Vector2(0.25, 0.25),
            new Vector2(-0.25, 0.25)
        ));
    }

    private PhysicsObject createCircle(Vector2 position) {
        return new PhysicsObject(position.x, position.y, new CircleShape(0.25));
    }
}
