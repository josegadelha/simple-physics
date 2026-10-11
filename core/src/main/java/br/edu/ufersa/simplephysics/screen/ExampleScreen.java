package br.edu.ufersa.simplephysics.screen;

import br.edu.ufersa.simplephysics.graphic.ExtendedShapeRenderer;
import br.edu.ufersa.simplephysics.graphic.GridRenderer;
import br.edu.ufersa.simplephysics.graphic.PhysicsShapeRenderer;
import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.PhysicsObject;
import br.edu.ufersa.simplephysics.physics.PhysicsWorld;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;
import br.edu.ufersa.simplephysics.physics.shape.ConvexPolygonShape;
import br.edu.ufersa.simplephysics.ui.SimplePhysicsSkin;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ExampleScreen extends ScreenAdapter {
    private static final float WORLD_UNITS_PER_PIXEL = 0.01f;
    private static final int PANEL_WIDTH = 350;
    private static final int PANEL_GAP = 24;
    private static final Color BACKGROUND_COLOR = Color.valueOf("242424");
    private static final Color PANEL_COLOR = Color.valueOf("2d2d2d");
    private static final Color SIMULATION_COLOR = Color.valueOf("fefefe");

    private final Viewport panelViewport;
    private final ScreenViewport simulationViewport;
    private final Camera panelCamera;
    private final Camera simulationCamera;

    private final ShapeRenderer shapeRenderer;
    private final ExtendedShapeRenderer extendedShapeRenderer;
    private final GridRenderer gridRenderer;

    private final PhysicsShapeRenderer physicsShapeRenderer;
    private final PhysicsWorld physicsWorld;

    private final Stage panelStage;
    private final InputMultiplexer inputMultiplexer;
    private final InputAdapter simulationInputAdapter;

    private final Skin skin;
    private final Table panelTable;
    private final TextButton squareButton;
    private final TextButton circleButton;

    public ExampleScreen() {
        panelCamera = new OrthographicCamera();
        simulationCamera = new OrthographicCamera();
        panelViewport = new ScreenViewport(panelCamera);
        simulationViewport = new ScreenViewport(simulationCamera);
        simulationViewport.setUnitsPerPixel(WORLD_UNITS_PER_PIXEL);

        shapeRenderer = new ShapeRenderer();
        extendedShapeRenderer = new ExtendedShapeRenderer();
        gridRenderer = new GridRenderer(0.5f);
        physicsShapeRenderer = new PhysicsShapeRenderer();

        physicsWorld = new PhysicsWorld();

        skin = SimplePhysicsSkin.create();
        panelStage = new Stage(panelViewport);

        simulationInputAdapter = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button != Input.Buttons.LEFT
                    || (!squareButton.isChecked() && !circleButton.isChecked())) {
                    return false;
                }

                int screenYFromBottom = Gdx.graphics.getHeight() - screenY;
                if (screenX < simulationViewport.getScreenX()
                    || screenX >= simulationViewport.getScreenX() + simulationViewport.getScreenWidth()
                    || screenYFromBottom < simulationViewport.getScreenY()
                    || screenYFromBottom >= simulationViewport.getScreenY() + simulationViewport.getScreenHeight()) {
                    return false;
                }

                com.badlogic.gdx.math.Vector2 unprojectedPosition =
                    simulationViewport.unproject(new com.badlogic.gdx.math.Vector2(screenX, screenY));
                if (squareButton.isChecked()) {
                    physicsWorld.objects.add(createSquare(unprojectedPosition.x, unprojectedPosition.y));
                } else {
                    physicsWorld.objects.add(createCircle(unprojectedPosition.x, unprojectedPosition.y));
                }
                return true;
            }
        };

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(panelStage);
        inputMultiplexer.addProcessor(simulationInputAdapter);
        Gdx.input.setInputProcessor(inputMultiplexer);

        panelTable = new Table(skin);
        panelTable.setFillParent(true);
        panelTable.top().padTop(24);
        panelStage.addActor(panelTable);

        squareButton = new TextButton("Create square: OFF", skin);
        circleButton = new TextButton("Create circle: OFF", skin);
        ButtonGroup<TextButton> shapeButtonGroup = new ButtonGroup<>();
        shapeButtonGroup.setMinCheckCount(0);
        shapeButtonGroup.setMaxCheckCount(1);
        shapeButtonGroup.add(squareButton);
        shapeButtonGroup.add(circleButton);

        squareButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                ((TextButton) actor).setText(
                    ((TextButton) actor).isChecked() ? "Create square: ON" : "Create square: OFF"
                );
            }
        });
        circleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                ((TextButton) actor).setText(
                    ((TextButton) actor).isChecked() ? "Create circle: ON" : "Create circle: OFF"
                );
            }
        });

        panelTable.add(squareButton).center().width(300).height(50);
        panelTable.row().padTop(12);
        panelTable.add(circleButton).width(300).height(50);
    }

    private PhysicsObject createSquare(float x, float y) {
        return new PhysicsObject(x, y, new ConvexPolygonShape(
            new Vector2(-0.25, -0.25),
            new Vector2(0.25, -0.25),
            new Vector2(0.25, 0.25),
            new Vector2(-0.25, 0.25)
        ));
    }

    private PhysicsObject createCircle(float x, float y) {
        return new PhysicsObject(x, y, new CircleShape(0.25));
    }

    @Override
    public void render(float delta) {
        float simulationWidth = simulationViewport.getWorldWidth();
        float simulationHeight = simulationViewport.getWorldHeight();

        ScreenUtils.clear(BACKGROUND_COLOR);

        physicsWorld.step(delta);

        panelViewport.apply();
        extendedShapeRenderer.setProjectionMatrix(panelCamera.combined);
        extendedShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        extendedShapeRenderer.setColor(PANEL_COLOR);
        extendedShapeRenderer.roundedRect(
            0,
            0,
            panelViewport.getWorldWidth(),
            panelViewport.getWorldHeight(),
            10
        );
        extendedShapeRenderer.end();
        panelStage.act();
        panelStage.draw();

        simulationViewport.apply();
        extendedShapeRenderer.setProjectionMatrix(simulationCamera.combined);
        extendedShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        extendedShapeRenderer.setColor(SIMULATION_COLOR);
        extendedShapeRenderer.roundedRect(
            -simulationWidth / 2f,
            -simulationHeight / 2f,
            simulationWidth,
            simulationHeight,
            0.1f
        );
        extendedShapeRenderer.end();

        gridRenderer.render(simulationCamera, simulationWidth, simulationHeight);
        physicsShapeRenderer.render(simulationCamera, physicsWorld);
    }

    @Override
    public void resize(int width, int height) {
        int availableHeight = height - 2 * PANEL_GAP;
        int availableWidth = width - 3 * PANEL_GAP;
        int simulationWidth = availableWidth - PANEL_WIDTH;

        panelViewport.update(PANEL_WIDTH, availableHeight, true);
        panelViewport.setScreenBounds(PANEL_GAP, PANEL_GAP, PANEL_WIDTH, availableHeight);

        simulationViewport.update(simulationWidth, availableHeight, false);
        simulationViewport.setScreenBounds(2 * PANEL_GAP + PANEL_WIDTH, PANEL_GAP, simulationWidth, availableHeight);
        simulationCamera.position.set(0, 0, 0);
        simulationCamera.update();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override public void hide() {
        if (Gdx.input.getInputProcessor() == inputMultiplexer) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        extendedShapeRenderer.dispose();
        gridRenderer.dispose();
        physicsShapeRenderer.dispose();
        panelStage.dispose();
        skin.dispose();
    }
}
