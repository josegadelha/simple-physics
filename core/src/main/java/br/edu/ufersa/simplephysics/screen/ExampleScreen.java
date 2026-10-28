package br.edu.ufersa.simplephysics.screen;

import br.edu.ufersa.simplephysics.graphic.ExtendedShapeRenderer;
import br.edu.ufersa.simplephysics.graphic.GridRenderer;
import br.edu.ufersa.simplephysics.graphic.ImpulseArrowRenderer;
import br.edu.ufersa.simplephysics.graphic.ConstraintRenderer;
import br.edu.ufersa.simplephysics.graphic.PhysicsShapeRenderer;
import br.edu.ufersa.simplephysics.physics.PhysicsWorld;
import br.edu.ufersa.simplephysics.ui.ScrollablePanel;
import br.edu.ufersa.simplephysics.ui.SimplePhysicsSkin;
import br.edu.ufersa.simplephysics.ui.SimulationButtons;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
    private final OrthographicCamera simulationCamera;

    private final ExtendedShapeRenderer extendedShapeRenderer;
    private final GridRenderer gridRenderer;
    private final ImpulseArrowRenderer impulseArrowRenderer;
    private final ConstraintRenderer constraintRenderer;

    private final PhysicsShapeRenderer physicsShapeRenderer;
    private final PhysicsWorld physicsWorld;

    private final Stage panelStage;
    private final InputMultiplexer inputMultiplexer;
    private final SimulationInputController simulationInputController;

    private final Skin skin;
    private final Table panelTable;
    private final Table shapeOptionsTable;
    private final Table physicsOptionsTable;
    private final Table cameraOptionsTable;

    private final TextButton squareButton;
    private final TextButton circleButton;
    private final TextButton dragObjectButton;
    private final TextButton staticButton;
    private final TextButton gravityButton;
    private final TextButton velocityReductionButton;
    private final TextButton impulseButton;
    private final TextButton revoluteConstraintButton;
    private final TextButton impulseScalingButton;
    private final TextButton followObjectButton;
    private final TextButton resetCameraButton;
    private final TextButton collisionButton;

    public ExampleScreen() {
        panelCamera = new OrthographicCamera();
        simulationCamera = new OrthographicCamera();
        panelViewport = new ScreenViewport(panelCamera);
        simulationViewport = new ScreenViewport(simulationCamera);
        simulationViewport.setUnitsPerPixel(WORLD_UNITS_PER_PIXEL);

        extendedShapeRenderer = new ExtendedShapeRenderer();
        gridRenderer = new GridRenderer(1f);
        impulseArrowRenderer = new ImpulseArrowRenderer();
        constraintRenderer = new ConstraintRenderer();
        physicsShapeRenderer = new PhysicsShapeRenderer();

        physicsWorld = new PhysicsWorld();

        skin = SimplePhysicsSkin.create();
        panelStage = new Stage(panelViewport);

        simulationInputController =
            new SimulationInputController(
                simulationViewport,
                simulationCamera,
                physicsWorld,
                WORLD_UNITS_PER_PIXEL
            );

        panelTable = new Table(skin);
        panelTable.top();
        Table panelLayout = new Table(skin);
        panelLayout.setFillParent(true);
        panelStage.addActor(panelLayout);

        ScrollablePanel settingsPanel = new ScrollablePanel(
            panelStage,
            panelViewport,
            panelTable,
            skin,
            simulationInputController.getInputProcessor()
        );
        panelLayout.add(settingsPanel.getScrollPane())
            .width(PANEL_WIDTH)
            .expandY()
            .fillY()
            .padTop(24)
            .padBottom(24);

        squareButton = SimulationButtons.createSquareButton(skin, simulationInputController);
        circleButton = SimulationButtons.createCircleButton(skin, simulationInputController);
        dragObjectButton = SimulationButtons.createDragObjectButton(skin, simulationInputController);
        staticButton = SimulationButtons.createStaticButton(skin, simulationInputController);
        gravityButton = SimulationButtons.createGravityButton(skin, physicsWorld);
        velocityReductionButton = SimulationButtons.createVelocityReductionButton(skin, physicsWorld);
        impulseButton = SimulationButtons.createImpulseButton(skin, simulationInputController);
        revoluteConstraintButton = SimulationButtons.createRevoluteConstraintButton(skin, simulationInputController);
        impulseScalingButton = SimulationButtons.createImpulseScalingButton(skin, simulationInputController);
        collisionButton = SimulationButtons.createCollisionButton(skin, physicsWorld);
        followObjectButton = SimulationButtons.createFollowObjectButton(skin, simulationInputController);
        resetCameraButton = SimulationButtons.createResetCameraButton(skin, simulationInputController);
        Table shapeSizeControl = SimulationButtons.createShapeSizeControl(
            skin,
            squareButton,
            circleButton,
            simulationInputController
        );
        SimulationButtons.configureSimulationToolGroup(
            followObjectButton,
            dragObjectButton,
            squareButton,
            circleButton,
            impulseButton,
            revoluteConstraintButton
        );

        shapeOptionsTable = new Table(skin);
        Label shapeTitle = new Label("Object Settings", skin);
        shapeOptionsTable.add(shapeTitle).center();
        shapeOptionsTable.row().padTop(12);
        shapeOptionsTable.add(shapeSizeControl).width(300).height(50);
        shapeOptionsTable.row().padTop(12);
        shapeOptionsTable.add(squareButton).center().width(300).height(50);
        shapeOptionsTable.row().padTop(12);
        shapeOptionsTable.add(circleButton).width(300).height(50);
        shapeOptionsTable.row().padTop(12);
        shapeOptionsTable.add(staticButton).width(300).height(50);

        physicsOptionsTable = new Table(skin);
        Label physicsTitle = new Label("Physics Settings", skin);
        physicsOptionsTable.add(physicsTitle).center();
        physicsOptionsTable.row().padTop(12);
        physicsOptionsTable.add(gravityButton).width(300).height(50);
        physicsOptionsTable.row().padTop(12);
        physicsOptionsTable.add(collisionButton).width(300).height(50);
        physicsOptionsTable.row().padTop(12);
        physicsOptionsTable.add(velocityReductionButton).width(300).height(50);
        physicsOptionsTable.row().padTop(12);
        physicsOptionsTable.add(impulseButton).width(300).height(50);
        physicsOptionsTable.row().padTop(12);
        physicsOptionsTable.add(impulseScalingButton).width(300).height(50);
        physicsOptionsTable.row().padTop(12);
        physicsOptionsTable.add(dragObjectButton).width(300).height(50);
        physicsOptionsTable.row().padTop(12);
        physicsOptionsTable.add(revoluteConstraintButton).width(300).height(50);

        cameraOptionsTable = new Table(skin);
        Label cameraTitle = new Label("Camera Settings", skin);
        cameraOptionsTable.add(cameraTitle).center();
        cameraOptionsTable.row().padTop(12);
        cameraOptionsTable.add(followObjectButton).width(300).height(50);
        cameraOptionsTable.row().padTop(12);
        cameraOptionsTable.add(resetCameraButton).width(300).height(50);

        panelTable.add(shapeOptionsTable).width(300).height(310);
        panelTable.row().padTop(12);
        panelTable.add(physicsOptionsTable).width(300).height(460);
        panelTable.row().padTop(12);
        panelTable.add(cameraOptionsTable).width(300).height(172);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(settingsPanel.getInputProcessor());
        inputMultiplexer.addProcessor(panelStage);
        inputMultiplexer.addProcessor(simulationInputController.getInputProcessor());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        float simulationWidth = simulationViewport.getWorldWidth() * simulationCamera.zoom;
        float simulationHeight = simulationViewport.getWorldHeight() * simulationCamera.zoom;

        ScreenUtils.clear(BACKGROUND_COLOR);

        if (!simulationInputController.isApplyingImpulse()) {
            physicsWorld.step(delta);
        }
        simulationInputController.updateCameraFollow();
        SimulationButtons.updateDisabledButtons(
            new TextButton[] { followObjectButton },
            new TextButton[] {
                squareButton,
                circleButton,
                dragObjectButton,
                impulseButton,
                revoluteConstraintButton
            },
            simulationInputController.isWaitingForFollowTarget()
        );
        SimulationButtons.updateFollowObjectButtonLabel(
            followObjectButton,
            simulationInputController
        );

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
            simulationCamera.position.x - simulationWidth / 2f,
            simulationCamera.position.y - simulationHeight / 2f,
            simulationWidth,
            simulationHeight,
            0.1f * simulationCamera.zoom
        );
        extendedShapeRenderer.end();

        gridRenderer.render(simulationCamera, simulationWidth, simulationHeight);
        physicsShapeRenderer.render(simulationCamera, physicsWorld);
        constraintRenderer.render(
            simulationCamera,
            physicsWorld,
            simulationInputController.getRevoluteDragStart(),
            simulationInputController.getRevoluteDragEnd()
        );
        impulseArrowRenderer.render(
            simulationCamera,
            simulationInputController.getImpulseStart(),
            simulationInputController.getImpulseEnd()
        );
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
        extendedShapeRenderer.dispose();
        gridRenderer.dispose();
        impulseArrowRenderer.dispose();
        constraintRenderer.dispose();
        physicsShapeRenderer.dispose();
        panelStage.dispose();
        skin.dispose();
    }
}
