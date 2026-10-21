package br.edu.ufersa.simplephysics.ui;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.PhysicsWorld;
import br.edu.ufersa.simplephysics.screen.SimulationInputController;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public final class SimulationButtons {
    // Button creation is provided through static methods; instances are not needed.
    private SimulationButtons() {
    }

    // Creates a toggle button that selects the square-creation tool.
    public static TextButton createSquareButton(
        Skin skin,
        SimulationInputController inputController
    ) {
        return createToolButton(
            "Create square",
            SimulationInputController.Tool.CREATE_SQUARE,
            skin,
            inputController
        );
    }

    // Creates a toggle button that selects the circle-creation tool.
    public static TextButton createCircleButton(
        Skin skin,
        SimulationInputController inputController
    ) {
        return createToolButton(
            "Create circle",
            SimulationInputController.Tool.CREATE_CIRCLE,
            skin,
            inputController
        );
    }

    // Creates a toggle button that enables or disables gravity in the physics world.
    public static TextButton createGravityButton(Skin skin, PhysicsWorld world) {
        TextButton button = createToggleButton("Gravity", skin);
        button.setChecked(true);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                world.gravity = button.isChecked()
                    ? new Vector2(0, -9.81)
                    : new Vector2(0, 0);
            }
        });
        return button;
    }

    // Creates a toggle button that selects the impulse tool.
    public static TextButton createImpulseButton(
        Skin skin,
        SimulationInputController inputController
    ) {
        return createToolButton(
            "Apply impulse",
            SimulationInputController.Tool.APPLY_IMPULSE,
            skin,
            inputController
        );
    }

    // Selects the given tool when checked and clears it only if it is still the selected tool.
    private static TextButton createToolButton(
        String label,
        SimulationInputController.Tool tool,
        Skin skin,
        SimulationInputController inputController
    ) {
        TextButton button = createToggleButton(label, skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (button.isChecked()) {
                    inputController.setSelectedTool(tool);
                } else if (inputController.getSelectedTool() == tool) {
                    inputController.setSelectedTool(SimulationInputController.Tool.NONE);
                }
            }
        });
        return button;
    }

    // Creates a button whose label reflects whether its toggle is on or off.
    private static TextButton createToggleButton(String label, Skin skin) {
        TextButton button = new TextButton(label + ": OFF", skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                button.setText(label + (button.isChecked() ? ": ON" : ": OFF"));
            }
        });
        return button;
    }
}
